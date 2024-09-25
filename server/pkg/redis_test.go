package pkg

import (
	"testing"
	"time"
)

func TestConnect(t *testing.T) {
	err := InitializeRedisClient("192.168.8.6:6379", "redis123", 1)
	if err != nil {
		t.Error(err)
	}
	defer CloseRedisConnection()
	err = StoreJWTToken("hao88", "TOKEN", time.Hour)
	if err != nil {
		t.Error(err)
	}
	token, err := RetrieveJWTToken("hao88")
	if err != nil {
		t.Error(err)
	}
	t.Log(token)
}
