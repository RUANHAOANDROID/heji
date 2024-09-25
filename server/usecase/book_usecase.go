package usecase

import (
	"context"
	"errors"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"heji-server/domain"
	"time"
)

type bookUseCase struct {
	repository       domain.BookRepository
	sharedRepository domain.SharedRepository
}

func (b bookUseCase) BookList(c context.Context, userId string) (*[]domain.Book, error) {
	return b.repository.List(c, userId)
}

func (b bookUseCase) CreateBook(c context.Context, book *domain.Book) error {
	return b.repository.CreateOne(c, book)
}

func (b bookUseCase) DeleteBook(c context.Context, bookId string) error {
	oid, err := primitive.ObjectIDFromHex(bookId)
	if err != nil {
		return err
	}
	return b.repository.Delete(c, oid)
}

func (b bookUseCase) JoinBook(c context.Context, code string, userId string) error {
	//b.repository.JoinBook(c, code)
	bookId, err := b.sharedRepository.FindBookId(c, code)
	if err != nil {
		errors.New("加入账本失败,邀请码不存在或已过期！")
	}
	return b.repository.AddBookUser(c, bookId, userId)
}

func (b bookUseCase) UpdateBook(c context.Context, book *domain.Book) error {
	//TODO implement me
	panic("implement me")
}

func (b bookUseCase) SharedBook(c context.Context, bookId string) (string, error) {
	hexID, err := primitive.ObjectIDFromHex(bookId)
	if err != nil {
		return "", errors.New("book_id Param Error")
	}
	_, err = b.repository.FindOne(c, hexID)
	if err != nil {
		return "", errors.New("账本不存在或未同步")
	}
	return b.sharedRepository.CreateOne(c, bookId)
}

func (b bookUseCase) Create(c context.Context, book *domain.Book) error {
	//TODO implement me
	panic("implement me")
}

func NewBookUseCase(br domain.BookRepository, sr domain.SharedRepository, timeout time.Duration) domain.BookUseCase {
	return &bookUseCase{br, sr}
}
