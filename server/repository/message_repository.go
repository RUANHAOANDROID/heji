package repository

import (
	"context"
	"go.mongodb.org/mongo-driver/bson"
	"heji-server/domain"
	"heji-server/mongo"
)

var message domain.Message

type messageRepository struct {
	database   mongo.Database
	collection string
}

func (m messageRepository) SaveMessage(c context.Context, msg *domain.Message) error {
	collection := m.database.Collection(m.collection)
	_, err := collection.InsertOne(c, msg)
	return err
}

func (m messageRepository) RemoveConsumer(c context.Context, msgId, consumerId string) error {
	collection := m.database.Collection(m.collection)
	filter := bson.M{message.TagsBson().ID: msgId}
	update := bson.M{"$pull": bson.M{message.TagsBson().ReceiverIds: consumerId}}
	_, err := collection.UpdateOne(c, filter, update)
	if err != nil {
		return err
	}
	filter = bson.M{message.TagsBson().ID: bson.M{message.TagsBson().ReceiverIds: 0}}
	_, err = collection.DeleteOne(c, filter)
	return err
}

func NewMessagesRepository(db mongo.Database, coll string) domain.MessageRepository {
	return &messageRepository{
		database:   db,
		collection: coll,
	}
}
