package cmd

import (
	"context"
	"fmt"
	"heji-server/config"
	"heji-server/mongo"
	"time"
)

func NewMongoDatabase(conf *config.Config) mongo.Client {
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	dbHost := conf.Mongo.Host
	dbPort := conf.Mongo.Port
	dbUser := conf.Mongo.Username
	dbPass := conf.Mongo.Password
	mongodbURI := fmt.Sprintf("mongodb://%s:%s@%s:%s", dbUser, dbPass, dbHost, dbPort)

	if dbUser == "" || dbPass == "" {
		mongodbURI = fmt.Sprintf("mongodb://%s:%s", dbHost, dbPort)
	}

	client, err := mongo.NewClient(mongodbURI)
	if err != nil {
		log.Fatal(err)
	}

	err = client.Connect(ctx)
	if err != nil {
		log.Fatal(err)
	}

	//err = client.Ping(ctx)
	//if err != nil {
	//	log.Fatal(err)
	//}

	return client
}

func CloseMongoDBConnection(client mongo.Client) {
	if client == nil {
		return
	}

	err := client.Disconnect(context.TODO())
	if err != nil {
		log.Fatal(err)
	}

	log.Println("Connection to MongoDB closed.")
}
