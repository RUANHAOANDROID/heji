package api

import (
	"syncserver/api/ws"
)

func RegisterRouters() {
	Gin.Use(Cors())
	Gin.Use(ErrorHandler())
	v1 := Gin.Group("/v1")
	ws.Upgrade(v1)
}
