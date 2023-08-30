package api

import (
	"github.com/gin-gonic/gin"
	"syncserver/api/ws"
)

func RegisterRouters() {
	Gin.Use(Cors())
	Gin.Use(ErrorHandler())
	ws.Upgrade(&Gin.RouterGroup)
	api := Gin.Group("/api")
	user := api.Group("user")
	handlerUserGroup(user)
	book := api.Group("book")
	handlerBookGroup(book)
}

func handlerBookGroup(r *gin.RouterGroup) {

}

func handlerUserGroup(r *gin.RouterGroup) {

}
