package domain

import "go.mongodb.org/mongo-driver/bson/primitive"

//go:generate go run github.com/wolfogre/gtag/cmd/gtag -types Category -tags bson .
type Category struct {
	ID     primitive.ObjectID `bson:"_id,omitempty"`
	BookID string             `bson:"book_id"`
	Type   int                `bson:"type"`
	Level  int                `bson:"level"`
}
