package ws

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"github.com/gorilla/websocket"
	"net/http"
	"sync"
)

var (
	upgrader = websocket.Upgrader{CheckOrigin: func(r *http.Request) bool {
		return true
	}} // use default options
	connections = make([]*websocket.Conn, 0)
	messageChan = make(chan []byte) // 用于接收客户端消息的通道
	mutex       sync.Mutex
)

func SendMsg(msg any) {
	if connections == nil {
		println("ws server is nil")
		return
	}
	mutex.Lock()
	for _, conn := range connections {
		fmt.Println("WebSocket msg: ", msg)
		err := conn.WriteJSON(msg)
		if err != nil {
			fmt.Println("Failed to send message:", err)
			conn.Close()
		}
	}
	mutex.Unlock()
}
func Upgrade(r *gin.RouterGroup) {
	r.GET("/flow", func(c *gin.Context) {
		wsConn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
		if err != nil {
			fmt.Print("upgrade:", err)
			return
		}
		defer func() {
			// 关闭连接并从连接列表中移除
			wsConn.Close()
			mutex.Lock()
			for i := range connections {
				if connections[i] == wsConn {
					connections = append(connections[:i], connections[i+1:]...)
					break
				}
			}
			mutex.Unlock()
		}()
		// 将连接添加到连接列表中
		mutex.Lock()
		connections = append(connections, wsConn)
		mutex.Unlock()

		go processClientMessages() // 启动处理客户端消息的goroutine

		for {
			_, message, err := wsConn.ReadMessage()
			if err != nil {
				if !websocket.IsCloseError(err, websocket.CloseNormalClosure, websocket.CloseGoingAway) {
					fmt.Println("read:", err)
				}
				break
			}

			fmt.Printf("recv: %s", message)

			// 将消息发送到处理通道
			messageChan <- message
		}
	})
}

func processClientMessages() {
	for message := range messageChan {
		msgStr := fmt.Sprintf("%s", message)
		fmt.Printf(msgStr)

	}
}
