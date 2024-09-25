package ws

import (
	"google.golang.org/protobuf/proto"
	"heji-server/wsmsg"
)

func SerializeMessage(packet *wsmsg.Packet) ([]byte, error) {
	// 序列化消息
	serializedMsg, err := proto.Marshal(packet)
	if err != nil {
		return nil, err
	}
	return serializedMsg, nil
}
