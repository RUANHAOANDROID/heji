package api

import (
	"github.com/gin-gonic/gin"
	_ "github.com/sirupsen/logrus"
	"syncserver/config"
	"syncserver/pkg"
)

var Gin *gin.Engine
var conf *config.Config

func init() {
	if Gin == nil {
		pkg.Log.Info("start web server gin init ")
		Gin = ginConfig()
	}
}
func ginConfig() *gin.Engine {
	r := gin.Default()
	Gin = r
	RegisterRouters()
	return r
}

func Start(config *config.Config) {
	conf = config
	trustedProxies := []string{
		config.Server.Host,
	}
	Gin.SetTrustedProxies(trustedProxies)
	pkg.Log.Info("http" + "://" + config.Server.Host + ":" + config.Server.Port)
	Gin.Run(":" + config.Server.Port)
}
