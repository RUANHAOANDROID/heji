package usecase

import (
	"context"
	"heji-server/domain"
)

type messageUseCase struct {
	repository domain.MessageRepository
}

func (m messageUseCase) SaveMessage(c context.Context, msg *domain.Message) error {
	return m.repository.SaveMessage(c, msg)
}

func (m messageUseCase) ConsumeMessage(c context.Context, msgId, uid string) error {
	return m.repository.RemoveConsumer(c, msgId, uid)
}

func NewMessageUseCase(mr domain.MessageRepository) domain.MessageUseCase {
	return &messageUseCase{
		repository: mr,
	}
}
