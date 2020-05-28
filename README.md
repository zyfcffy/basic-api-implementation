### 数据库环境

#### 当docker-compose up -d启动数据库容器后，登录http://localhost:44444/ 检查bd:rsSystem 是否被创建，若没有，进行如下操作：
* docker-compose down停止容器
* docker ps -a找到mysql 5.5 对应的容器id
* docker rm {id}
* 重新docker-compose up -d启动，并检查