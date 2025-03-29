package main

import (
	"github.com/getsentry/sentry-go"
	"heji-server/cmd"
	"heji-server/config"
	"log"
	"os"
	"time"
)

func main() {
	err := sentry.Init(sentry.ClientOptions{
		Dsn: "https://baac970e1a9fe7fb98b22030d061988c@o4508631282155520.ingest.us.sentry.io/4509059627417600",
	})
	if err != nil {
		log.Fatalf("sentry.Init: %s", err)
	}
	// Flush buffered events before the program terminates.
	defer sentry.Flush(2 * time.Second)

	sentry.CaptureMessage("It works!")

	conf, err := config.Load("config.yml")
	if err != nil {
		panic(err)
	}
	cmd.Main(os.Args, conf)
}
