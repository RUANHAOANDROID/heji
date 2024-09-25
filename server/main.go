package main

import (
	"heji-server/cmd"
	"heji-server/config"
	"os"
)

func main() {
	conf, err := config.Load("config.yml")
	if err != nil {
		panic(err)
	}
	cmd.Main(os.Args, conf)
}
