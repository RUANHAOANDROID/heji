package controller

import (
	"errors"
	"fmt"
	"github.com/gin-gonic/gin"
	"github.com/gorilla/websocket"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"google.golang.org/protobuf/proto"
	"heji-server/api/ws"
	"heji-server/api/ws/handler"
	"heji-server/domain"
	"heji-server/pkg"
	"heji-server/wsmsg"
	"net/http"
	"sync"
	"time"
)

// WSController websocket 处理
type WSController struct {
	MessageUseCase domain.MessageUseCase
	BillUseCase    domain.BillUseCase
	BookUseCase    domain.BookUseCase
}

var (
	upgrader = websocket.Upgrader{
		ReadBufferSize:  1024,
		WriteBufferSize: 1024,
		CheckOrigin: func(r *http.Request) bool {
			return true
		},
	}
	clients      = make(map[string]*websocket.Conn)
	clientsMutex sync.Mutex
)

func (wsc *WSController) Upgrade(ctx *gin.Context) {
	userID := getUserId(ctx)
	if userID == "" {
		ctx.AbortWithStatusJSON(http.StatusBadRequest, gin.H{"Error": "User ID is required"})
		return
	}
	fmt.Printf("[websocket] conn user %s \n", userID)
	w := ctx.Writer
	r := ctx.Request
	conn, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		pkg.Log.Error("upgrade:", err)
		return
	}
	defer conn.Close()
	addConn(conn, userID)
	// 监听正常关闭
	conn.SetCloseHandler(func(code int, text string) error {
		pkg.Log.Println("[websocket]close connection")
		conn.CloseHandler()
		conn.Close()
		err = errors.New(text)
		removeConn(userID)
		return err
	})
	conn.SetPongHandler(func(appData string) error {
		fmt.Printf("[websocket] pong received:%s\n", appData)
		addConn(conn, userID)
		return nil
	})
	go func() {
		for {
			time.Sleep(30 * time.Second)
			fmt.Printf("[websocket] ping %s,%s\n", userID, conn.RemoteAddr())
			err = conn.WriteMessage(websocket.PingMessage, nil)
			if err != nil {
				pkg.Log.Error("Error sending ping:", err)
				conn.Close()
				removeConn(userID)
				return
			}
		}
	}()
	//go wsWriter(ws, &writeMutex, connId)
	msgChan := make(chan *wsmsg.Packet)
	go wsc.processor(msgChan, ctx, conn)

	for {
		//解析消息 msgType 为传输类型
		msgType, p, err := conn.ReadMessage()
		if err != nil {
			if websocket.IsCloseError(err, websocket.CloseNormalClosure, websocket.CloseGoingAway) {
				pkg.Log.Printf("[websocket]链接关闭 %v|msgType=%d|user=%s,", err, msgType, userID)
				removeConn(userID)
				return
			} else {
				pkg.Log.Printf("websocket]链接异常关闭 %v|msgType=%d|user=%s,", err, msgType, userID)
				removeConn(userID)
				return
			}
		}
		var msg wsmsg.Packet
		if err := proto.Unmarshal(p, &msg); err != nil {
			pkg.Log.Error("Error decoding Proto message:", err)
			break
		}
		fmt.Printf("[websocket] msg <- %v\n", msg.String())
		msgChan <- &msg
	}
}

func addConn(conn *websocket.Conn, userID string) {
	clientsMutex.Lock()
	if _, ok := clients[userID]; ok {
		delete(clients, userID)
	}
	clients[userID] = conn
	fmt.Printf("[websocket] add user %s,%v\n", userID, conn.RemoteAddr())
	fmt.Printf("[websocket] conn count %d\n", len(clients))
	for k, v := range clients {
		fmt.Printf("[%s,%v] ", k, v.NetConn().RemoteAddr())
	}
	clientsMutex.Unlock()
}
func removeConn(uid string) {
	clientsMutex.Lock()
	delete(clients, uid)
	clientsMutex.Unlock()
	fmt.Printf("[websocket] Connections %d\n", len(clients))
}

func pushMessageToUser(uid string, msg *wsmsg.Packet) {
	clientsMutex.Lock()
	defer clientsMutex.Unlock()
	//遍历连接池
	for key, conn := range clients {
		if msg.SenderId == uid {
			continue
		}
		if key == uid {
			bytes, err := ws.SerializeMessage(msg)
			if err != nil {
				pkg.Log.Error(err)
				return
			}
			err = conn.WriteMessage(websocket.PingMessage, bytes)
			if err != nil {
				pkg.Log.Error(err)
			}
		}
	}
}

