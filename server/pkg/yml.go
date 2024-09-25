package pkg

import (
	"bytes"
	"fmt"
	"github.com/spf13/viper"
	"gopkg.in/yaml.v3"
)

// LoadYml Load 加载配置
func LoadYml(path string, obj any) error {
	// 使用 viper 读取配置文件
	viper.SetConfigFile(path)
	err := viper.ReadInConfig()
	if err != nil {
		return fmt.Errorf("failed to read config file: %w", err)
	}
	// 将配置绑定到结构体
	err = viper.Unmarshal(obj)
	if err != nil {
		return fmt.Errorf("failed to unmarshal config: %w", err)
	}

	fmt.Printf("config %v\n", obj)
	return nil
}

// SaveYml 保存yml
func SaveYml(filename string, obj any) error {
	// 将结构体转换为字节数组
	yamlBytes, err := yaml.Marshal(obj)
	if err != nil {
		fmt.Println("无法将结构体转换为YAML格式：", err)
		return err
	}

	// 将字节数组加载到Viper
	viper.SetConfigType("yaml")
	err = viper.ReadConfig(bytes.NewBuffer(yamlBytes))
	if err != nil {
		fmt.Println("无法加载配置：", err)
		return err
	}

	// 写入配置到文件
	err = viper.WriteConfigAs(filename)
	if err != nil {
		fmt.Println("无法写入配置文件：", err)
		return err
	}

	fmt.Println("配置已成功写入YAML文件：", filename)
	return nil
}
