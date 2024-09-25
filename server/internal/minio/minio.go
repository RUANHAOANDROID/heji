package minio

import (
	"bytes"
	"errors"
	"fmt"
	"github.com/gin-gonic/gin"
	"github.com/minio/minio-go/v7"
	"github.com/minio/minio-go/v7/pkg/credentials"
	"heji-server/internal/get"
	"io"
	"log"
	"net/http"
)

// 初始化 MinIO 客户端
var minioClient *minio.Client

const bucket = "heji"

func Connect() {
	conf := get.Config().MinIO
	// 初始化 MinIO 客户端
	var err error
	minioClient, err = minio.New(conf.Address, &minio.Options{
		Creds:  credentials.NewStaticV4(conf.Address, conf.SecretKey, ""),
		Secure: false,
	})
	if err != nil {
		log.Fatal(err)
	}
	if minioClient.IsOffline() {
		panic("Minio 不在线")
	}
}

// ListImages 列出用户对应账单的图片
func ListImages(c *gin.Context, userId string, billId string) {
	deviceType := c.Param("type")
	objectsCh := minioClient.ListObjects(c, bucket, minio.ListObjectsOptions{Prefix: "apk/" + deviceType + "/"})

	var objects []string
	for object := range objectsCh {
		if object.Err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": object.Err.Error()})
			return
		}
		objects = append(objects, object.Key)
	}
	c.JSON(http.StatusOK, gin.H{"apks": objects})
}

// DownloadImage 处理下载对象的请求
func DownloadImage(c *gin.Context, imageName string) {
	objectName := c.Param("objectName")
	// 下载对象
	object, err := minioClient.GetObject(c, bucket, objectName, minio.GetObjectOptions{})
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	defer object.Close()

	// 将对象内容读取到内存中
	var buffer bytes.Buffer
	if _, err := io.Copy(&buffer, object); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	// 设置响应头
	c.Data(http.StatusOK, "application/octet-stream", buffer.Bytes())
}
func UploadImages(c *gin.Context) error {
	uid, exists := c.Get("x-user-id")
	if !exists {
		return errors.New("用户不存在")
	}
	fmt.Println("uid:", uid)
	file, header, err := c.Request.FormFile("file")
	if err != nil {
		c.String(http.StatusBadRequest, "Bad request")
		return err
	}
	defer file.Close()

	// 将文件上传到MinIO
	objectName := header.Filename
	contentType := header.Header.Get("Content-Type")
	size := header.Size

	_, err = minioClient.PutObject(c, bucket, objectName, file, size, minio.PutObjectOptions{
		ContentType: contentType,
	})
	if err != nil {
		log.Println(err)
		c.String(http.StatusInternalServerError, "Failed to upload file to MinIO")
		return err
	}

	c.String(http.StatusOK, fmt.Sprintf("File %s uploaded successfully", objectName))
	return nil
}
