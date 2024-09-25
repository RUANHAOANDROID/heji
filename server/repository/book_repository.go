package repository

import (
	"context"
	"errors"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo/options"
	"heji-server/domain"
	"heji-server/mongo"
)

var book domain.Book

// 账本存储库
type bookRepository struct {
	database   mongo.Database
	collection string
}

func (b bookRepository) Delete(c context.Context, id primitive.ObjectID) error {
	_, err := b.database.Collection(b.collection).DeleteOne(c, bson.M{bill.TagsBson().ID: id})
	return err
}

func (b bookRepository) AddBookUser(c context.Context, bookId string, userId string) error {
	coll := b.database.Collection(b.collection)
	oid, err := primitive.ObjectIDFromHex(bookId)
	if err != nil {
		return err
	}
	filter := bson.M{book.TagsBson().ID: oid}
	err = coll.FindOne(c, filter).Decode(&book)
	if err != nil {
		return err
	}
	book.Users = append(book.Users, userId)
	updateOptions := options.Update().SetUpsert(true)
	update := bson.D{
		{"$set", bson.D{
			{"users", book.Users},
		}},
	}
	_, err = coll.UpdateOne(c, filter, update, updateOptions)
	return err
}

func (b bookRepository) FindInitialBook(c context.Context, tel string) (domain.Book, error) {
	coll := b.database.Collection(b.collection)
	filter := bson.M{book.TagsBson().IsInitial: tel, "is_initial": true}
	var book domain.Book
	err := coll.FindOne(c, filter).Decode(&book)
	return book, err
}

func (b bookRepository) FindOne(c context.Context, id primitive.ObjectID) (domain.Book, error) {
	coll := b.database.Collection(b.collection)
	filter := bson.M{"_id": id}
	var book domain.Book
	err := coll.FindOne(c, filter).Decode(&book)
	return book, err
}

func (b bookRepository) List(c context.Context, userId string) (*[]domain.Book, error) {
	coll := b.database.Collection(domain.CollBook)
	tags := book.TagsBson()
	filter := bson.M{"$or": bson.A{bson.M{tags.CrtUserId: userId}, bson.M{tags.Users: userId}}}
	cursor, err := coll.Find(c, filter)
	if err != nil {
		return nil, err
	}
	var books []domain.Book
	err = cursor.All(c, &books)
	return &books, err
}

func (b bookRepository) Update(c context.Context, book *domain.Book) (*domain.Book, error) {
	//TODO implement me
	panic("implement me")
}

func (b bookRepository) CreateOne(c context.Context, book *domain.Book) error {
	if book.IsInitial {
		initBook, err := b.FindInitialBook(c, book.CrtUserId)
		if err == nil && initBook.IsInitial == book.IsInitial {
			return errors.New("已存在初始账本")
		}
	}
	one, err := b.FindOne(c, book.ID)
	if err == nil && (book.ID == one.ID) {
		return errors.New("账本已存在！")
	}
	coll := b.database.Collection(domain.CollBook)
	_, err = coll.InsertOne(c, book)
	return err
}

func NewBookRepository(db mongo.Database, coll string) domain.BookRepository {
	return &bookRepository{
		database:   db,
		collection: coll,
	}
}
