package api

var success = 0
var fail = 1

type Response[T interface{}] struct {
	Code int    `json:"code"`
	Msg  string `json:"msg"`
	Data T      `json:"data"`
}

func RespError(msg string) Response[string] {
	return Response[string]{Code: fail, Msg: msg, Data: ""}
}
func RespSuccess(data any) Response[any] {
	return Response[any]{Code: success, Msg: "ok", Data: data}
}
