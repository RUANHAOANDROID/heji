package domain

type QianJiExcel struct {
	//时间
	Time string `json:"time"`
	//分类
	Category string `json:"category"`
	//类型
	Type string `json:"type"`
	//金额
	Money string `json:"money"`
	//账户1
	Account1 string `json:"account1"`
	//账户2
	Account2 string `json:"account2"`
	//备注
	Remark string `json:"remark"`
	//账单图片
	Urls string `json:"urls"`
}
