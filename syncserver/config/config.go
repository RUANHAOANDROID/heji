package config

import (
	"bytes"
	"fmt"
	"github.com/spf13/viper"
	"gopkg.in/yaml.v3"
	"syncserver/pkg"
)

type Config struct {
	App struct {
		AppName  string `yaml:"appName,omitempty"`
		Version  string `yaml:"version"`
		Describe string `yaml:"describe"`
	}
	Database struct {
		Type string `yaml:"type"`
		Name string `yaml:"name"`
	}
	Server struct {
		Host string `yaml:"host"`
		Port string `yaml:"port"`
	}
}

// Load 加载配置
func Load(path string) (*Config, error) {
	// 使用 viper 读取配置文件
	viper.SetConfigFile(path)
	err := viper.ReadInConfig()
	if err != nil {
		return nil, fmt.Errorf("failed to read config file: %w", err)
	}
	c := &Config{}
	// 将配置绑定到结构体
	err = viper.Unmarshal(c)
	if err != nil {
		return nil, fmt.Errorf("failed to unmarshal config: %w", err)
	}

	pkg.Log.Println(c)
	return c, nil
}
func (c *Config) Save() {
	// 将结构体转换为字节数组
	yamlBytes, err := yaml.Marshal(c)
	if err != nil {
		fmt.Println("无法将结构体转换为YAML格式：", err)
		return
	}

	// 将字节数组加载到Viper
	viper.SetConfigType("yaml")
	err = viper.ReadConfig(bytes.NewBuffer(yamlBytes))
	if err != nil {
		fmt.Println("无法加载配置：", err)
		return
	}

	// 写入配置到文件
	filePath := "config.yml"
	err = viper.WriteConfigAs(filePath)
	if err != nil {
		fmt.Println("无法写入配置文件：", err)
		return
	}

	fmt.Println("配置已成功写入YAML文件：", filePath)
}
