package controller

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"heji-server/config"
)

func getUserId(c *gin.Context) string {
	value, exists := c.Get(config.AuthUserId)
	if exists {
		str, ok := value.(string)
		if !ok {
			fmt.Println("Value is not a string")
			return ""
		}
		return str
	}
	return ""
}
