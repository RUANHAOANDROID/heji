# 合计
> 注：该项目为练手项目仅仅用做学习和非商业化目的，UI方面参考了钱迹（无广告且记账速度非常快的一款记账软件），我本身也是钱迹的超级终身VIP，非常感谢钱迹的优秀开发者。

## 功能介绍

合伙记账,运营账本,统计分析,从支付宝微信导入账单,从ETC导入账单

### 权限角色

不同角色具有不同的权限

* 管理员(待开发)
    * 管理员具有创建管理用户权限
* 账本创建人（待开发）
    * 账本创建人具有账本全权
* 账本用户
    * 账本用户可以添加账单\修改账单\删除账单权限
* 查账人（待开发）

    * 查账人仅可以浏览账本

### 账本

#### 功能描述：
1. 用户均可创建账本,账本通过口令分享给其他记账人.仅创建人具有删除账本权限且账本不再有账单.
2. 账本创建人具备分配账本权限功能{浏览\增加\更改\删除}
3. 账本具备{账本名称\账本所属类别}

#### 目前已有账本

* 运营账本（目前单一账本，多用户同时记账，非自身记账不可删除）

### 账单
1. 账单分为收入\支出
2. 账单属性

   1. 账单类别
   2. 金额
   3. 时间
   4. 票据图片
   5. 备注信息
3. 账单查重功能
   > 通过账单时间\金额\票据MD5值判断是否存在重复记录
4. 账单导入
5. 账单导出

#### 账单导入支持：

* ETC 
* EXCEL
* CVS
* 钱迹 
* 微信
* 支付宝
#### 账单导出支持：

* EXCEL
* CVS
* 钱迹 



### 统计
1. 支出人员支出占比
   1. 起始资金占比
   2. 月支出占比
   3. 年支出占比
    
    > 根据时间分为月/年/有史以来
2. 支出走势
   1. 修理费用走势
   2. 过路费走势
   3. 气价走势
3. 收支类型占比
  
   1. 全年收支占比
   2. 月收支占比
4. 报表
   1. 全年收支报表
   2. 月收支报表
5. 收支总览
   1. 年收支总揽
   2. 月收支总揽

## Client Android

Android client 采用单Activity 多Fragment 项目结构偏向于MMVP模式

### UI

<table>
    <tr>
      <td>
          <img src="https://user-images.githubusercontent.com/10151414/120281819-02348980-c2ec-11eb-9171-4a32c1609fd5.jpeg" alt="hello" style="zoom:67%;"/>             </td>
      <td>
          <img src="https://user-images.githubusercontent.com/10151414/120281838-0791d400-c2ec-11eb-8ad4-1601b4de1694.jpeg" alt="hello" style="zoom:67%;"/>
      </td>
      <td>
          <img src="https://user-images.githubusercontent.com/10151414/120454374-675cad80-c3c6-11eb-926d-d70ba010e1cf.jpeg" alt="addBill" style="zoom:67%;"/>
      </td>
    </tr>
    <tr>
        <td><img src="https://user-images.githubusercontent.com/10151414/120281860-0e204b80-c2ec-11eb-8d8d-a1e0fd04bab3.jpeg" alt="hello" style="zoom:67%;"/></td>
        <td> <img src="https://user-images.githubusercontent.com/10151414/120281866-0fea0f00-c2ec-11eb-9c93-f2172c3c76eb.jpeg" alt="hello" style="zoom:67%;"/></td>
        <td><img src="https://user-images.githubusercontent.com/10151414/120281869-111b3c00-c2ec-11eb-9454-2330cca7897a.jpeg" alt="hello" style="zoom:67%;"/></td>
    </tr>
</table>

### 技术集
*   基于AndroidX,使用Java + kotlin 混合开发
*   OkHttp + retrofit 网络请求
*   navigation Fragment导航
*   BaseRecyclerViewAdapterHelper 列表的展示
*   gson 数据格式化
*   MatisseKotlin 图片选择
*   permissionx 人性化的权限封装
*   room 更简单好调试的Sqlite数据库
*   utilcode 强大简单的工具集
*   xpopup 多样式的弹窗
*   Luban 账单图片压缩
*   calendarview 日历记账
*   mmkv  KevValue存储
*   MPAndroidChart 强大的图表用做统计
*   immersionbar 多机型强大Toobar的封装适配

## 服务端
服务端使用Java Springboot框架，数据库采用MongoDB，目前打包为jar程序直接运行
### 技术集
*   SpringBoot 基础框架
*   MongoDB 数据存储
*   JWT  Json web Token 用户鉴权
*   gson  数据的格式化
*   Easyexcel Excel导入导出操作
*   Lombok 减少样板代码

