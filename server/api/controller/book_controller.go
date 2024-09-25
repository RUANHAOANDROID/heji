package controller

import (
	"github.com/gin-gonic/gin"
	"heji-server/domain"
	"heji-server/pkg"
	"net/http"
)

type BookController struct {
	UseCase domain.BookUseCase
}

func (bc *BookController) CreateBook(c *gin.Context) {
	var book domain.Book
	err := c.ShouldBind(&book)
	if err != nil {
		pkg.Log.Error(err)
		c.JSON(http.StatusBadRequest, domain.RespError(err.Error()))
		return
	}
	if book.Users == nil {
		book.Users = []string{book.CrtUserId}
	}
	err = bc.UseCase.CreateBook(c, &book)
	if err != nil {
		c.JSON(http.StatusBadRequest, domain.RespError(err.Error()))
		return
	}
	c.JSON(http.StatusOK, domain.RespSuccess(nil))
}
func (bc *BookController) BookList(c *gin.Context) {
	books, err := bc.UseCase.BookList(c, getUserId(c))
	if err != nil {
		c.JSON(http.StatusOK, domain.RespError(err.Error()))
		return
	}
	c.JSON(http.StatusOK, domain.RespSuccess(books))
}
func (bc *BookController) DeleteBook(c *gin.Context) {
	id := c.Param("bid")
	err := bc.UseCase.DeleteBook(c, id)
	if err != nil {
		c.JSON(http.StatusBadRequest, domain.RespError(err.Error()))
	}
	c.JSON(http.StatusOK, domain.RespSuccess(nil))
}
func (bc *BookController) UpdateBook(c *gin.Context) {
	var book domain.Book
	err := c.ShouldBind(&book)
	if err != nil {
		c.JSON(http.StatusBadRequest, domain.RespError(err.Error()))
	}
	err = bc.UseCase.UpdateBook(c, &book)
	if err != nil {
		c.JSON(http.StatusBadRequest, domain.RespError(err.Error()))
	}
	c.JSON(http.StatusOK, domain.RespSuccess(nil))
}
func (bc *BookController) SharedBook(c *gin.Context) {
	id := c.Param("bid")
	code, err := bc.UseCase.SharedBook(c, id)
	if err != nil {
		c.JSON(http.StatusBadRequest, domain.RespError(err.Error()))
	}
	c.JSON(http.StatusOK, domain.RespSuccess(code))
}
func (bc *BookController) JoinBook(c *gin.Context) {
	code := c.Param("code")
	userId := getUserId(c)
	err := bc.UseCase.JoinBook(c, code, userId)
	if err != nil {
		c.JSON(http.StatusBadRequest, domain.RespError(err.Error()))
	}
	c.JSON(http.StatusOK, domain.RespSuccess(nil))
}
