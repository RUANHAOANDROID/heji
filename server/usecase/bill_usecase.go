package usecase

import (
	"context"
	"heji-server/domain"
)

type billUseCase struct {
	repository domain.BillRepository
}

func (b billUseCase) SaveBill(c context.Context, bill *domain.Bill) error {
	return b.repository.Save(c, bill)
}

func (b billUseCase) BillList(c context.Context, bookId string, pageNumber, pageSize int64, bill *[]domain.Bill) error {
	return b.repository.List(c, bookId, pageNumber, pageSize, bill)
}

func (b billUseCase) DeleteBill(c context.Context, billId string) error {
	return b.repository.Delete(c, billId)
}

func (b billUseCase) UpdateBill(c context.Context, bill *domain.Bill) error {
	return b.repository.Update(c, bill)
}

func NewBillUseCase(br domain.BillRepository) domain.BillUseCase {
	return &billUseCase{br}
}
