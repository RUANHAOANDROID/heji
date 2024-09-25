package domain

import (
	"context"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

//var (
//	Types = flag.String("types", "", "struct types")
//	Tags  = flag.String("tags", "", "preset tags")
//)

const (
	CollBook = "books" //mongo collection users
)

//go:generate go run github.com/wolfogre/gtag/cmd/gtag -types Book -tags bson .
type Book struct {
	ID        primitive.ObjectID `bson:"_id,omitempty" json:"_id"`
	Name      string             `bson:"name" json:"name"`
	Type      string             `bson:"type" json:"type"`
	Banner    string             `bson:"banner" json:"banner"`
	CrtUserId string             `bson:"crt_user_id" json:"crt_user_id"`
	CrtTime   int64              `bson:"crt_time" json:"crt_time"`
	UpdTime   int64              `bson:"upd_time" json:"upd_time"`
	Users     []string           `bson:"users" json:"users"`
	IsInitial bool               `bson:"is_initial" json:"is_initial"`
}

type BookUseCase interface {
	CreateBook(c context.Context, book *Book) error
	BookList(c context.Context, userId string) (*[]Book, error)
	DeleteBook(c context.Context, bookId string) error
	JoinBook(c context.Context, code string, userId string) error
	UpdateBook(c context.Context, book *Book) error
	SharedBook(c context.Context, bookId string) (string, error)
}

type BookRepository interface {
	CreateOne(c context.Context, book *Book) error
	FindOne(c context.Context, id primitive.ObjectID) (Book, error)
	FindInitialBook(c context.Context, tel string) (Book, error)
	List(c context.Context, userId string) (*[]Book, error)
	Update(c context.Context, book *Book) (*Book, error)
	Delete(c context.Context, id primitive.ObjectID) error
	AddBookUser(c context.Context, bookId string, userId string) error
}