func broadcastMessage(message map[string]interface{}) {
	clientsMutex.Lock()
	defer clientsMutex.Unlock()
	for uid, client := range clients {
		fmt.Println(uid)
		err := client.WriteJSON(message)
		if err != nil {
			pkg.Log.Error(err)
		}
	}
}

// processor 处理消息的 Goroutine
func (wsc *WSController) processor(packet <-chan *wsmsg.Packet, ctx *gin.Context, conn *websocket.Conn) {
	for msg := range packet {
		oid, err := primitive.ObjectIDFromHex(msg.Id)
		if err != nil {
			pkg.Log.Error(err)
		}
		h := GetHandler(msg.Type, wsc)
		fmt.Printf("[websocket] msgType=%v handle=%v\n", msg.Type, h)
		if h != nil {
			h.HandleMessage(msg, ctx, conn)
		}
		//多人消息先存储后转发,单人账本不需要存储消息仅仅备份账本到mongo
		//消息存储后根据ACK来移除消息人员列表,推送到列表人员根据ACK消费删除直至删除
		if len(msg.GetReceiverIds()) > 0 {
			fmt.Printf("[websocket] message to users [%v]\n", msg.GetReceiverIds())
			message := &domain.Message{
				ID:          oid,
				Type:        msg.Type.String(),
				Timestamp:   msg.Timestamp,
				SenderId:    msg.SenderId,
				ReceiverIds: msg.ReceiverIds,
				Content:     msg.Content,
			}
			err = wsc.MessageUseCase.SaveMessage(ctx, message)
			if err != nil {
				pkg.Log.Error(err)
				return
			}
			for _, uid := range msg.GetReceiverIds() {
				fmt.Printf("[websocket] message to users [%v]\n", uid)
				pushMessageToUser(uid, msg)
			}
		}
	}
}

// handlerCreator 函数类型，用于创建消息处理器
type handlerCreator func(c *WSController) ws.MessageHandler

// handlerMap 映射消息类型到创建处理器的函数
var handlerMap = map[wsmsg.Type]handlerCreator{
	wsmsg.Type_ADD_BILL: func(c *WSController) ws.MessageHandler {
		return &handler.AddBillHandler{BillUseCase: c.BillUseCase}
	},
	wsmsg.Type_ADD_BILL_ACK: func(c *WSController) ws.MessageHandler {
		return &handler.AddBillAckHandler{BillUseCase: c.BillUseCase}
	},
	wsmsg.Type_DELETE_BILL: func(c *WSController) ws.MessageHandler {
		return &handler.DeleteBillHandler{BillUseCase: c.BillUseCase}
	},
	wsmsg.Type_DELETE_BILL_ACK: func(c *WSController) ws.MessageHandler {
		return &handler.DeleteBillAckHandler{BillUseCase: c.BillUseCase}
	},
	wsmsg.Type_UPDATE_BILL: func(c *WSController) ws.MessageHandler {
		return &handler.DeleteBillHandler{BillUseCase: c.BillUseCase}
	},
	wsmsg.Type_UPDATE_BILL_ACK: func(c *WSController) ws.MessageHandler {
		return &handler.DeleteBillAckHandler{BillUseCase: c.BillUseCase}
	},
	wsmsg.Type_ADD_BOOK: func(c *WSController) ws.MessageHandler {
		return &handler.AddBookHandler{BookUserCase: c.BookUseCase}
	},
	wsmsg.Type_ADD_BOOK_ACK: func(c *WSController) ws.MessageHandler {
		return &handler.AddBookHandler{BookUserCase: c.BookUseCase}
	},
	wsmsg.Type_UPDATE_BOOK: func(c *WSController) ws.MessageHandler {
		return &handler.AddBookHandler{BookUserCase: c.BookUseCase}
	},
	wsmsg.Type_UPDATE_BOOK_ACK: func(c *WSController) ws.MessageHandler {
		return &handler.AddBookHandler{BookUserCase: c.BookUseCase}
	},
	wsmsg.Type_DELETE_BOOK: func(c *WSController) ws.MessageHandler {
		return &handler.DeleteBillHandler{BillUseCase: c.BillUseCase}
	},
	wsmsg.Type_DELETE_BOOK_ACK: func(c *WSController) ws.MessageHandler {
		return &handler.DeleteBillHandler{BillUseCase: c.BillUseCase}
	},
}

// GetHandler 返回对应消息类型的处理器
func GetHandler(packetType wsmsg.Type, c *WSController) ws.MessageHandler {
	if creator, ok := handlerMap[packetType]; ok {
		return creator(c)
	}
	return nil
}
