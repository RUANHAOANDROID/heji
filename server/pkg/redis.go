package pkg

import (
	"context"
	"fmt"
	"github.com/redis/go-redis/v9"
	"time"
)

var (
	ctx         = context.Background()
	redisClient *redis.Client
)

// InitializeRedisClient 初始化redis
func InitializeRedisClient(addr, password string, db int) error {
	redisClient = redis.NewClient(&redis.Options{
		Addr:     addr,
		Password: password,
		DB:       db,
	})

	// 检查客户端是否连接
	pong, err := redisClient.Ping(ctx).Result()
	if err != nil {
		return fmt.Errorf("failed to connect to Redis: %v", err)
	}

	fmt.Println("Connected to Redis:", pong)
	return nil
}

// CloseRedisConnection closes the Redis connection.
func CloseRedisConnection() error {
	if redisClient != nil {
		return redisClient.Close()
	}
	return nil
}

// StoreJWTToken stores the JWT token in Redis with an expiration time.
func StoreJWTToken(username, token string, expirationTime time.Duration) error {
	err := redisClient.Set(ctx, username, token, expirationTime).Err()
	if err != nil {
		return fmt.Errorf("failed to store JWT token in Redis: %v", err)
	}
	return nil
}

// RetrieveJWTToken retrieves the JWT token from Redis using the username.
func RetrieveJWTToken(username string) (string, error) {
	token, err := redisClient.Get(ctx, username).Result()
	if err != nil {
		if err == redis.Nil {
			return "", fmt.Errorf("JWT token not found in Redis for username: %s", username)
		}
		return "", fmt.Errorf("failed to retrieve JWT token from Redis: %v", err)
	}
	return token, nil
}
