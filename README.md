# 合计
合伙记账

<table>
    
    <tr>
        <td><img src="https://user-images.githubusercontent.com/10151414/120281819-02348980-c2ec-11eb-9171-4a32c1609fd5.jpeg" width="480" height="800" /></td>
        <td><img src="https://user-images.githubusercontent.com/10151414/120281838-0791d400-c2ec-11eb-8ad4-1601b4de1694.jpeg" width="480" height="800" /></td>
    </tr>
    
    <tr>
        <td><img src="https://user-images.githubusercontent.com/10151414/120281860-0e204b80-c2ec-11eb-8d8d-a1e0fd04bab3.jpeg" width="480" height="800" /></td>
        <td> <img src="https://user-images.githubusercontent.com/10151414/120281866-0fea0f00-c2ec-11eb-9c93-f2172c3c76eb.jpeg" width="480" height="800" /></td>
        <td><img src="https://user-images.githubusercontent.com/10151414/120281869-111b3c00-c2ec-11eb-9454-2330cca7897a.jpeg" width="480" height="800" /></td>
    </tr>
</table>






运营账本

统计报表


**整体需求:**

**权限角色:**

角色分为管理员账户\账本创建人\账本用户\查账人

管理员具有创建管理用户权限

账本创建人具有账本全权

账本用户可以添加账单\修改账单\删除账单权限

查账人仅可以浏览账本
**账本** 

用户均可创建账本,账本通过口令分享给其他记账人.仅创建人具有删除账本权限切账本不再有账单.
账本创建人具备分配账本权限功能{浏览\增加\更改\删除}
账本具备{账本名称\账本所属类别}

**账单**
账单分为收入\支出
账单具备属性包括{账单类别\金额\时间\票据\备注信息(更具具体类别小票和备注)}
账单查重功能(通过账单时间\金额\票据MD5值判断是否存在重复记录)
账单导入,从支付宝\微信\银行转账单

**统计**
支出人员占比(起始资金占比){有史以来\年\月\} 
支出走势{{修理费用走势\过路费走势\气价走势\运营投入走势}
支出类型占比
报表{全年收支\月份收支}
月收支总览
年收支总览

**客户端:**



服务端:
