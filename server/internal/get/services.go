package get

import (
	"heji-server/config"
)

var conf *config.Config

func SetConfig(c *config.Config) {
	if c == nil {
		panic("config is nil")
	}
	conf = c
}
func Config() *config.Config {
	if conf == nil {
		panic("config is nil")
	}
	return conf
}

//func MongoTimeOutMax() time.Duration {
//	return Config().Mongo.TimeoutMax
//}

func Jwt() config.Jwt {
	return Config().Jwt
}

func Options() config.Options {
	return Config().Options
}
