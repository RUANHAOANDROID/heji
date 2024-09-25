package domain

import (
	"go.mongodb.org/mongo-driver/bson/primitive"
	"golang.org/x/net/context"
)

type Files struct {
	ID         primitive.ObjectID `bson:"_id,omitempty"`
	BillId     string             `bson:"bill_id"json:"bill_id"`
	FileName   int64              `bson:"file_name"json:"file_name"`
	FilePath   string             `bson:"file_path"json:"file_path"`
	Length     float64            `bson:"length"json:"length"`
	MD5        string             `bson:"md5" json:"md5"`
	UploadTime string             `bson:"upload_time" json:"upload_time"`
	Ext        string             `bson:"ext"json:"ext"`
}
type FilesUseCase interface {
	SaveBill(c context.Context, book *Book) error
	BillList(c context.Context, book *[]Book) error
	DeleteBill(c context.Context, bookId string) error
	UpdateBill(c context.Context, book *Book) error
}

type FilesRepository interface {
	Upload(c context.Context, book *Book) error
	Delete(c context.Context, bid string) error
	List(c context.Context, userId string) (*[]Book, error)
	Download(c context.Context, book *Book) (*Book, error)
}
