# 第一阶段：使用 golang 镜像作为基础镜像进行编译
FROM golang:1.23-alpine AS builder

# 设置工作目录
WORKDIR /app

# 将当前目录的 go.mod 和 go.sum 复制到工作目录
COPY go.mod .
COPY go.sum .

# 下载依赖
RUN go mod download

# 将其他源代码文件复制到工作目录
COPY . .

# 编译 Go 应用，使用静态链接
RUN CGO_ENABLED=0 GOOS=linux go build -ldflags="-s -w" -o runner
# 安装upx压缩工具
RUN apk add --no-cache upx
# 压缩运行文件
RUN upx --best --lzma runner

# 第二阶段：使用轻量级的 alpine 镜像作为基础镜像
FROM alpine:3.19.0 AS runner

LABEL name="heji-server"
LABEL org.opencontainers.image.source=https://github.com/RUANHAOANDROID/heji
LABEL org.opencontainers.image.authors="hao88"
LABEL version="1.0"
LABEL description="heji server"
# 设置工作目录
WORKDIR /app

# 从第一阶段复制编译好的二进制文件到当前阶段
COPY --from=builder /app/runner .
COPY --from=builder /app/config.yml .

# 运行应用
CMD ["./runner"]

# 暴露应用运行时需要的端口
EXPOSE 8080