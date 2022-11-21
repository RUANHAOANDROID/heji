# 数据说明

## 客户端

App端使用 SQLite数据库

App数据库使用ObjectId作为主键，同步到服务端后主键不变

App离线优先，服务仅作为多设备的同步备份。

App首先保存数据至SQLite然后在后台同步至服务端

App在未登陆情况下能够离线使用，默认创建一个本地用户和一个本地个人账本

APP离线账本可以在账本设置页选择迁移同步到注册的用户下

## 服务端

服务端使用MongoDB

#### 用户（MUser）
| 列名        | 类型       | 说 明       |
|-----------|----------|-----------|
| _id       | ObjectID | 自增ID      |
| name      | String   | 用户名       |
| password  | String   | 密码        |
| tel       | String   | 电话号码      |
| authority | List     | 权限（关联权限集） |

#### 权限（MAuthority）

| 列名        | 类型       | 说 明         |
|-----------|----------|-------------|
| _id       | ObjectID | 自增ID        |
| authority | String   | 权限          |
| book_id   | String   | 账本ID（关联账本集） |

#### 账本Client（Book）

| 列名          | 类型       | 说 明                        |
|-------------|----------|----------------------------|
| _id         | ObjectID | 自增ID                       |
| book_name   | String   | 账本名称                       |
| create_user | String   | 创建人                        |
| cover       | byte[]   | 封面图片                       |
| sync_status | Integer  | 0本地新增，-1本地删除、1本地更新，2已同步到服务 |
| anchor      | long     | 锚点用作记录服务最后修改时间             |

#### 账本Server（MBook）

| 列名          | 类型       | 说 明      |
|-------------|----------|----------|
| _id         | ObjectID | 自增ID     |
| book_name   | String   | 账本名称     |
| create_user | String   | 创建人      |
| users       | array    | 该账本下的用户集 |
| cover       | byte[]   | 二进制封面图片  |
| modified    | long     | 最后修改时间   |

客户端账本同步时根据anchor 和sync_status向服务器发起同步。	

通过”Max(anchor)“=="Max(modified)"询问是否需要更新数据 。客户端更新已经同步的数据



#### 账单（MBill）

| 列名          | 类型       | 说 明           |
|-------------|----------|---------------|
| _id         | ObjectID | 自增ID          |
| book_id     | String   | 所属账本ID        |
| money       | Double   | 货币            |
| category    | String   | 账单收支类型        |
| type        | Integer  | 收入/支出         |
| dealer      | String   | 经手人           |
| createUser  | String   | 创建人           |
| remark      | String   | 备注            |
| images      | String[] | 图片集（关联账单图片）   |
| time        | String   | 账单日期（用户选择的日期） |
| create_time | long     | 创建时间          |
| update_time | long     | 更新时间          |

账单作为账本的子集，更新账单时更新账本modified以供客户端询问更新与否

#### 账单备份（MBillBackup）

> 同账单

#### 账单票据图片（MBillImage）

| 列名          | 类型       | 说 明    |
|-------------|----------|--------|
| _id         | ObjectID | 自增ID   |
| bill_id     | String   | 所属账单ID |
| filename    | String   | 文件名    |
| length      | Long     | 文件长度   |
| md5         | String   | MD5    |
| upload_time | Long     | 上传时间   |
| ext         | String   | 后缀名    |
| isGridFS    | Boolean  | 是否分片   |
| data        | Binary   | 二进制图片  |

#### 账单类型（MCategory）

| 列名      | 类型       | 说 明      |
|---------|----------|----------|
| _id     | ObjectID | 自增ID     |
| book_id | String   | 所属账本ID   |
| type    | Integer  | 支出/收入    |
| name    | String   | 标签名      |
| level   | Integer  | 多级标签所属等级 |
| index   | Integer  | 排序顺序     |

#### 同步日志（MOperateLog）

| 列名       | 类型       | 说 明               |
|----------|----------|-------------------|
| _id      | ObjectID | 自增ID              |
| book_id  | String   | 根据book划分日志        |
| opeID    | Integer  | 操作对象的ID           |
| opeClass | String   | 操作对象类别 （操作了账本或账单） |
| opeType  | Integer  | 操作类型（删除或更新）       |
| opeDate  | String   | 操作时间（客户端操作时间）     |

#### 客户端错误日志（MErrorLog）

| 列名          | 类型       | 说 明  |
|-------------|----------|------|
| _id         | ObjectID | 自增ID |
| deviceModel | String   | 设备型号 |
| tel         | String   | 电话号  |
| contents    | String   | 日志内容 |