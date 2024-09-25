package domain

import (
	"context"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

const (
	CollMessage = "messages" //mongo collection users
)

//go:generate go run github.com/wolfogre/gtag/cmd/gtag -types Message -tags bson .
type Message struct {
	ID          primitive.ObjectID `bson:"_id,omitempty" json:"_id"`
	Type        string             `bson:"type" json:"type"`
	Timestamp   int64              `bson:"timestamp" json:"timestamp"`
	SenderId    string             `bson:"sender_id" json:"sender_id"`
	ReceiverIds []string           `bson:"receiver_ids" json:"receiver_ids"`
	Content     string             `bson:"content" json:"content"`
}
type MessageUseCase interface {
	// SaveMessage 保存消息
	SaveMessage(c context.Context, msg *Message) error
	// ConsumeMessage 消费消息 当消费完消息删除消息
	ConsumeMessage(c context.Context, msgId, uid string) error
}
type MessageRepository interface {
	// SaveMessage 保存消息
	SaveMessage(c context.Context, msg *Message) error
	// RemoveConsumer 消费消息
	RemoveConsumer(c context.Context, msgId, consumerId string) error
}
