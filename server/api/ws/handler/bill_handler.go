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

// AddBillHandler 记录账单
type AddBillHandler struct {
	BillUseCase domain.BillUseCase
}

func (h *AddBillHandler) HandleMessage(packet *wsmsg.Packet, ctx context.Context, conn *websocket.Conn) {
	if packet.Type == wsmsg.Type_ADD_BILL {
		fmt.Println("[handler]添加账单", packet.Content)
		var bill domain.Bill
		err := json.Unmarshal([]byte(packet.Content), &bill)
		//hex, err := primitive.ObjectIDFromHex(packet.Id)
		if err != nil {
			pkg.Log.Error(err)
		}
		err = h.BillUseCase.SaveBill(ctx, &bill)
		if err != nil {
			pkg.Log.Error(err)
		}
		ackPacket := &wsmsg.Packet{
			Id:      packet.Id,
			Type:    wsmsg.Type_ADD_BILL_ACK,
			Content: bill.ID.Hex(),
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

// AddBillAckHandler 记录账单 确认
type AddBillAckHandler struct {
	BillUseCase domain.BillUseCase
}

func (h *AddBillAckHandler) HandleMessage(packet *wsmsg.Packet, ctx context.Context, conn *websocket.Conn) {
	if packet.Type == wsmsg.Type_ADD_BILL_ACK {
		fmt.Println("[handler] 添加账单 确认", packet.Content)
	}
}

// DeleteBillHandler 删除账单
type DeleteBillHandler struct {
	BillUseCase domain.BillUseCase
}

func (h *DeleteBillHandler) HandleMessage(packet *wsmsg.Packet, ctx context.Context, conn *websocket.Conn) {
	if packet.Type == wsmsg.Type_DELETE_BILL {
		fmt.Println("[handler]删除账单", packet.Content)
		err := h.BillUseCase.DeleteBill(ctx, packet.Content)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		ack := &wsmsg.Packet{
			Id:      packet.Id,
			Type:    wsmsg.Type_DELETE_BILL_ACK,
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

// DeleteBillAckHandler 删除账单
type DeleteBillAckHandler struct {
	BillUseCase domain.BillUseCase
}

func (h *DeleteBillAckHandler) HandleMessage(packet *wsmsg.Packet, ctx context.Context, conn *websocket.Conn) {
	if packet.Type == wsmsg.Type_DELETE_BILL_ACK {
		fmt.Println("[handler]删除账单 ACK", packet.Content)
	}
}

// UpdateBillHandler 更新账单
type UpdateBillHandler struct {
	BillUseCase domain.BillUseCase
}

func (h *UpdateBillHandler) HandleMessage(packet *wsmsg.Packet, ctx context.Context, conn *websocket.Conn) {
	if packet.Type == wsmsg.Type_UPDATE_BILL {
		fmt.Println("[handler]更新账单", packet.Content)
		var bill domain.Bill
		err := json.Unmarshal([]byte(packet.Content), &bill)
		//hex, err := primitive.ObjectIDFromHex(packet.Id)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		err = h.BillUseCase.UpdateBill(ctx, &bill)
		if err != nil {
			pkg.Log.Error(err)
			return
		}
		ackPacket := &wsmsg.Packet{
			Id:      packet.Id,
			Type:    wsmsg.Type_UPDATE_BILL_ACK,
			Content: bill.ID.Hex(),
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

// UpdateBillAckHandler 更新账单 ACK
type UpdateBillAckHandler struct {
	BillUseCase domain.BillUseCase
}

func (h *UpdateBillAckHandler) HandleMessage(packet *wsmsg.Packet, ctx context.Context, conn *websocket.Conn) {
	if packet.Type == wsmsg.Type_UPDATE_BILL_ACK {
		fmt.Println("[handler]更新账单 ACK", packet.Content)
	}
}
