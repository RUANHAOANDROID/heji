package ws

import (
	"context"
	"github.com/gorilla/websocket"
	"heji-server/wsmsg"
)

type MessageHandler interface {
	HandleMessage(packet *wsmsg.Packet, ctx context.Context, conn *websocket.Conn)
}
