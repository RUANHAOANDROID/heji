package config

import (
	"heji-server/pkg"
	"time"
)

// Config app config
type Config struct {
	App     App
	Redis   Redis
	Mongo   Mongo
	MinIO   MinIO
	Jwt     Jwt
	Options Options
}
type App struct {
	Name    string `yaml:"name"`
	Version string `yaml:"version"`
}

type Mongo struct {
	Host       string        `yaml:"host"`
	Port       string        `yaml:"port"`
	Database   string        `yaml:"database"`
	Username   string        `yaml:"username"`
	Password   string        `yaml:"password"`
	TimeoutMax time.Duration `yaml:"timeoutMax"`
}
type Jwt struct {
	Secret         string        `yaml:"secret"`
	ExpirationTime time.Duration `yaml:"expirationTime"`
}
type MinIO struct {
	Address   string `yaml:"address"`
	AccessKey string `yaml:"accessKey"`
	SecretKey string `yaml:"secretKey"`
	Token     string `yaml:"token"`
}
type Redis struct {
	Address  string `yaml:"address"`
	Password string `yaml:"password"`
	DB       int    `yaml:"db"`
}

type Options struct {
}

func Load(path string) (*Config, error) {
	// Initialize options from config file and CLI context.
	var config Config
	err := pkg.LoadYml(path, &config)
	if err != nil {
		pkg.Log.Error(err)
	}
	return &config, nil
}
