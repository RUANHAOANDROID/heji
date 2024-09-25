
# 架构设计

架构上借鉴了https://github.com/amitshekhariitbhu/go-backend-clean-architecture
![architecture.png](%2Farchitecture.png)



## router
路由作为入口

## controller
控制层持有依赖与用例

## domain
该作为最中心层
该层通常放置
- 请求和响应的模型
- 数据库的实体
- 用例和数据库接口


## usecase 
用例实现domain依赖于数据库

## repository

提供数据给用例，持有数据库实例
