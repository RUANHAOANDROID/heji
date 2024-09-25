package repository

import (
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"golang.org/x/net/context"
	"heji-server/domain"
	"heji-server/mongo"
	"math/rand"
	"time"
)

type sharedRepository struct {
	database   mongo.Database
	collection string
}

func randomCode() string {
	// 定义字符集
	charSet := "abcdefghkmnpqrstuvwyz23456789"
	charSetLength := len(charSet)

	// 生成四位随机字符
	var result string
	for i := 0; i < 4; i++ {
		randomIndex := rand.Intn(charSetLength)
		result += string(charSet[randomIndex])
	}
	return result
}

var shared domain.Shared

func (s sharedRepository) CreateOne(c context.Context, bookId string) (string, error) {
	coll := s.database.Collection(s.collection)
	shared := domain.Shared{
		Code:       randomCode(),
		BookId:     bookId,
		ExpireTime: primitive.NewDateTimeFromTime(time.Now().Add(5 * time.Minute)),
	}
	_, err := coll.InsertOne(c, shared)
	return shared.Code, err
}

func (s sharedRepository) FindBookId(c context.Context, code string) (string, error) {
	coll := s.database.Collection(s.collection)
	filter := bson.M{shared.TagsBson().Code: code}
	err := coll.FindOne(c, filter).Decode(&shared)
	return shared.BookId, err
}

func NewSharedRepository(db mongo.Database, coll string) domain.SharedRepository {
	return &sharedRepository{
		database:   db,
		collection: coll,
	}
}
