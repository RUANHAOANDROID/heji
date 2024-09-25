package handler

import (
	"context"
	"encoding/json"
	"fmt"
	"github.com/gorilla/websocket"
	"heji-server/api/ws"
	"heji-server/domain"
	"heji-server/pkg"
	"heji-server/wsmsg"
)

// AddBookHandler 记录账单
type AddBookHandler struct {
	BookUserCase domain.BookUseCase
}

func (h *AddBookHandler) HandleMessage(packet *wsmsg.Packet, ctx context.Context, conn *websocket.Conn) {
	if packet.Type == wsmsg.Type_ADD_BOOK {
		fmt.Println("添加账本", packet.Content)
		var book domain.Book
		err := json.Unmarshal([]byte(packet.Content), &book)
		//hex, err := primitive.ObjectIDFromHex(packet.Id)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		err = h.BookUserCase.CreateBook(ctx, &book)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		ackPacket := &wsmsg.Packet{
			Id:      packet.Id,
			Type:    wsmsg.Type_ADD_BOOK_ACK,
			Content: book.ID.Hex(),
		}
		bytes, err := ws.SerializeMessage(ackPacket)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		err = conn.WriteMessage(websocket.BinaryMessage, bytes)
		if err != nil {
			pkg.Log.Error(err)
		}
	}
}

// AddBookAckHandler 记录账单确认
type AddBookAckHandler struct {
	BookUserCase domain.BookUseCase
}

func (h *AddBookAckHandler) HandleMessage(packet *wsmsg.Packet, ctx context.Context, conn *websocket.Conn) {
	if packet.Type == wsmsg.Type_ADD_BOOK_ACK {
		fmt.Println("添加账本-ACK", packet.Content)
	}
}

// DeleteBookHandler 删除账本
type DeleteBookHandler struct {
	BookUserCase domain.BookUseCase
}

func (h *DeleteBookHandler) HandleMessage(packet *wsmsg.Packet, ctx context.Context, conn *websocket.Conn) {
	if packet.Type == wsmsg.Type_DELETE_BOOK {
		fmt.Println("删除账本", packet.Content)
		err := h.BookUserCase.DeleteBook(ctx, packet.Content)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		ack := &wsmsg.Packet{
			Id:      packet.Id,
			Type:    wsmsg.Type_DELETE_BOOK_ACK,
			Content: packet.Content,
		}
		bytes, err := ws.SerializeMessage(ack)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		err = conn.WriteMessage(websocket.BinaryMessage, bytes)
	}
}

// DeleteBookAckHandler 删除账本
type DeleteBookAckHandler struct {
	BookUserCase domain.BookUseCase
}

func (h *DeleteBookAckHandler) HandleMessage(packet *wsmsg.Packet, ctx context.Context, conn *websocket.Conn) {
	if packet.Type == wsmsg.Type_DELETE_BOOK_ACK {
		fmt.Println("删除账本", packet.Content)
		err := h.BookUserCase.DeleteBook(ctx, packet.Content)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		ack := &wsmsg.Packet{
			Id:      packet.Id,
			Type:    wsmsg.Type_DELETE_BOOK_ACK,
			Content: packet.Content,
		}
		bytes, err := ws.SerializeMessage(ack)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		err = conn.WriteMessage(websocket.BinaryMessage, bytes)
	}
}

// UpdateBookHandler 更新账本
type UpdateBookHandler struct {
	BookUserCase domain.BookUseCase
}

func (h *UpdateBookHandler) HandleMessage(packet *wsmsg.Packet, ctx context.Context, conn *websocket.Conn) {
	if packet.Type == wsmsg.Type_UPDATE_BOOK {
		fmt.Println("更新账单", packet.Content)
		var book domain.Book
		err := json.Unmarshal([]byte(packet.Content), &book)
		//hex, err := primitive.ObjectIDFromHex(packet.Id)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		err = h.BookUserCase.UpdateBook(ctx, &book)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		ackPacket := &wsmsg.Packet{
			Id:      packet.Id,
			Type:    wsmsg.Type_UPDATE_BOOK_ACK,
			Content: book.ID.Hex(),
		}
		bytes, err := ws.SerializeMessage(ackPacket)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		err = conn.WriteMessage(websocket.BinaryMessage, bytes)
		if err != nil {
			pkg.Log.Error(err)
		}
	}
}

// UpdateBookAckHandler 更新账本确认
type UpdateBookAckHandler struct {
	BookUserCase domain.BookUseCase
}

func (h *UpdateBookAckHandler) HandleMessage(packet *wsmsg.Packet, ctx context.Context, conn *websocket.Conn) {
	if packet.Type == wsmsg.Type_UPDATE_BOOK_ACK {
		fmt.Println("更新账单", packet.Content)
		var book domain.Book
		err := json.Unmarshal([]byte(packet.Content), &book)
		//hex, err := primitive.ObjectIDFromHex(packet.Id)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		err = h.BookUserCase.UpdateBook(ctx, &book)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		ackPacket := &wsmsg.Packet{
			Id:      packet.Id,
			Type:    wsmsg.Type_UPDATE_BOOK_ACK,
			Content: book.ID.Hex(),
		}
		bytes, err := ws.SerializeMessage(ackPacket)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		err = conn.WriteMessage(websocket.BinaryMessage, bytes)
		if err != nil {
			pkg.Log.Error(err)
		}
	}
}
