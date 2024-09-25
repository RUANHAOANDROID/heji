package domain

import (
	"go.mongodb.org/mongo-driver/bson/primitive"
	"golang.org/x/net/context"
)

const (
	CollShared = "shared" //mongo collection users
)

//go:generate go run github.com/wolfogre/gtag/cmd/gtag -types Shared -tags bson .
type Shared struct {
	ID         primitive.ObjectID `bson:"_id,omitempty" json:"_id"`
	Code       string             `bson:"code" json:"code"`
	BookId     string             `bson:"book_id" json:"book_id"`
	ExpireTime primitive.DateTime `bson:"expire_time" json:"expire_time"`
}

type SharedRepository interface {
	CreateOne(c context.Context, bookId string) (string, error)
	FindBookId(c context.Context, code string) (string, error)
}
