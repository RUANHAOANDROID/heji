package controller

import "heji-server/domain"

// FileController 文件传输接口
type FileController struct {
	FilesUseCase domain.FilesUseCase //文件用例
}
