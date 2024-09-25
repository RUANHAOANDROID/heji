package api

import (
	"github.com/gin-gonic/gin"
	"heji-server/api/middleware"
	"heji-server/config"
	"heji-server/mongo"
	"heji-server/pkg"
)

var log = pkg.Log

var APIv1 *gin.RouterGroup

func Setup(conf *config.Config, db mongo.Database) {
	router := gin.Default()
	trustedProxies := []string{
		"127.0.0.1",
		//config.Localhost,
	}
	if err := router.SetTrustedProxies(trustedProxies); err != nil {
		log.Warnf("server: %s", err)
	}
	//开启重定向
	router.RedirectTrailingSlash = true
	router.Use(middleware.Cors())
	router.Use(middleware.ErrorHandler())
	router.Use(middleware.JwtAuth(conf.Jwt.Secret))
	router.Use(gin.Recovery())
	APIv1 = router.Group(config.ApiUri)
	APIv1.Use(middleware.LoggerMiddleware())
	RegisterRoutes(conf, db)
	router.Run(":8888")
}
