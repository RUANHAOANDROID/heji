package repository

import (
	"context"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo/options"
	"heji-server/domain"
	"heji-server/mongo"
	"heji-server/pkg"
)

var bill domain.Bill

type billRepository struct {
	database   mongo.Database
	collection string
}

func (b billRepository) Save(c context.Context, bill *domain.Bill) error {
	filter := bson.M{bill.TagsBson().ID: bill.ID}
	cursor, err := b.database.Collection(b.collection).Find(c, filter)
	defer cursor.Close(c)
	if err != nil {
		return err
	}
	// 检查是否存在文档
	exists := cursor.Next(context.TODO())
	if exists {
		updateOptions := options.Update().SetUpsert(true)
		_, err = b.database.Collection(b.collection).UpdateOne(c, filter, bill, updateOptions)
	} else {
		_, err = b.database.Collection(b.collection).InsertOne(c, bill)
	}
	return err
}

func (b billRepository) Delete(c context.Context, bid string) error {
	objectId, err := primitive.ObjectIDFromHex(bid)
	if err != nil {
		pkg.Log.Error(err)
	}
	_, err = b.database.Collection(b.collection).DeleteOne(c, bson.M{bill.TagsBson().ID: objectId})
	return err
}

func (b billRepository) List(c context.Context, bookId string, pageNumber, pageSize int64, bills *[]domain.Bill) error {
	findOptions := options.Find().SetSkip(pageNumber).SetLimit(pageSize) // 设置查询结果限制为 10 条
	cursor, err := b.database.Collection(b.collection).Find(c, bson.M{bill.TagsBson().BookId: bookId}, findOptions)
	if err != nil {
		return err
	}
	err = cursor.Decode(&bills)
	return err
}

func (b billRepository) Update(c context.Context, bill *domain.Bill) error {
	filter := bson.M{bill.TagsBson().ID: bill.ID}
	updateOptions := options.Update().SetUpsert(true)
	_, err := b.database.Collection(b.collection).UpdateOne(c, filter, bill, updateOptions)
	return err
}

func NewBillRepository(db mongo.Database, coll string) domain.BillRepository {
	return &billRepository{
		database:   db,
		collection: coll,
	}

}
