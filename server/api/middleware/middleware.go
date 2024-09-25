package middleware

import (
	"bytes"
	"fmt"
	"github.com/gin-gonic/gin"
	"heji-server/config"
	"heji-server/domain"
	"heji-server/internal/tokenutil"
	"io"
	"net/http"
	"strings"
)

func Cors() gin.HandlerFunc {
	return func(c *gin.Context) {
		method := c.Request.Method
		origin := c.Request.Header.Get("Origin")
		if origin != "" {
			c.Header("Access-Control-Allow-Origin", "*") // 可将将 * 替换为指定的域名
			c.Header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, UPDATE")
			c.Header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
			c.Header("Access-Control-Expose-Headers", "Content-Length, Access-Control-Allow-Origin, Access-Control-Allow-Headers, Cache-Control, Content-Language, Content-Type")
			c.Header("Access-Control-Allow-Credentials", "true")
		}
		if method == "OPTIONS" {
			c.AbortWithStatus(http.StatusNoContent)
		}
		c.Next()
	}
}
func ErrorHandler() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Next()
		// 检查是否有错误发生
		if len(c.Errors) > 0 {
			// 获取最后一个错误
			err := c.Errors.Last()
			// 将错误信息转换为标准的响应格式
			c.JSON(http.StatusOK, domain.RespError(err.Error()))
			// 阻止其他中间件和处理函数继续执行
			c.Abort()
		}
	}
}
func JwtAuth(secret string) gin.HandlerFunc {
	return func(c *gin.Context) {
		path := c.Request.URL.Path
		if strings.Contains(path, "Register") || strings.Contains(path, "Login") {
			c.Next()
			return
		}

		authHeader := c.Request.Header.Get("Authorization")
		t := strings.Split(authHeader, " ")
		if len(t) == 2 {
			authToken := t[1]
			authorized, err := tokenutil.IsAuthorized(authToken, secret)
			if authorized {
				userID, err := tokenutil.ExtractIDFromToken(authToken, secret)
				if err != nil {
					c.JSON(http.StatusUnauthorized, domain.RespError(err.Error()))
					c.Abort()
					return
				}
				c.Set(config.AuthUserId, userID)
				c.Next()
				return
			}
			c.JSON(http.StatusUnauthorized, domain.RespError(err.Error()))
			c.Abort()
			return
		}
		c.JSON(http.StatusUnauthorized, domain.RespError("Not authorized"))
		c.Abort()
	}
}

// LoggerMiddleware is a middleware to log the request and response
func LoggerMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		// Record the start time
		//startTime := time.Now()

		// Get the request body
		var requestBodyBytes []byte
		if c.Request.Body != nil {
			requestBodyBytes, _ = io.ReadAll(c.Request.Body)
		}
		c.Request.Body = io.NopCloser(bytes.NewBuffer(requestBodyBytes))

		// Save the response body
		responseBody := new(bytes.Buffer)
		responseWriter := &bodyWriter{body: responseBody, ResponseWriter: c.Writer}
		c.Writer = responseWriter

		// Process the request
		c.Next()

		// Log the request and response
		//latency := time.Since(startTime)
		//status := c.Writer.Status()

		//fmt.Printf("Request: %s %s\n", c.Request.Method, c.Request.URL.Path)
		fmt.Printf("[GIN] Request Body: %s\n", string(requestBodyBytes))
		//fmt.Printf("Response: %d\n", status)
		fmt.Printf("[GIN] Response Body: %s\n", responseBody.String())
		//fmt.Printf("Latency: %v\n", latency)
	}
}

type bodyWriter struct {
	gin.ResponseWriter
	body *bytes.Buffer
}

func (w bodyWriter) Write(b []byte) (int, error) {
	w.body.Write(b)
	return w.ResponseWriter.Write(b)
}
