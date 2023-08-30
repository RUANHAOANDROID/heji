package main

import (
	"syncserver/api"
	"syncserver/config"
	"syncserver/pkg"
)

func main() {
	conf, err := config.Load("config.yml")
	if err != nil {
		pkg.Log.Error(err.Error())
		panic(err)
	}
	api.Start(conf)
}
