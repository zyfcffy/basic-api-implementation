### 作业要求

* 将所有接口的返回值都替换成使用ResponseEntity
* 所有post请求都返回201,并且返回的头部带上index字段（值为创建的资源在列表中的位置：eg: 添加的热搜事件在列表中的index）
* 完成demo里的练习：get /rs/list和 get/rs/{index}接口返回的数据中不包含user字段
* 添加获取所有用户的接口: get /users，期望返回的数据格式例子: 
    ```
    [{
        "user_name": "xxxx",
        "user_age": 19,
        "user_gender": "female",
        "user_email": "xxx@xx",
        "user_phone": "1xxxxxxxxxx"
    }]
* 先写测试！！！
* hint: @JsonpProperty的使用


#### 在demo的基础上完成下面需求：
* 重构添加热搜的接口，热搜事件现在增加一个字段:user，用来表示是哪个用户添加的该热搜事件。
  用户所包含的字段以及字段的限制和demo中保持一致，请求例子：
  ```
  {
      "eventName": "添加一条热搜",
      "keyword": "娱乐",
      "user": {
        "userName": "xiaowang",
        "age": 19,
        "gender": "female",
        "email": "a@thoughtworks.com",
        "phone": 18888888888
      }
  }
  ``` 
  
* 如果userName已存在在user列表中的话则只需添加热搜事件到热搜事件列表，如果userName不存在，则将User添加到热搜事件列表中（相当于注册用户）
* 需要对请求进行校验：其中user keyword eventName都不能为空, user的校验规则：
    ```
  名称(不超过8位字符，不能为空)
  性别（不能为空）
  年龄（18到100岁之间，不能为空）
  邮箱（符合邮箱规范）
  手机号（1开头的11位数字，不能为空）
    ```
  
* 测试需要对每个验证条件进行覆盖
    
notice: 注意@Valid和@Validated的配合使用



  


根据课堂上的demo，完成下面需求
1. 提供获取某一条热搜事件的接口
2. 提供能够根据起始参数，获取对应范围内的热搜事件列表的接口
3. 提供添加热搜事件的接口（事件包含两个字段：事件名称和关键字）
4. 提供修改某条热搜事件的接口（demo没有展示，请大家自己完成）
5. 提供删除某条热搜事件的接口（demo没有展示，请大家自己完成）

#### 需求4、5详细描述

* 需求4： 修改某条事件时（通过参数传递的序号，修改列表中对应的事件数据），如果RequestBody只传了eventName没有传keyword那么仅仅只修改eventName
         如果只传了keyword没有传eventName，那么只修改keyword字段
         如果两个字段都传了，那么都进行修改
         
* 需求5： 通过参数传递的序号，删除列表中对应的某条事件数据


<span style="color: red"> 注意：所有的需求都请先写测试再写实现 </span> 



