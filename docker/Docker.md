# **DOCKER**







官网地址-->[docker](https://www.docker.com/)

tips: 笔记大多数为命令的执行





[TOC]



## 一、安装

> 此次安装基于`CentOS7`版本 安装文档 --> https://docs.docker.com/engine/install/centos/

### 1) 卸载旧程序

```sh
 sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
```

### 2) 环境搭建

```bash
# docker编译依赖于gcc
yum -y install gcc

# yum-utils
sudo yum install -y yum-utils

# 由于官网提供的仓库地址是国外网络...采用阿里云
# 设置docker镜像源
yum-config-manager \
    --add-repo \
    https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
# 上一条命令的第二步
sed -i 's/download.docker.com/mirrors.aliyun.com\/docker-ce/g' /etc/yum.repos.d/docker-ce.repo

# 可忽略 更新yum索引软件包 变的快一点
yum makecache fast
```



### 3) 依照官网执行

```bash
 # 补充 `sudo`root用户执行的声明
 sudo yum install docker-ce docker-ce-cli containerd.io
```



### 4）启动测试

```bash
systemctl start docker # 开启docker服务
```



```sh
 sudo docker run hello-world
 # 执行命令出现以下表示安装成功
 
 Hello from Docker!
 This message shows that your installation appears to be working correctly.
 
 docker --version
 Docker version 20.10.12, build e91ed57
```



### 5） 阿里云镜像加速

> 来源 黑马 自己可以申请阿里云个人实例镜像加速

官网--> https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors

```bash
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://uc51lue1.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```



### 6) 相应卸载命令

```bash
# systemctl stop docker
# yum remove docker-ce docker-ce-cli containerd.io
# rm -rf /var/lib/docker
# rm -rf /var/lib/containerd
```





## 二、docker常用命令

### 1） run干了什么?

```java
docker run (images) -->{
	if(本地存在(images)){
		// 直接以该镜像作为模板容器实例运行
        images.run();
		return;
	}
	// 本地没有 就去dockerHub找
	Images img = DockerHub.get(images.getName())
    if(img != null){
        // 下载到本地运行
        images.run();
        return;
    }
    throw new Exception("无法找到该镜像，执行失败！！！");
}
```

### 2) 常用命令

#### 帮助类

```sh
systemctl stop docker  # 停止docker服务

systemctl restart docker  # 重启docker服务

systemctl status docker # 查看状态 active (running)代表运行

systemctl enable docker # 配置开机启动

docker info # 查看摘要信息

docker --help # 查看帮助文档

docker 命令 --help # 查看精确命令的帮助文档
```



#### 镜像命令

具体命令

--附加参数功能



1. `docker images` 直接运行查看所有镜像
   1. `-a`列出所有本地镜像
   2. `-q`只显示镜像id

docker images 选项说明

```bash
REPOSITORY    TAG       IMAGE ID       CREATED        SIZE

镜像源        标签版本号   镜像id         创建时间        占用大小
```

2. `docker search`查看远程库是否存在某个镜像
   1. `docker search --limit 5 镜像名`分页

```sh
NAME         DESCRIPTION      STARS     OFFICIAL   AUTOMATED
名称			说明			   点赞数		是否官方    是否自动构建
```

3. `docker pull 镜像名：tag`下载镜像 tag版本号
   1. 不写tag默认`latest`也就是最新版
4. `docker system df `查看镜像/容器/数据卷占用空间 `df -h`
5. `docker rmi 镜像名`删除镜像
   1. `docker rmi -f image:tag`删除单个
   2. `docker rmi -f image:tag image:tag...`删除多个
   3. `docker rmi -f $(docker images -qa)`删除全部



#### docker的虚悬镜像是什么？

> 仓库名，镜像名都是<none>的镜像 `dangling`image
>
> 
>
> REPOSITORY    TAG       IMAGE ID       CREATED        SIZE
>
> none          none      fsfdsdfds       ....          12232MB



#### 容器命令

* `docker run`
  * `--name`为容器起一个名称
  * `-d`后台守护进程运行
  * `-i`以交互模式运行 与`-t`一起使用
  * `-t`为容器分配一个伪终端使用
  * `-P`随件端口映射
  * `-p`指定端口映射

```bash
docker run -it ubuntu:latest bash
# 交互式运行ubuntu容器 需要交互式 bash shell脚本
# 输入exit退出
```

* `docker ps`查看当前运行的容器

  * `-a`包括没运行的
  * `-l`最近创建de
  * `-n 1`  最近创建过的1个
  * `-q`静默模式 只显示容器编号

* 退出容器内部的命令

  * `exit`退出结束容器停止
  * `ctrl+p+q`退出容器不停止

  ```sh
  # 不停止可以直接
  docker exec -it ub bash # 再次进入容器
  ```

* `docker start 容器名/id`重新启动

* `docker rm 容器名/id`删除容器

  * `-f`硬删
  * `docker rm -f $(docker ps -a -q)`删除一堆
  * `docker ps -a -q | xargs docker rm -f`同上

**问题**:

执行docker run -d ubuntu 以后台模式运行某个容器

docker ps -a 查看容器已经退出

**注意点**：docker容器后台运行，必须要有一个前台进程，容器运行的命令如果不是一直挂起的那一种（top,yail）,就会自动退出

比如nginx这种web容器，配置服务只需要启动响应的额service --> service nginx start,但是导致前台没有运行的应用，就会认为无事可做，自杀

**解决方案**：**将要运行的容器以前台进程的形式运行，也就是命令行模式，表示还有交互，别中断**



docker run -it redis:latest 前台运行

docker run --name rs -p 6379:6379 -d redis 守护进程启动 指定端口6379



* `docker logs 容器名/id`查看容器日志
* `docker top 容器名/id`查看容器内进程
* `docker exec `进入容器
  * `-it`交互伪终端
  * `bash/容器特有命令`执行相关命令

```bash
# 开启一个容器 后台运行
docker run --name rs -p 6379:6379 -d redis 

# 直接进入容器内部
docker exec -it redis bash
# 执行redis客户端
redis-cli

# 或者直接执行客户端
docker exec -it redis redis-cli
```

* 重新进入容器`docker attach 容器id`

与exec的区别是？

> atach直接进入容器启动命令的终端，不会启动新的进程 exit会导致直接停止容器
>
> exec 是在容器中打开新的终端，可以启动新的进程，exit不会导致容器的停止



* `docker cp 容器名称/id 容器文件路径  主机路径`拷贝容器重要文件

```bash
docker cp redis:/data/dump.rdb /opt
```

**镜像**的导入与导出

* docker save image:atg -o 文件路径	导出
* docker load docker load -i 文件路径  加载镜像

```bash
# 导出
docker save redis:latest -o /opt/redis.tar
docker save redis:latest > /opt/redis.tar

# 加载
docker load -i /opt/redis.tar
```

**容器**的导入导出

* docker export 容器名/id > 文件路径
* cat 文件.tar | docker import - 包名/容器名:版本号

```bash
# 导出
docker export redis > aa.tar

# 导入
cat aa.tar | docker import - wjl/redis:666
```





## 三、 docker镜像



​	所谓的镜像是一种轻量级，可执行的软件包，它包含运行某个软件所需的所有内容，把应用程序和配置依赖打包形成一个可交互的运行环境，这个打包好的运行环境就是image镜像文件



docker run ≈ new Java()



容器镜像层都是只读的，容器是可写的

> 当容器启动时，一个新的可写层被加载到镜像的顶部。这一层通常被称为“容器层”，容器层之下都叫“镜像层”。
>
> 所有对容器的改动-无论添加、删除、还是修改文件都只会发生在容器中。只有容器是可写的，容器层下面的镜像层都是只读的



### 1）commit命令

案例--> 让原生Ubuntu镜像安装vim 

docker run -it --name ub ubuntu 进入容器

```bash
apt-get update # 更新包管理工具 `apt-get`类似与CentOS的yum

apt-get -y install vim # 安装vim

exit 
```

执行commit命令

```bash
 docker commit -m="add vim cmd" -a="wjl" d036ca38c53b ubuntu-vim:1.0
 
 -m 描述信息
 -a 作者
 d036ca38c53b 刚刚安装vim的Ubuntu 
 ubuntu-vim:1.0  容器名:版本号
```

执行docker images

```bash
ubuntu-vim   1.0       de9852d7a6f6   About a minute ago   174MB
```



#### 提交阿里云

步骤

登录[阿里云](aliyun.com)--> 控制台-->容器镜像服务-->个人实例-->命名空间-->创建镜像仓库选择自己的命名空间-->添加描述-->创建本地仓库

此时阿里云会生成相关命令 直接cv



### 2）创建并提交私有库

```bash
docker pull registry 

# 映射5000端口
docker run --name reg -v /usr/wjl:/tmp/myregistry  -p 5000:5000 -d --privileged=true  registry:latest
 
# 找一个容器做点改动 执行commit命令
 docker commit -m="add vim cmd" -a="wjl" d036ca38c53b nginx_biaoji:1.0

# 验证本地私服库是否包含镜像
 curl -XGET http://192.168.0.103:5000/v2/_catalog

# 提交
docker tag nginx_biaoji:1.0 192.168.0.103:5000/nginx_biaoji:1.0

# 执行docker images 所出现的就是要推送的镜像
192.168.0.103:5000/nginx_biaoji

# 推送私有库下面有详细的配置

# 推送私有库
 docker push 192.168.0.103:5000/nginx_biaoji:1.0
 
# 验证是否成功
curl -XGET http://192.168.0.103:5000/v2/_catalog


# 拉取镜像
docker pull 192.168.0.103:5000/nginx_biaoji:1.0
```

由于docker不支持http发送 ，所以采用配置



```json
vim /etc/docker/daemon.json
# 注意ip
{
"registry-mirrors":["https://uc51lue1.mirror.aliyuncs.com"],
"insecure-registries":["192.168.0.103:5000"]
}
```

**配置完重启**



## 四、挂载数据卷

CentOS7安全模块比之前系统版本强，不安全的会先禁止，所以目录挂载的情况默认为不安全的行为，在SELinux里挂载的目录被禁止掉了，如果要开启，一般使用`--privileged=true`命令，扩大容器的权限解决挂载目录没有权限的问题，否则，container内的root只是外部的一个普通用户权限。



所谓的数据卷就类似于活动硬盘，容器死了挂载数据卷的文件夹还会存放数据

语法

`docker run -it --privileged=true -v 宿主机目录(绝对路径):/容器目录 image:tag`



```bash
docker volume inspect 容器id # 查看挂载信息
```

以上的配置是默认加了`rw`表示可读可写

要想限制容器只读可以在卷参数后面加上`:ro`

ro: read-only



数据卷的继承

`docker run -it --volumes-from u1  --name u2 --privileged=true ubuntu bash`

--volumes-from u1 参数表示继承u1的数据卷



## 五、常用软件运行示例

### 1）tomcat

无需下载最新版  docker pull billygoo/tomcat8-jdk8

docker run --name tom -d -p 8080:8080 billygoo/tomcat8-jdk8



Tips:注意最新版需要将webapps.dict更改为webapps



### 2) Mysql

dcker pull mysql:latest

```bash
docker run --name mysql -e MYSQL_ROOT_PASSWORD=123456 -d -p 3306:3306  mysql:latest
# -e 指定环境变量
# MYSQL_ROOT_PASSWORD MySQL的密码
```

create database if not exists mysqldb character set utf8;

连接Navicat连接测试成功



但是mysql很重要 需要数据卷

```bash
 docker run --name mysql
 -d -p 3306:3306 
 --privileged=true 
 -v /tmp/mysql/log:/var/log/mysql 
 -v /tmp/mysql/data:var/lib/mysql 
 -v /tmp/mysql/conf:/etc/mysql/conf.d 
 -e MYSQL_ROOT_PASSWORD=123456 
 mysql:latest	
```

在/tmp/mysql/conf目录下创建`my.cnf`文件

```bash
[client]
default_character_set=utf8
[mysqld]
collation_server = utf8_general_ci
character_set_server = utf8
```

docker restart mysql

```bash
mysql> select * from vip;
+----+------+
| id | name |
+----+------+
|  1 | 王   |
|  2 | 李   |
+----+------+
2 rows in set (0.00 sec)
```



### 3) redis

创建redis/conf 目录 找一份redis.conf拷贝至此目录

1. 后台启动 daemonize no
2. 允许远程访问 bind 0.0.0.0
3. 可以选择数据持久化 appen
4. 保护模式 protected-mode no

```bash
docker run -p 6379:6379 
--name redis --privileged=true 
-v /opt/redis/conf/redis.conf:/etc/redis/redis.conf 
-v /opt/redis/data:/data 
-d redis:6.0.8 
redis-server /etc/redis/redis.conf
```

docker exec -it redis redis-cli



### 4） MySql主从复制

#### 主机

粘贴以下内容：注意 两个Mysql进程可能起不来



5.7

docker run -p 3307:3306 --name mysql-master -v /tmp/mysql2/mysql-master/log:/var/log/mysql -v /tmp/mysql2/mysql-master/data:/var/lib/mysql -v /tmp/mysql2/mysql-master/conf:/etc/mysql  -e MYSQL_ROOT_PASSWORD=123456 -d mysql:latest





8.0

```
docker run -p 3307:3306 --name mysql-master -v /tmp/mysql2/mysql-master/log:/var/log/mysql -v /tmp/mysql2/mysql-master/data:/var/lib/mysql -v /tmp/mysql2/mysql-master/conf:/etc/mysql  -v /tmp/mysql2/mysql-master/mysql-files:/var/lib/mysql-files  -e MYSQL_ROOT_PASSWORD=123456 -d mysql:latest
```



在` /tmp/mysql2/mysql-master/conf/`下创建`my.cnf`

```bash
[mysqld]
## 设置server_id 同一区域网唯一
server_id=101
## 执行不需要同步的数据库名称
binlog-ignore-db=mysql
## 开启二进制日志
log-bin=mall-mysql-bin
## 设置二进制日志使用内存大小（事务）
binlog_cache_size=1M
## 设置二进制格式‘
binlog_format=mixed
## 二进制日志过期时间清理
expire_logs_days=7
## 跳过主从复制终于到的所有错误避免slave复制中断 1062主键重复
slave_skip_errors=1062
```

https://blog.csdn.net/qq_40604437/article/details/106680762 8.0错误问题解决



> 注意：以下语法有很多在8.0中过时 详细信息查看日志
>
> docker logs -f imageName/id

[mysqld]
server_id=101
binlog-ignore-db=mysql
log-bin=mall-mysql-bin
binlog_cache_size=1M
binlog_format=mixed
expire_logs_days=7
slave_skip_errors=1062



secure_file_priv=/var/lib/mysql



如果报错

ERROR 1045 (28000): Access denied for user 'root'@'localhost' (using password: YES)
root@782942b739bb:/# mysql -u root -p

加上这一行

skip-grant-tables



进入容器登录MySQL

```sql
# 创建一个从机用户
CREATE USER 'slave'@'%' IDENTIFIED BY '123456';
# 授权
GRANT REPLICATION SLAVE,REPLICATION CLIENT ON *.* TO'slave'@'%';
```



#### 从机



5.7

docker run -p 3307:3306 --name mysql-master -v /tmp/mysql2/mysql-master/log:/var/log/mysql -v /tmp/mysql2/mysql-master/data:/var/lib/mysql -v /tmp/mysql2/mysql-master/conf:/etc/mysql  -e MYSQL_ROOT_PASSWORD=123456 -d mysql:latest





8.0

```
docker run -p 3308:3306 --name mysql-slave -v /tmp/mysql2/mysql-master/log:/var/log/mysql -v /tmp/mysql2/mysql-slave/data:/var/lib/mysql -v /tmp/mysql2/mysql-slave/conf:/etc/mysql  -v /tmp/mysql2/mysql-slave/mysql-files:/var/lib/mysql-files  -e MYSQL_ROOT_PASSWORD=123456 -d mysql:latest
```

[mysqld]
server_id=102
binlog-ignore-db=mysql
log-bin=mall-mysql-bin
binlog_cache_size=1M
binlog_format=mixed
expire_logs_days=7
slave_skip_errors=1062

log_slave_updates=1

##设置为只读 有surper权限的除外

read_only=1

secure_file_priv=/var/lib/mysql



#### 主服务器查看状态

```sql
mysql> show master status;
+-----------------------+----------+--------------+------------------+-------------------+
| File                  | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+-----------------------+----------+--------------+------------------+-------------------+
| mall-mysql-bin.000001 |      713 |              | mysql            |                   |
+-----------------------+----------+--------------+------------------+-------------------+
1 row in set (0.00 sec)
```



进入从服务器

[root@192 ~]# docker exec -it mysql-slave bash
root@c7ac51046759:/# mysql -u root -p123456



执行认主

```sql
change master to master_host='192.168.0.102',master_user='slave',master_password='123456',master_port=3307,master_log_file='mall-mysql-bin.000001',master_log_pos= 713,master_connect_retry=30;
```





参数说明

```bash
master_host 主数据库ip
master_port 主数据库端口
master_user 之前在主数据库创建用于同步数据的账号
master_password 密码
master_log_file 指定从主数据库复制的日志文件，查看主数据的状态 获取File参数
master_log_pos 指定从主数据库哪个地方开始复制数据，查看主数据的状态 获取Position参数
# 就是之前在主数据库获取的  Position ↑
master_connect_retry 连接失败重试的事件间隔 秒为单位
```



在从机执行

```sql
show slave status\G; 


# 表示还没开始
Slave_IO_Running: No
Slave_SQL_Running: No
```

在从机开启主从

```sql
mysql> start slave;
Query OK, 0 rows affected, 1 warning (0.02 sec)

# 表示成功
Slave_IO_Running:Yes
Slave_SQL_Running: Yes

```

此时主机创建表 插入数据从机能看到查询到表示成功

[(19条消息) Mysql主从同步时Slave_IO_Running：Connecting ; Slave_SQL_Running：Yes的情况故障排除_mbytes的博客-CSDN博客_mysql slave_io_running](https://blog.csdn.net/mbytes/article/details/86711508)

### 5） redis集群

· 1~2亿条数据需要缓存，请问如何设计这个存储案例

单机单台100%不可能，肯定是分布式存储，用redis如何落地？![graphic](data:application/octet-stream;base64,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)

| 2亿条记录就是2亿个k,v，我们单机不行必须要分布式多机，假设有3台机器构成一个集群，用户每次读写操作都是根据公式：hash(key) % N个机器台数，计算出哈希值，用来决定数据映射到哪一个节点上。 |
| ------------------------------------------------------------ |
| 优点： 简单粗暴，直接有效，只需要预估好数据规划好节点，例如3台、8台、10台，就能保证一段时间的数据支撑。使用Hash算法让固定的一部分请求落到同一台服务器上，这样每台服务器固定处理一部分请求（并维护这些请求的信息），起到负载均衡+分而治之的作用。 |
| 缺点：  原来规划好的节点，进行扩容或者缩容就比较麻烦了额，不管扩缩，每次数据变动导致节点有变动，映射关系需要重新进行计算，在服务器个数固定不变时没有问题，如果需要弹性扩容或故障停机的情况下，原来的取模公式就会发生变化：Hash(key)/3会变成Hash(key) /?。此时地址经过取余运算的结果将发生很大变化，根据公式获取的服务器也会变得不可控。某个redis机器宕机了，由于台数数量变化，会导致hash取余全部数据重新洗牌。 |



一致性Hash算法背景

　　一致性哈希算法在1997年由麻省理工学院中提出的，设计目标是为了解决

分布式缓存数据变动和映射问题，某个机器宕机了，分母数量改变了，自然取余数不OK了。

一致性哈希环

  一致性哈希算法必然有个hash函数并按照算法产生hash值，这个算法的所有可能哈希值会构成一个全量集，这个集合可以成为一个hash空间[0,2^32-1]，这个是一个线性空间，但是在算法中，我们通过适当的逻辑控制将它首尾相连(0 = 2^32),这样让它逻辑上形成了一个环形空间。

 

  它也是按照使用取模的方法，前面笔记介绍的节点取模法是对节点（服务器）的数量进行取模。而一致性Hash算法是对2^32取模，简单来说，一致性Hash算法将整个哈希值空间组织成一个虚拟的圆环，如假设某哈希函数H的值空间为0-2^32-1（即哈希值是一个32位无符号整形），整个哈希环如下图：整个空间按顺时针方向组织，圆环的正上方的点代表0，0点右侧的第一个点代表1，以此类推，2、3、4、……直到2^32-1，也就是说0点左侧的第一个点代表2^32-1， 0和2^32-1在零点中方向重合，我们把这个由2^32个点组成的圆环称为Hash环。

![graphic](data:application/octet-stream;base64,iVBORw0KGgoAAAANSUhEUgAAAb8AAAHTCAIAAAA4R4EEAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAANldSURBVHhe7P1ncCRbm9+JSREKiRFk7IpLN7sklyGSIVErUR+0qw1FKELUB+1KS5nlaleiEz1XHJoZMmY4JOed4QzfMa+7977X921v0fC+G6bRQAPdaPhyWb6AculdVaW3VYXSczIBNLrQDuhC39t9T9/fzcgqFApZVZm/+j95Tp7zP7AdDYPBYDCnBdsTg8FgzgK2JwaDwZwFbE8MBoM5C9ieGAwGcxawPTEYDOYsYHtiMBjMWcD2xLwew2zA0vNN1zNgxbQUy1aPfvr2NFu2qsnwnLpR73R8WOEFClY0vQZ/K3wM3AnABvhNCx7cajvhj+B3YQn393aTMJjXgu2JeT1gzyM3wQpoq7cCBffBnwAtwnOCowWRnn9wL19IgUDhJrgSrAo/BWPCw0SJGR0bqFR34U64GQIOhUd2PS0Gc65ge2Jej+PqsAS1hcYMb/aQ0IPtfReeGWgo4qXLX+1E1iWZDb0JZoQHhOug1B/9+IfZHAF3wj2KKsFWHUVRDOadge2JeT1hrAtTHqyEjuth9gQJwrNBfgxTLSjy8y8+3t3LgE8hloZ3wgpsBjwMouinn/2sSu7Bw0CdsEmwEtb+XU+LwZwr2J6Y1xOeVQwTIqyE2bOH9oQnhD8BEgQDghDBkj/56e/uFbP7HQ/WQZ3wt47b88uvfg6VO6zXGwL8OtwPPz3+hBjMOwDbE/N6wGtgqDBywkpIb4UVPjOIEhwNN8GeuXwS5AiKhFwJGwA/AnfDY6By//iTH4fZM7wHlvBbYfMRBvPOwPbEvJ4w/YXrsBLePLrn7QnNCDoOBQ3rkC7BniDKsKUIHgByDNcFkf7o4x+xXBWSKdwMxarpNRw/Me8YbM8PGSug685XAkJ8ARxfFSW6VucAWBFECpBk5sSvvwoHoR5ydL8OOK5hWiosLVuzba0mcxe/+SKTidvBWYIw6sKDwddwE3QJ9mTYCtzkeBLuAavCT8Ghh8+JwbwLsD0/WMCbpouwunA0VZNBOvAYyGuKInqu0agLnY4rCKRl1WGl2TRMs7a/b9fr7PLy3NDQrampoa2tx56nVav52dnxiYmB5eV5iiy2W67nmpapuo6ha3VYgZuOrZ/EtTXPVnRFsIx6u2nv73uqVle0huVYpm26vmtZpufatqkbauPCF58WMsmmZ/oBrqNbpmLodcA0Gh9/9KNKuQCbDeutpg0/1VS5XucbigivCxIrZNKjQh63JmHOCWzPD5Yje3YL9PA8JjrDaKtKQwQlgYDaLdtxFN/XWy0T1AnehKWmieVyFkgmtz/99Mewkkhs1moMy5aGh+4sP1pQGhIItNV09tseeNP3LNBolzdDwJ5tT+u0zJYHehQ1tdbptPY7+6YN9rRc+E3L8lzXNgxTVb754rOT9gRXhvb87NOflYq50J7gTYGnVEUCaYIxa3W+3oBvAh80CuU8vEx4sV3vDAbTE7A9P2TAlSfVCfeDZZBcNBmsBA4CMYFuajLbaHCQPcGh4E3XVTsdB8KmLNOwzOXiX3/9CQRPiKWGIVPU7rWrFx4uzELeBG+ahgI2VBUZsiesd3kzBOzpGLKlib6jtXwLtGtasD2WXK+9iT1tC565EfLpz39KVvf2224YSHWtBq+i3XLa+yBUdCY0/G6gmTK8Ll6gjr8nGEyvwPb8kHnOmwHh/WbQfRLU0/RR53PwEdS/D+anR0fvXrny5e3blzc3V8CPJFnguDKoMxpd+9nPfufWrUuhVSF7gkb7795YfDhXrwlN3wb1hSV8WMV3eTME7NlpW65Zs416o8ZvbT4t7GahZm+2Wy+zp+8aoT1BjmBP2OCQj372+xxb7ex7jbrg2BpsPNxcfbL02ecfTUwOrz59JEpMq+3AlwS8urBXEwbTc7A9P2S61AmE90MuA7mESgI31Ws8y1RASbduXo7srD15/PBnP/3dL7/4WODJTsdTFZGhS7lsAnQpCpQF4c4zlh/NrywvgDo7nSaYEfImODQ86QlRFIr3k3iObqiCyJa21le++uKTf/D3//bY+LBmQPy038SeYMlQnbDNsKkMXYZAKkssZM9yKT85MQyBdCeyvrX99Gcf/f78g3vpTByqeJIqhlX8Czl6ozCYM4Dt+SHTpc6wbAfAnvv7LvgIal4LamG9zjGV2zevQIKDe0BG31z4/F/8839yt+8GRRbhTgAM+/NPfhKPbYG8YPloCTUZgTHBnlCwQxkOfoTi3dAbcOdxaR4B9oSynSpnnyw/uHjh87/3d/+/92emmu2m1/Tf0J7wpwFYAVHCX4fIHMp0dmbyk49/PNB/S5LZ3b3M3f6bP/rxDwWRhuwJKfvoAtCTHL1RGMwZwPb8kOmS5hGm2QAfgX1ssI+ptJu22hBLe1kFtbz7kOmGh/p++Zd+8c7tazQFqZOA5cT40LWr35DVvYcLM7/+b351aPDOjeuXnjxeoqlyWLyDRjv78LvwtMpxaR4B9qyLJFnK5NKx5aX5f/mr//ze/UlFUxRNfXN7whL47NOf8RwJN+F+EChsye//3m9nMwm/ack1LhLd+K//yl9eefwQsie8HE2vdUnziK63BYM5FdieHzLgzZPqRP0uAxMZWq3lW5Ze9x29JjLgUDBp0weFGXOzU6BIcCh4c2d77fHKw3QqtvpkqVEXtreeTk2OfPXlzyHxJYkoVOuQN2syDwkUsidU7q2m0+XNELCnh7pDOZ2Wvf50GbLn5NQYZE9IqqeyJ8j9449+BFsCNyERCzz1+Wcf/eZv/CuIzGGH0GyO+NV/+cuXLn/VUMSwS/9xYx7n+NuCwZwWbM/vF6E6kYlMRVdlx1RangkCNVQZTOpYWqMuQnj87NOPfuMH/2pzY9WxdbAhKBKwLfAX/K5+YMOggQiAdVjqWh0ECCEUnmG/7bVbLqzDY+BX4KfwnPAXLV1Cze567cnKw3/8i/8Q7NlQG/AIy7HRWVgDntZxTNPSVGTPNOEGrgwBV4Z1Omz8F59/XJM5yM6wvreb+Xe//YOPfvb71couhE0o1avk3t//B3/r+o1LFF0KL0N67h3A9sT0CGzP7xFH6oTUCZmv03abruGaqmMoTceAIlcWWZBdNLL5xeef3L51rVLehUR5ZMkuewKhauFOslrcWH+y9nQlEd9JJWOLD+ceryyCfOGpIJ/u7WZ5jtJQl0yn5aqupczNTP7Df/B3RseGYFMaqgICDe3p2HaYPS98/mkulXiZPcGVEIrBnpCUJZH5+Sc/+b3f/a1CHo0HKsmsINK/9Mu/eKfvOs2UIX5ie2LOCWzPD58jaYYgJaHLfjQo2EGaLUiRYEatbuuNTqeZz6U+/ujHw0N3i3s58CMU47AMvRmq87g9AXArGHZ7a+3ypa9/9Ps//MmPf/enP/m9T3/+M1j+u9/+jd/6t7/+809+ev/eBMtUQW2KTAO2UV9cmPmlf/aLc/P3Xd+VajJkV4ifug6KtCxd77InePXInqFAv/ziE7AnrKuKRJHFTz7+8Q9+/V9C5b7f8faKWcief/vv/PWvvv6UpIqgSPi7z70b2J6YHoHt+YHzQnV29j0o1fWG1HJNcEsTynPInr6dTSfGx4YmxofDUFmTeYicULkfebNLnfBTuCfs6QmehYcdrcP9UL939n2o3+Fhho5a9j2rDtkT7Ln0cPZv/62/Pn1vQjd1zQBfGqjC10DQyJ6G0vj6s5+DPdGpUhcE3W3Pn/z4d6BOBy3CKxIF+sLXn/3LX/3lTDoOQnQ9A+LnP/mn//3A4O2wnzzEz+feEGxPTI/A9vyQeVnqDAv2tmvue5YPynNNiJ88Vf7804/67tyAnOgHF1wC4XVEL1QnAFpUGhJkT3gMrMNvgTrrNSFcCR8DK3ATAR4MWo0csxGPbv6j/9/ff7g4X1fq+50OCPS4PY+yZ5c9wy5KQNhjqd1ylIYoS+zwUN/v/PA3tzZXQZTNlr3wcObv/r2/OTc/Dfb0glHvnntPsD0xPQLb80PmuDqP2xO1s9t6p+mANB29Adlz6+nKv/6VX56eHN1YXy3kM0uLD3YLWZYhG3Wps990HVDhCwh+dGBSSJcgUFjRtTqYENZD7YYmhXVNkTy70dm3Waq4/nT5n//yP4Hsmd+Fcvs5ez5rc08TSLjP2zMUKIRNjq3CSthbfvXJ0ueffXT71lUrGDv54qUvf/vf/SBfSNUbAqizy5LYnphege35IfMyewKmWgN1uoYSsjR//+/+zb/6X/8//vIv/qP//u/+nb/1N//GX/vVX/kXn3/282hku9Npe64NuA4E0uOYhq4cBU8IoeBNqNPDHkvgTfBhaE+4E+GZdYnay8Wnxgd/8qMf/rW/+t/+xm/+myvXrmxsbULlHp73DFuNLE29+OXne9nUUY+lLntC0mToctO3Wk10LSZZ3ZsYH4JA+s3FL65cvfC7v/dbk1MjiorGDTHMBrYn5pzA9vyQeaE9gf2WoysSamdve5ZWh/ipyDwR3Ypsb1TKxaerj5NE/PHKo+JeAdTJsXRozxMCNZs+OiUK0RLkCA4NwyaEUFjCnWHxDg8INQpluNbgObrIkHuZVCybIZJELJNOSpIIwRW8aYA9LdsxTfDoxa++3M2mIXX6PhKoB/HW1G1dAyxd++znH6tKDZQtCJSqivUaXZOpJLGZycSIZCRB7EgyC2V7q+2AQ7vfE2xPTI/A9vyQ6bIncCTQlwAWg9h4Kg5Oib4WePLwr3iWfojhA6YZnJxUPdP2LWffa0Eyvnb5WjqZ9HzD9TSUQG2raTi+Drc8V/M++/gLqkq3Wk2o03VdcGyx5YmeLbgO6gGKwbwbsD0/ZLo+7DegW3k9J3DogT39Q/S6vO+5ULA3ZNk2zLpc//zTzyvVqu6ohqNYsGGG7mmWr9ie4riK88VHn5d2S77vNlt2u22YBmfplGvxro3tiXl3YHt+yHR92G9At+zOg1Cgh6C/a6GxPDzPt/Y7frPtub5z4dKFIlnSW5beMi3fdF2rZVv7ltWxzI5lXPzsE54stTyjLrP1OmvooucqoE7XPrgQHoN5B2B7fsh0fdhvQLfpeohrG0eE9wSTGmmWq9qeJtXZusp3Oq6qSY6rXb7y5cbOU9U3Fd/UPcOEkt9Smma9ZcgtQ/r6k99jytlO22p6RtBIZZqmDsk1eNquV4TBnBfYnh8yXR/2G/BMdj0HpOlZiNCeoE7L0U1XNV2l2bG8FiRQqek3Wm695cg3Ln68l4s5Lcdq+U7LRRNsuGrwI6nliFcu/LSQ2TY1sd1yO51Ora5Ksmo7Tcc2T7wiDOa8wPb8kOn6sN+AbuX1EBe1EaEGIlhxbMN2jNCeltuQZbLTMTtuzZZKnabSkcpXf/df1/PxfQPSpxkIV3NtxbZrliObjvzlhY9Ipui3Pd2ybLfl+B3AcvZt2zrxijCY8wLb80Om68N+A7qV10OQPU2ziWxoQvy0kUAhgaqu3dAkslZOu1SuEX/aofL78bW53/mBuvygs1vwyhWzWlUYWhJYVubJulhRav/mJz9ey2TKNSVHCmW2wYqGINss38D2xLxLsD0/ZLo+7DegW3k9BCIkqBMI4iSU2Kh+92y1ZdaLkbXPfu2XBv7dr9/557848o//weNf+xfjf+OvPvjHvzjyT/7Z7X/xK5d+7d989hu//aPf/dG//enP/9UnX//KZ5d+5fOrP/im75d//NUPPrn8+59f/63f/2xodKbegCfElTvm3YHt+SHT9WG/Ad3K6yGhPVvGQfwMzoHqLVPt6PXMg5mf/O2/8cP/y//53/2n/9uf/Cf/q9//hT/5yR//j774M3/ut//Mn/2d/93//nf/b3/ly1/9zQufX598kv69wUc3Y/yny3s/m0tdepS78TDRN7N2+fZY/+C4psL2g0CPvxwM5hzB9vyQ6fqw34Bu5fUQ0GXLMNqBPf3Anr6l7xtqR61rscjsD3/4xX/1l3/nz/0vfvbH/tSF/+l/2PeH/+SdP/qnP/6jv/Cbf+rPfP1X/j+DP/zk849v3llIX9mSPttWP9nWPn4qX9oQ76xVBxZjA1MPH6+uN30n+ENdrwiDOS+wPT9kuj7sN+A53/WWQ3uiBAr2DBqR9A7YU6l3ymV+ZOz2//O//dF/9Gc/+QN/ZODf+5Oz/96fHv+Df+LKH/2Fz/7nf+H+P/rlR1/dvnXt/kcDG5fjzm8/UX+47vxozfrsiXx1pXplar1/6mEmn7MPZz3CYN4N2J4fMl0f9hvQrbwegup0Qwd7+qbhBuc9XVtvQ+WuNjpktfN0ffyv/a3P/sxf+OYP/ompf/8/XvqDf2r6f/LHrv/RX5j+v/5X8d/7GTE893R199e+nvndh8xvrRq/vqz/aM35ZFm6sLD3yZ35O+NzFMc2GryDe8tj3iHYnh8yXR/2G9CtvB4SnOUEUJdP2zFMx7Qc1BWpozc6NNlJphb+8S9f+Yv/+ZX/4D8e+0N/av5//Av3/uB/+NUf+eOT/81fyX19Ud5O5/bqv/zZyG/ez/94y/j9NePrWPPapnZtqfhZ39zEwiqaNsSod41nisGcK9ieHzJdH/Yb0K28HhLas2middM1NM/UPMN09aaldBpSp1pOf/RJ///pv7zyC3/2zh/44xP/w/9g6g//6Yt/7s/3/3f/r92+vk7dSJekf3154odz6d9/wn2y0bgSMe9uN67Opi8MLCxvp41mS7PhJcOTd70iDOa8wPb8kOn6sN+AZ7LrOUf2BMfpntHwzXrTVHzDctSOrXbqojM/P/X//uuX/2f/y+t/6E8M/I/+8MAf/pP9f+kvLf3GvyLnZjp+qyA0vpx7+psTqz9eyn/5lLm4Qt1ZqV4YXrs28ihZ4tVmW7EhzMIf6npFGMx5ge35IeM4anAq8A2BHeI53/UW10bDKYFDwZ6Ga6geuoYd7Gm4WtNqdCy1s5uf+yf/9OJf/E8v/7E/c+sP/LFL//6fGPiv/++V4dv06qOO59WbndlM9dduTV9YL15ZJ79ZzN8GjfYvDc2u8apr73fslmcHozIHPLeXH9L1euHXgKPR+RAnfuUY6P18EV0Pw3xvwPb8gFGRPR3ljYEHI7WdxEFzY7yAIzO+KeEmoT0PbkLqNA0XMDxPbypiu1S494MfXP8v/vI3f/YvXvpDf2roP/nPbv7Vv5Kf6utw5bahG34nQir/8sLwR/d2vlzM3lyrDG2Uf3pz6uFWSrE9r+nC04bzhRxyfFN119FcF1APUTxX9RwNTZ30PMGDT3A0oPIxjkZZfiHoV46/dswHB7bnB8yp1AmoXdI8ojf2RMMpKQAkPkigPmo+MtFQ8o5h+6alSh29Ebl5Y/jv/MOP/vz/5ss/8ufG/w//5dA//Qel5cmOTLV0zfE7aVr7+dDSR6NPv1lMX39SuL4Yvzq9HC9SkABtS237BhqC/nDc++cFCgZ8Tp2u2/BAoA6alvk4LxXooROP2/O1Au1+BzAfFtieHzA9sGfX7vI2wCYF9mxAyQz2bJrosncHnaw0bRcU2uhADt3Znvilf/Hj//V/9tM//ufn/5u/ufKT36kT6x1N9nXNb3f2RGtyc/fHdx9cXohfW4x/OvTg3kaSVlA7lKZIHd8K7HmUPUN7hvrrVufL7Am8QqAn6dZlFyfeBMyHBLbnB0xQJncr8hXAg89LnQBskuk1TLCno3iWFtrThfhpI3u2PHNfkTscM/wrv/bR//G/+K0//Rdm/84/Svff8qjdjq35hgH2pOpOlFR/79rYldmNGw82f3pj5HFyV7ZcNLpyQ2p5kD1Bc0d5MwTu6VLncXuqPhJoN0FF/0IO3HpEaNVuaR5x4k3AfEhge37IBCcZ35zwdOSpeHYG/bWAzQ2wpxfaU22i9nfTsyyoum0XYqPZ0hodgV/5+pvP/7u/8bv/+V8a+Ue/VFl80KpxHdvwDMPx2mzDKtfsn14ZuDL58NbM8ld3x3byZVEzdMuyTNTmc6Czbl5tzxcDP+oCidXWm7ZxhA8b5oSnCxCO+ww7XOl+uzAfFNieHzJd/noDYJ+AY74b29JfRPD4o6bn12EF9gRssKet+qE9Tcu1bAcMZGid/abJ0sJO5MI/++df/M2/N/RvfpOJ7PhK3Td017I0wxZVk9PsG6PTFwdGLw+Nj84vFmhWaChQ/7daPmzS8Zr6GGDPkwJFdPkxpEujx9DAmC0L0QwJBRo49KQ9kUCPHWmYDw9szw+ZE3J8LXDAo3ORXdiWYb0AVN1bb4zparrf0L1G0HCkhbNpgj0dC43KaTqW77tmo+7w/PUf/fjLX/1XC9duisWSj4IpyLGt6Ibh+ortPI1Gf37x4k+//HI1siMoiqQqYPf9zr5uaoGzuht2AuBHAR6g2l44vRySaZc6Q054E9G0NfBmO+BAoNie32+wPT9kTsjx1cAO8QJ72raJXGkb5kkcw3TfFMPVNV8BTBc1T7lomKUgeMKGmmazs8+IfLPdFFjmzpXLP/7BDyIrT+pSrdnqOF6z3emoptXsdJy2T4ns7338k1//4b+NZ5LwLHVDaxga/Lqiq5ZnWJ7+OrQQMKnjAkeZ9BkvlOmhPU2gZZlNwDZ92/SAoJHKAdwDUDsYgN7P5443zIcEtucHjaVZJroEHJKR71lN3/bQIa23Wy7cRHHpcD+Ax5iGaqHma9Nz4WEgNTTVmm6omqEalo6wDd3SNRNqaMNtus1Os9lpufu+1XTAIuGkRZpjqLYOd9otqJnNuqEIDYkW2QpLFplyupKN7yZ2ktsbO+tra0/h/42Nzc2tnY2dyOzS0sLq400iPrP48JMvPvvxz35y4/at6QcPpxeW7j9cmllcnHu0NL+yuPD44cMnD3/wwx9cuHbh3sL9R2srj7fWthIxIp/L7BWIXDJbypaYEiVSXJ2TdVn3dGffsVqW1/Fgg2FpePCOaLB02+A8DU0S3zQcTzdtxTDrptWwHVVRBF2XLbPhWIpl1AHPVpuO0bKttm3vO07bQZMoAb4NXwCmbcIbaMDXjONYB7iwfKk94Z1/IV0Pw3zHwfb8kAldGTo01GiIqsg1mQcMvRFMrNZsNR0LtKirBujRgCJZN00Dimq36TX3W47v2k3XaXkQ/dz9JixNH5KtKekNQa2xdZGSuDJH7VLlfLWYq+w92V4HqT1af/JwdRmUd39xfnphdmL+3tDs+ODM2NC90cHJ0eHxsdGJibGp6bHp+5dv3b4xOHhzdOTywN3Pb177+bXLX96+DuvXR8auD4/dGB65OTJya3To1ujAnbG7d8b6Lt2+dGe0b+T+2OjM5ND0xNC9qZGZ+2NzMzOPHswtP5hfWQBgZfbRfHhzJxWLZYnUXrZAFis8DRsMW163tHCqELtpQWjVLEUx6qpR103FbzlNeNFNG75v4GsGffEE2Hows7wFXxaW79iebQFgz6bvou+bwJuua/u+22r5ALbnhw225wdN0LBz/MiElVAHYMxQmo26KAqMptZaTdd1HdfzHBAAPM62dJRFLd21G6Yuqg2mJpIiV2SpbKUYzabW45H1RGwtEV3Z2ZxfXRmbn+mbGL05MnhjeODqQB9wfagfbt4eG747OdY/Nd4/PbGw9XQpsvE4tr2ejG+lkjvp9FYqBcTy+V2WLfDczl5hYzeXEbmS3ijU5QRJJapkolJJVEqJyl6inE+Us0Q5E90ltrLRjXRkPR17SkRXojtLO9vLke3xhZmx+emh+xN3J0dujw1eH+q70n/r4p3rX924Aly8cwO2cHpp4dHW+pPIzpPI9kYiEsnE08Us5GKmxtXNhulb3r7nNEGKpmnrJnzxwNsQtJLBm9Fqer7nQMyErxldU2AJd8JNeM8geEJUB0KHhjdx5f5hg+35IWMaCnzGkECBwIcH2RPuVBqSLHEgzTB7QsjStYZuouQJNDRVUup8XWZrEteQwZjp0u5mMr64sTrxcK5/evzGyMDl/js/vfDV59evXRkc6JuaHJmfu7eyvLi1+ZRIbGbS27lsdLdAlEsZiixwbFEUSjVpq5DbKe4mquUUTWZYJsPQKZJMVqs5lkXwXE7gMxKfAmpCpiZmJTEniXlJyIlsRqAzPJnhK8CuTKfZUpavlhWR1Oq7kpDhmCzPlOvCnkjnOTLHIrJMNVktxou7RLkIm7S0vQVbOPpg/s7k5Dd9fZ9evfL5tcvf3L4Gkh28N35vcX5p/cl6bHubiNEix4gcJ/K8JNQaNXhb/Ja/39nXDNW0Ddd3WvvNdqfVhCTugyxNz3dCaULqbLebAPg0OGuM7fkhg+35IQMZM/QmlO1QpAPgU7An3A/S3G9DZEIaBeBhnU6rptRrugowkkDkswtPlu+MDl24ee2jC198dvXixbu3bo4PDcxOTa08fBTdXM8m0wydE8S9Wn1XrsFKhuNhCesphg1JsxwA94dkBTEny/larVCv5+u1fE3OyRL4McNx8UqFIMmCJBXqtQTPRjkaBJqWeMihGYlNS2xKZJISHUDFuPIOXYxzlVydLzSkJE/H6ArBUkiyPJViqxkeNowtgHZBxxxsCZ2kQ5hww5I0myTpeKm4k888iUdmV1dG5u7fGh26ePvml1cv//TzTy/dvDE1N7u2tZXMZPJ7e6VqtcrQNuqRbymWJmv1mlZXTNXyLBcyO9ToUMCbkFKNsHgPTxxje37YYHt+yEBh7jqmaUCZ2bBMkKa93/bBknCzXhNrsgA/avqOptYzaWJxceHe/Ez/2MiFa1d+9vmnH331+ZW+W1OL8yuRzZXo1maWSFR2k3QpTu4BKbZSkPk4RSUZPsNLQIoVCJqDm7AS3pPmRABuwp0AwfBxmo3RbJRhIwy7wzI7LB1h6RiEUFHICpAfuRTDJDk2LYvpupSU+QRHJrhqgq/GeTIuUDGRiop0RKTjNS4mMUBCYhMiG+OoONhW5FICk+KoFKxzdJpnA7gUxyVZWIEAK+fEWlaQgYKs7NXVvMDneCbDUlmWyrM0gqFyZDWSzTzZ2Z6Ynb12584X31y8cPnKnYHBsXvTa7HtaD5Z5KqiXjeattlyUEOZrbc77f1O22+6ULAbKLtrYUX/MnseFQFddD0M8x0H2/NDxnMtOIbhYEbn5tDJTMvQ1UZd7uy3NLWRz2UWHszduX3z8qVvbly/evfunbF7k/cXHzzeWt9JE8m9fKZazFLlDFVOksUMV81LTFagEkwpUi3sVPIRshglSbBhguWTvJgSJABWALgHIDghvAn3p0U5LdZSgpwUanFBjolSTJaiMixFQhaTogACTbNsrFLZqZTjLB3nmB2mSohUUoQlmYC8KdERmdmR2R2ZC+C3RXaTpbZBnSKXlAVCgHzKpUQIqnxK5AkeNoxJsCzB8eEGZCS0AQlWiNE8QLB8GvmaIqgqkKLJNE2mqhWiXEpVyplKJV8ld0kqV67EMtnVze0HKyu3xgYv9t/49MrXP7/05ZX+mzOPHqT2slxdEBuSaqioom/5rabX9F1Ytl/eatQlzSO6Hob5joPt2VvUHtH1tAc83z1TO35Z+nNd013ddGEFDQFnWag/uuM5pm1yApdIJpYfL//880+/+ubrm3dujU9NLK082oxsJdIEkUtvp4nYbi5DlvMsVYDil6MyDGilkhOZjECneDLBVuIM2K1C8CR4ihCEBCcSvJQU5JRYg2WcFSIUC/cA8CMA7gFiDA/EWTHGiVFO3OHFbUHY4vlNjt1mGUKEhzEJhslJoDkxytARhiYkPiFQcb4S56tRrrrDk1scuclTGzy9JbLRmrgj8mDPiMCmGzLYc5usxFiK4BlQZxJeKcfAcxI8lxYPnB56HAQKHg+Qs5KUESCcQmJlIKiiGp+mCLJa4PlktRrd3U3sFXMUnYd6v1TeyWY3c6n1HPE0FXsU2bj/eGl4dvrmyMDlOzdvDfXPPHyQSKc4UdAM3YC4b6J3HqLoEYFJDwgv1nIQ2nFcG42CGtJ1oD7P0T7QdT/mnYLt2UNC8SmnoRHQdadqmHXDbBiWYoExPRMAJxqm4qAxhTXP0l1Tsw043vSgGG9C2WiiY9H0Oy3NMWmRE9Q6lOiyplRYeisRu/dw/vbI4I3Bu/1TY+MPZqYfLcw/XVmJbW3nU0R1L8NUsmwV0mVGYNKQ3Y4BikSW5OlEiHAcJiEIcUE6NRA8n0OMi2JCeI44WkKc5CBRhiRELgSSZlzkE0egxzwDvPk8YNIuhAN4AFLzAakTpLnn4SGu0giGSlDVneLeWia9Eos92tmZWFwcvHf/xtDwtbsDgxNTC49X49l8kaJrqqromm6ZBlTyqEXesG3L91yI/zZ8jvCNpiuGUnN0pWnrLcdowj2GaumK75pNz7Qt1YIPHVnycCyCYOSXY7sK7HXPvkFfTdeRj3l7sD17yGnVCbzYnn7TdD3DdFTdauimAhhwINnIm+iib0OzNMVQFUuH48r0PBdqxVanYzU9GY69/abb6RQ5ZnT2fv/k+NC9ybH5++MLs8iY64+fEJGtQhqJkiezIp2TmVyNzcqQ16BGppISS0jcSeIC8yJYsB6o8LwQRALJrsuGPSQwKXLoGwFBOyWJaUlICeBWSKwsQdMERSUpaiObe5IgFjY27z1aGZtfGL4/OzB1r398cnZxaW17p0hSqmn67bbXalqQPA2j2QSF2hYSpdpy7U7La1qaKvPw+YI6gSCKqqbRAIE2fbNrrKxgJwl3G9jrui35CroOfsxbgu3ZQ05tzxOHBIACLKgTsF3DtLVQnbDuN20o6HzbbLr2fnB+zYXbhlpX6pbvOu1m3dRTxcL8k5Whmenb46PXhwf7JsfGHswurK+uEbFIIUtUixmWLIhsXmKzIpMWKIKrQhke5yoxRDUhMgkRtcN0ccKbId87eyZFFIcTHJNgUetWVhQKslys1/dkeU+S90SpwPGpcnUjmX64vnlv6dHo/ZnhqanBiYnRe/cePl5J5XINTW13OoYFn6+NetY6pmsbbjBhCRQWYc3uOQZkTzg4TUOB7OlD8XEQPJ/bYYBgr+tW5Cs4fuRj3h5szx6CxHdkxjfhxMEAv46eCsp2C44WqN2CPtuGpQGwEp4ygyoQ9YzxHGRTy2gYajybnnn0cGBybHhmum9q/Mpg//DczEY6maqWszS5y7O7IlcQ2BzPZAVEiqOSPKiTRI3aAkROJlXj0nUohLE9XwrYM85zMZ6JMlSEJqM0mWBoCKEZgYcEmmKYLMuBQEtyrShKeZbLkNR2Ov0ksjPzaGn43vTdsbGhyYnp+bn5R4u5YoEVOORQ33HgIzZUQ1eCK2U1+HxBqb5nA0EXXRU4PMvZJVDYW7r9+Gq6Dn7MW4Lt2VtCgb4pz42/iW7CM6C9HIp0tILG6UH21E1IoAi/5cHxZjpQzKu0wCay6cUnK+Oz9672990cHRqevTf39PFmLp3l6ILIpxmqANIMAGNmeDrNUWlYisxBD0oZQUhMQmbiEh0/UOfB6cXjIFG+AO77Zk9CEhISfMdwCZ4F4hBCOYZA7V10nCLj1WqCJJM0nWFZMCnq/8/QBY7Z5ZgsVd3JpB+sPhmYHL/Sd3tocnxi5h58dulCtqbUvJbX2m96TdcPrviEkh4NV4ocisYisEzYPcLD9dgJUMTB3vLmHB32mJ6A7fltcpgpDkF3wl5uQB4JWswNCyo43/FaPoAuM295klrPl/dWtzfuPZzvnxi9Pnj30t3b08tL0WKB1BqlupRkqkmWyopcmmfiVCVBVwh0D5kM8iaKnDwFeRM1ZwtUTKCiArnNVTaZ0iZdOuHHV/O9sydqrZJ4QhaSAahXQODQjCymD0+GpoM0muG4NLp6is1xdJalcgwC6oB0tUyUdtEp6YnR2yODg5Njs0sL24lomSHrurLf2e909j3PDS74Aj+iBnowabACh+sL95ZTcPzIx7w92J7fJodHwsGHAft30OUIedOwdN3S0SBuPsRUk6tJZYaKZpJL66sD0+OX+m5eGeibWHywU8iU61K5UcvyLEgTjJmCmpqpxlkyLXFhu3kSSnXUu4hJ8HScp6JslZBYQuaAZJ0nniGc8OOr+d7ZMyZwUZENGv0RMYGFQh7suU1Wdqgq1PJxhk7y3JFJswKXAbdyDGg0L3AFgQOTpunqLs8kK8WnROz+ylIf+grsH5yenH+8nN0tUAwt12p1pd5Q61BtmGBPtGMYxyUYEO5CXXe+hqM9DdMTsD2/TdCg6wefBBweQdh0DBPUaRu6bQSdkkxRbeTKxaW1J4NT459c+vrq0N37T5Y2skmCLKcZMgWhkiHBmzE4eqlKjCEJgU3Csc3RUfgpBCU4wlkwJhnlqBjYU2BioFeJjUlsVGQiAo0QmajExmWkgzjPvTH8965yl8VEDRXvB+o8/CIBh8Y4JhZ08ke9poIOp0mOJVg6ydIpqNxFviCLeZFPc3SCLGdYKkVXc+jCfKnAc2tJYvzhwu3x0U+//mp0cmInHqtQpCCLqqFZru023XDHOEZoQ9iFnpnxTTg67DE9Aduzt3TvryG60Wi1Xcc1DFPxfMu0VFWr+U17v+M3W45paopSh7CJTn61vIahKHCPZQhKPbmbm3wwd/H2zct9t/unJ9YzRKy8mxeZ3ZqQAUvCQQiFocQneZrgIVqik3GolgwPaYFFJ+leBASoIyKHwDpkK+TEN0f43lXuR8HzOOhMqIA46nYakoTPKCAFoMtGmZCDa0k5OkimqJd+lufyHBcv5EfvTX/85RefX/zm3sJ8trgrKnXV0oW6aDeddqcNJjUd0206XtMGh7qeCXsX7E4WVDCuAbsWcDS+/Qs4cfxj3gZsz97S7c0Q2LnBlbD7gj0BkCng+XatIekmusLPQeNEomHKIWw2TI0S+YXVlS+vXf708jeD96dXEzHUes5SBF3JCEyhJuRrIpTkBIekmQYhInUGjeCgvwDwIJBArRwvICpCBcpFnieKfuWEH18FVPrfL3smXmHPEwQCPbDnM4cGJKFy5xBpKPMRfEYQsjwfK+7tckyRY9cT8b7x0a9vXIWC42l0W9TqbF2UtYbd8tx9X7P1BuxCtgY5FPYr+GKGJTgUdipYglKfM+ZxThz/mLcB27O3dHszJNyzYfcNgycAdyJvNp26VhcbkuHZXqclqHU4VOCA+fLalZujQ3NPH+/s5tIMVHlkkqoSdBWkmauLWTT4EIeu/4EykIc6PWi7QGET6e84cRHi0guAH0UBkY8cA+5Bv9KtyFfw/bOnEDS4vxkn7PnsTxM8i+C44DKng+dP8XxVU+HjjuwVQKNEtbSZTd5//KhvcuzC7euD9ya2knFRbzidJppQCaTZaTXUGuxLoE6oYMLUCeqE9We67OLE8Y95G7A9e0u3N0MgHUCpHgo0SJ1ocGIIDrJac9q+39nnFfnBk+Vvbl2/OnBn+tHDlej2RjaVIMthoUewNNR9WRmN2AYHZJyjQ1DkRAUjiCzMm6A/MOMzgvOSLyD8afR50ONBiKciuOzyvPgOnvcU4P0PtfiGvMCecH+ChyU8FTwhvMYD4E/sVMpZka8aWtVQswK7Uyps7+YixfzEo4Whuembo4N3JkaWNldLAl13dEmvtzrN8PsYChrYzWAdwNnznYHt2Vu6vRkCrgRpgkDDEAoyDe3Z7Oxny3vjc/dvDg/0T41NLT1Y3Fp/moqnGTLJVAmWSolsviHllVpaFmIsFQ+aJqIMBUt0cEIhyXMx7uBkZejE43RJ84jnHsYf0G3G1yN+r+wZAFERAiP69WOKfAUvtCd4s1udQILns3U5FZyHSQpMRuazMp9kyUhlLycyCbI4v7l6bXTgm7s3h+furcZ3iN0s2DNMmqFAj3jOmMc5cfxj3gZsz94CrkSt5yfRDdinzfa+7zcd09J4gUnn0gtPHg1OT1wb7Bu4N7G4tRYr5jNMNcNSCbqCOhtJXISprlf2NqlysibsmeqzqhBCKAA1OAePYcI6GklQADO+Er4bJMEz8z2zZzB6CBdy4tleyEl7HqgzGAxFCjl8P4UtqhIXWfisEyKLukkwVdBoVuY2S/lodS8r0Gm28ii2NTg33X9/4t6jB8urjzJZoqFI8H3c3vfCRiSgW5pHnDj+MW8DtmdvQRMrnqRWk0xThxVdVwWBS6WIpaWH92bvXbx1bXh2ejUeIcp74M00U01Q5Z3yboIl4ywZgyVItiFntHpUZJ8UC2DMZ/20xYNuQ4fZ8w3seWS9ExyMJnfi/tfw/bMnGm755YSx9BjHvQnAX0TqTIoSIcqH9pTjPAgUOTTdqMFnnVZrMfjiRD3MUOfcrWqRdPQoVXqaSxJ0ea8uxMni3MaToZnJ63euT02PbW2vVap79YYYnk/vNuZxThz/mLcB2/PVPLuq8pCuBxwH3lDYQU3HtgA7wHIQru+2Om1BFiOJ2MOVpcGxkVv9faP3p9ZTCaJaLAgslOpxshReFxSjKxGqHJzTZCIsuQMFO1RwDSlZE8O2iINmIo6Jcyz4JSVJUPQ9E+jLCcptIfFCuICuO18Jeiqw57kB6QyJJjDO+RA68WDozzcA7BnGz5eCSvvn/sQzaQagv0jAU4kyQAgSgpcTnASAPWMCn5AFIMozYE9C4lI1gRDZjfJuEEL5BF2NkeWMwORlPs/TT7Y3pufu3+q/0z80sLSyVCgWpLqk6oodTCh/MKc8AtvzXMD2fAXhtedd4zIggaqaDEsol5otx7I1Ta8bpuK5lqmpqDR3XVPTPdfd73S8VpMReK4mF6jqk8j24P2pK4P9/TPT65kkqTVQ178jBAQcJCHE4biWcPAcgG4enVA7IqwEvxWCM3fBQMjnRpe/PgROvMZDgqx94vM94NgZAEQKvnF5tgDKLlceRyLD9+9f7b97e2Ro4fFKPJNSTE1Sarql73da7X0fvIlGG7FRZw8LvuM986hxKaz0YT3kmWddPEPy68H2fAVd6kT2DASqdTq+ptckmQNvgkBBo7ALojmCHKjdDdswm57fbDZN26prqqwpi2urA/emLg/cHZi7v1HIFpV6ThY3y3tdh8QbEGaZ7wgQtUAHEqZHiMfi6uuBqEvQbIbh0zQb3SsuRyJTS4uD01N9YyOLq4/5umz5jlgT5Tqq6D3fhJ22te+BOnVT0YwGGi7Wt/ymHZq0S50HnPAF5jjYnq/geXseDmwDKA1xfx+1m8N+GZ5vQrugA/5UW2jGb1eDPGoaoM5oiugbG742cHf0wdzjRDReLQV5E6kwJcIxcNyMb8Jzx8+3DbZnbzmdPZM8n+HFvCiX6o1SrZ7nuJ187uHG2uTC/PTD+dvDAyvrqw1D8dqu07Rc36orEkjTdg2vacNNsCcUTPCtH17H8WJO+AJzHGzPV4Ds6dohasDBuB4uOsWpgTQNU3Vc029C/a43GrVWq2kYhmmhMTijycTV2ze/vn5lbG7maSIaLRbyArtbl9ICG2PIKF1Fk/B0y/G1nDiEvk2wPXvLqe2ZlSQ0ZQjLZXm+IAp7krDLczmGWtpcW9la7x8fvjPST+SSDaOhWmqr01RNBV3f6VuQQI3Da5PAkmH9DhxI00FlO67cXwu250uBIr1LnSHgzXbLlSVOlNhmCxJoS9MUVW2EkwupppHdK4xMTXx97fKd0aHFjadbmWQBkiZdjVZLMaqSEtlsTUzJfJynsT0xxzilPQUuRtOJYA7njMDnEFxB5Auw5JhoIbuTTc4uP7x488rUg/ukyDRMBQQK3oQEqur1UKMQRcNJX5A6AyOE3vQC3OdlgekC2/OlBPZUvQM04MiejbroezZ40/PR3BimpTdbXruzX2Hp8dl7n1260D8x+jS+E9/LZ6hKVanFK8UkS2XQfLlcnKWiDOqKlJR5bE/MMU5nTwJds4Su083WpKwkpoPJQdPBgHhphtyTONj3UpW9WCE9PDN5qe/GzNL8XnVPgl23Dftqy205mqVCGg2nLUDj4FlgBMib4E3TQ4MuoFlDunyBOQ6250txHDCm6luqZyF1HtoTvWvw5byPvsYtuS5ZjgmpU5D4pSfLF29dH5qZerixup6Kp+lKVa2VG3KSqeYkPsUzCY4CA6ZlMVUTIxy9US1he2KOcWp7ZutypialJAG1y/NMWuRysrhbl0qKnIcSh6MSlb3IbiZFFpejm19evzR2b2IruiXUBLsJwdKs6w1At3XAsECghm2BLk3PsTwbwPZ8DdieLyWwp+aDOo/ZM3jXDBd5U1S0RrvTdnw3kSJGxkcv37w2vjD7KLqZossVVd6t8QRdiVaLUK1vV/YgckLwTPDMNlXdpqsJic9pDWxPzDFObU8Czcl8cO1ZSuLTsBS5JE+jDnAcleHpvbpYbkgZthqv7OaY6uDk6OD48NTcva34DtcQnY5vt9y6oai2rtk6Gok5sKcb2NNH82Zhe74KbM+XAvZseqahyKZW22/a+y3XMlWgve831JrtWoZtFMp7c4sP7gwNDE+Oz60spagyQZcJpgIkWZLgAIrg6aALZzDy5uHgm1GeAbA9Mcc4vT0PB2wFCIlLSujUUEpkocpBOx5LJQGmmoS9kalAMZSny5uJnYm56f7xoYdPl0s8qXom0LA1xdJ0x4C92rZN09BgX2+jWZNfbM/w3OhJuh72wYPt+VLAnhA2Ox0fUBuirtU7Qd9jTmAgcvI1YWl15dZA3/X+O/cWHxC7OVqRQZ0JthoSzFhJIQJLAkfqDOyJBiTH9sQc4/TnPZ+3JxIoZE8RzSyARi/kaORQhiSYKpACezLVTGV3O5N48HR5ZG56cmluPRUtCrTiW06nZbU9xdQcz+l09jv7+w1ZwvZ8Ndier0JRJJAmvE22ozcUCbwpNyTLt9d2Nu4vzt8dHx6bvfc0vgM7ZUlkc5A0mUocqiSw55E6OQp2ZbSjo0GLj+zJxTgWwPbEHOMt7RmCEuiRPWH3A4EmGBKAEJplKhWZZ7R6gafmNp5cHRu4MTk8vQo1U6naELWWC0nB8T1d1y3T3G82g0akbmUAXdI8outhHzzYnq/CDIZd0IyGotWbHb/VaclqbSO2dWu4f2B67P7K4mY6kefIcl3ICzQKnhwZ50NAmsibIajIClpIA9DwjnEOwPbEHOctK/dDewan19FMLQfxkyZYOsGgECo3nSxb3UCjjZTKukxwlcm1R18O3vp68Paj+Baj11XPVizDsEwbCngDBIHt+SqwPV+FYale0/b3Xadp1/V6obq38GTpxlDf8vbaRiqeqOym6HKSLiWoYoqr5mqw11IJgQaQPRFomrBwprBDe3IEB/AJlkuwXWZ8E7oPoW8VbM/ecgZ7HocnRB5NSBcMInMwfD3AohnngSRLRyu7OZHerfMpgdyhdnfovShXjvOVW/OTVyYGRx/OZsiS2fRs39MMCKAatuerwfZ8KRbgGQasNy1JlZeeLn9++evLd2+sRDYihQwUO3s1rqSI+RoL3+FxphRjysibAvOMwJ4JjjnY3QN1JjkeIFiOwPbEPMep7YnOcgalOuJQnSmRf6ZOjk3CtzULDg0n+KzmZAbUGaH3IlwprXAphYvwZUKmxp8uXRq5Ozx/bzuVIHlWt639zj6256vB9nwpYE+nZWuult5LT8xODkyOrOysEaVcvJQDdaaYcoqtJLlKkq8mBTIl0ukaR4hMQmSBbnuiwR9BmgfqRDDYnpguzmTPZzyz58EDAnWGe13wbc1EqVKuxu4Zck4TokJlg9ndYPe2+BIItGTVt0rZS0N9F25fX4/tCHWZF3kb2/OVfN/seTDEHIDmUn+Og0t9rUMM1yhx1SfRjbuTw9eH++49fhgr5fYkJi9Saa6aEShInVn4JpegWicJkUpKB+o8smeMR2V7HHZiJBoeCvaQY9kT9vIjM74Jzw6e7wDYnr0F7Hm68UxTogCuPATWDzh4wMEX9lG5wxQaIlRIW+RuDL7y62xcpmMylUTxs7JRySfYSlagF7bWvrlz8/bo8E4yYbiW6ZhWQDDyN/gR4TpmlzdDuuTywfO9sqeqW3XH1z3fAF26ttbyLd81LUNRG3Kn0/Y9p1avWa7T7OzXdHWPpe7OTl4Y6bs5M76SiSd5MkaXonQpV+fjXCUG1TpfDQF7JgQqjsp2Nhjv/Th84uWg+PAeg+3Zc+D9PBUv+y0QaDj283HbwkcWDh3LQJEU1EkHwHqcRx2T0TTXLPUkkxxafPDNQN/08uIuR2m+Y7V9u+WJNUmuS51Oq9lyHTSZkmLZqusZDkJ3XDSn/JvzAdj2+2VP29MAy1YsSwF7Nj2z6aEr0jr7bUkUVFV1fa+BzpY7JY4duD99dWp0YGVhOZ9MymymIUS56jYIlKuGxMCnAXGkzsCegS7fkMCe4T79noLt+a0jB3TdeSjQ0KHP7WNdpcwBaK7WoJk+JXIZWYCV9WLhUSp+YejuyMP5rXymWhMbjul39ludfdu1XA8NCer5CFCn5aohXX58PSd89H7x/arc4evRtBqm2bAtFV2xbmkIVIyY7XbLdh2v3TI898GTx59dvnR7avze1tpauZCuC/D9vMNUYvD9LEFVTsPKcYLUGbQXYXti3im9sScYE13iGYw5C+toDDCRQ12VqfLV8eFbU+NpqizbuupaUMvX1brlGJ5vhZPIw2Fl2qE9T5c9Ec/L6L3j+2VPqDVCdfoeOnGjaw3AsnTdUOtK3W56sq4trD6+dOf20Oz9p+kkqmXqImjxaamwXtlLynymIcV4sOcLwPbEvHN6Y08Im7CEBArqBGAdjQRGV3aqxYXo1sjSg777k/dWlko843Zamq27vu01QZ22hUa5VdDB5epAtxxfywkfvV98v+wJBTtKnY4OWKaqG/DBG639ZqvTrutqspAfnb1/ub/v/uPlJFUlGDIlcQmRjXIUEKoTbm6SpaBF6Dl1hmB7Yt4tvbFnjCFBoGBPSJ3AYRRlVguZolZPsuTUk0d99ybuLS+mS7tO2/c7zea+Dw4Npo9vuB5EUROOr245vpYTPnq/+H7Z03OhLkczZOl6TdPrtms2kTr3SZ5N5DN946NXBvsfJ6I7e4XVbCoj8TEWCnOakDjwZqomwDpoFG6imHnQqo7t2XXoYt4lvbEn6PJgfKageAfgZloWioZaNtV8XczL/M5e/sbY0OW7t7aScRmKNsdwPMu0NctGkyYBEEK75fhaTvjo/eJ7Z0+o2S1LVbWa5eitTtNwTZJnZh49/Ozqxbv3JgiynGKpBF3dVWT4EkbDv0scpE4Q5TZdAWAlq8gn7RnmU2xPzLulN/bMN2QQKMTP8OLO8M4Ez2yRpShTjVBlKOFjZGktQ4w/nLvSf/vJzkahutcwGpZnoImS0LxyqAm+W46v5YSP3i++d5U7fMyWremmGl63Tknso62ngzNTq8lYrFoEbxYaEuxMkDqjLLnDVMP+mxGWBMKOnKFDX2RPnD0x75je2PO4N3N1tP9DdEiK3J6hbFNlQmBLhpKV+Azs58X8zOOlK/23ljdXGZk1PRMSqIPm49QhhHbL8bWc8NH7xYdpz/AsTNgfDT5XMGaIZjQMS7V90227qqOlS/mJhZnPb1xejGyu59NJjs7WhBQU5siGqPNmjEc93t+cLj++GmxPzFvTG3seJ6zcURUPSDwiGDM0LbIZcCvP5Hl69smjO+NDozOTJaZc1+utDhxvmlTjmy1HUWVIJ+19L/QjxFKo857psosTPnq/+DDtGX4TAuF6CGoTbFrevud3mpJee7Kzfmts8O79iaX41lYxF2cqaZnP1EUo1SFIRjg6dlp7cmyXH18NtifmrTlnex5c/XlwT1oAgTI5nt4VmfHFuaH7E8P3xgvkXpWt2k1bNRrgUHAiGFM3GvWGaDv6fseHm88Z8zgnfPR+8YFX7qFDQ0xHremyve/KZmM1tnlnYqh/ZuJRYjvOlFIinZLZpMwlJA6kGQ78ju35OrA9v3XO0Z4BR5eBojalIIEyGYGOVgqMpTxJbA/eH7/Udz1XKfANQbNVVa+DK4FwouNXeTPkhI/eLz5Me4IrUdgMO3gG/dHgpuGoDVvZ4yrzTx/dmhicXHmQoPYKDX6b2kvKbFxiIjwZ4Q9TZwiPxjB+c7r8+GqwPTFvzfnaMy3wIUf2DAUao0s5idmVmaep6IONlVuj/Q9WF9kaZ9r6kTo7nabrmWECfc6Yxznho/eLD9OeoTGBUKOttuM3LbAnp0kzq4sX7l4fWri3VcqkZTop0QmZiYn0Dk9uc9VtjoTUGRfROPCH6uTenC4/vhpsT8xbc772zAh8ludhGQj0wJ5AnK1GqSLBlJN0ERien7o51n9vcZbmqdCVhqmAOkGjml4/Ogf6Ak746P3iw7SnbtTBm+E6eLPZsjW9RnLV4bmJ/rmJe2tL2+VsSqKibGmbLcXBnjIbldiIyERFpE6UOjk2wtBdcnwtXX58NdiemLfmHO2ZFjhQZ44XDgWK7gxOhrIblULFUuJMiWDLT9LRNFuaWnlwfejOzIP7JFXym+gaJN1oBNfCWzh7vmcYZgOWYfEOGq03hEw28eDR/Fe3L89tP87J1K7CEyKZkMiYRG1y5SgItMbFZD4WnPdENTikTvZ0wRPo8uOrwfbEvDXna09QZ44TQoGG9XvQEM8WNHmzWsgrPAi0pIlPs7FIKbMa37o7fHdxab5YyqP+1LYG6gSB4jb39wsdPjn42GyoICy1rkh7lcLcw5nPL325vZeOUntpkcrUGEKkolwlJlKJGgdle0Sgo2LQXsTRMZZO8PAdKwSzD50CEOJp4LA9MW/HC+0JhAINHHog0BD41Lp12cUxe0LZfqDOw+L9wJ5xnopx1ShbTon0VjlXMWpFRYzsZVY2V2/evTUyMbJb2lX1hmYojms2m24wHuhzOOHycMDQ95T3256uj64VA11CsWBbmtKQDF1ptzxR5Fr7vtt0DNfciu8MT4/fHLq7RkTTPJUU6JAToxyiXvEEEMweHBBMHSOghHhuHJfRewe253eZI4Ee0ZVDX08KzYnAAkduDY6LYITQgGDAUHQ0pQQ6zdN7ErcS2RyYGh+dmU5kU5qpe01P11RTV9u+B1i6Zht6p+03fadRlwKNWm+I61i2ZXyneL/tiS6zDQaEhxQNHw1407HNek3yPGe/09ZtYzO2c3tkcGB6fHlno1gTUujDfrYrvAHd+xPmGNie7xenm/kjoOtweBVwcCXoSpohH+1s3RgZHL43tVst266jaVqn1dIbDa1e2weHei6s6Gq902l1+fG1dMnrW+f9tidU6KBOSJ2hPVuoRoCvN8X1bEEW42lieHqib3xkYf3JrsiUVRnbs6dge75fnK89IYeCPbMCk6Iqyztb/RNjg+NjkXjMNE1d09q+v+/7pqpYmtppwrqnNepwtHb58dV0yetb5z2v3D0T1Ok6hudalqmpSg2yZ6fTVnV1YXlxaGJsYn4mkk+nqFJFlaLVvaDQ6PrIX03XzoQ5Drbn+8W52zMtMjvl3TRHVZXaRoq4Mdg/MDocS8Rd17UM0zaMpmND9kRjMwUlPLbnt4qlQeRs+jaoU1Pruq46ruX6bq5YGJmaGJ2ZfhLbLtWFrEAVanyELGJ79hRsz/eL87Vn0JBAx9lqnEEJtCCw0Xxman728vVr+d2CIAhwlLqWCdkT1Aka3W/62J7fJppa2295Td+B1GkYWrPtS3V5JxG9OzI0+WB2J5dK0+VMMJtbWgpPb2N79hBsz/eLc7fnNl1OymxKRFMfJ5lqjiHX4lHIMVduXCdSSdd1QKC6onRarc5+21AVbM9vDQeWpgbfZq5tQsEOqRPUuR2PDE+OXx/oWyOiBZEpyCyoMylSMZ4kpLCVsPtTfyVdOxPmONie7xfnbs9w7q+kzKUlLlotbuTSO/lMrlqGQ3J0cmI7smPbdqfTsXRdq9dbHmql6PLjq+mS17fOe21P1PVBqcma2uh02o7nQOqEz+nO6FCyvMsYDUidBFeNMqWCXtvhKgkZ9U/q+shfR9fOhDkOtuf7xTnbU2QJmY9wFBoVF+o8nk5Q5RRV2eXoRCE3u7gwMjG+sbVZq9XarVbb95sutuc7wW9alq3qRj0YvxqN1xlcR6S3W57vu+39pqIpG5EtqBGmH84/TUSyHAnqTAkUpM6ESMclOiahJbZnT8H2fL84d3vGAQkt0RzxUOfxdJqjMiwV38unirsg0MHRkTiR0HXdgH+6hmp522y1fDCjotTb7SbEIFkWjxvzOF3y+tZ5b7In2DO8dD28/tIwG7rR2O+0VF1paMpetTQ1NzM+d38znYiV8l3qPLQnzp69Bdvz/eK8K3d0vQkS6NGFJzxKoCDQNF2N7ua2UsTs0sORyfF4krBdxzBNMCjYE7wJ0gQ0TWk0aqiOPOHNkC55feu8H/ZUNTlMnZ5vhsETsBzd8W3dNnLFAqhzcGp8IxUvwJceXXpencwBwZVFXR/56+jamTDHwfZ8v3gH9uSOwSZ4+kig8Uoxx5BbaeLu+Ojo9FS+XNQtsxGccwuNaVkG4HkO0CXNI7rk9a3zftgTanZYgjdhCRoNQyiaDECRS3R1emGub2wY1JnjyDi5lxbpE+o8/D488ZG/jq6dCXMcbM/3i3O35+HFzVwCgBAqMAmAp2N0JQ0mhSVd3c6m+yfHR+5PV1jaDHpngz2bTY/jGCjh4aYo8l3SPKJLXt8674c9w9QJK6BRTa9B2Q7LWkPcrRaH703eHB7YyaVIrRar7q7vZQqqeCJ1InVie/YabM/3i3dnz1CgcQEJNC7QMY5McFSELOZEttyQlyPb/dMTcyuPSIZyXbtWk3zfhRIeAEWapn7cmMfpkte3zntjT1Sq2yoET9Boq+3ACivSDx4vfXP7+qPtdd7WIHVGqWLFVrbpUniu8yh1PjsRc+Ijfx1dOxPmONie7xfna8+Ag18MBYqGGBcYINOQNsligiOzMpdiybzAbmRTN4YGJu9NQfAELUqSAKkTanZIoLBy3JjH6ZLXt877Yc+wyQiMCev7Ha/Zshm2srGzNjg1NvN4CQr2XZldzRJpiaGa1mo5FztsJnqWOgN1Ynv2FGzP94tzt2cwJhP6xefsKTKpuhhhq0mZTUtsjCzt1oQMS808fnSj79b6+lPQIkVVoWxvt5uapuDznmdGDei6E/VYQgP92+hcp+0avMwury59ffWb2SePCLIUKRc2djOFhghfbo9302lFOAiez4871/VJvxnH9yRMF9ie7xdgz+Njfb4J8Cl3HRGv4qQ9oXIHge6wZFaRw25MRa0epypr+cyexD96+uRnn3xUpaqqrmq6pmlqp7NvBNe/H+E4puMGYHu+EtXxdcuFjNlALeyO5rmG55quY9Rq4n6nbUK97jvNTmdhdeWzSxeWdzYTVDnOVJM8jSarktAVDkmRTUqgS9S8jjg4HYPteR5ge753wOf15px6PNAjQnu+iIMjK8WzGY5N7O0trq5+feVyOp9nBaHZavue56MxReBAt13LsAzVtiE82Y5n6kajS17fOt8te4I6gTB+OjbCtgC92fRqjZpqGrKmPN7evDMxNr28tL2bi9PVOEsRPIOGwpZ4AFaOue/teW6fwDwPtueHzRkq/VOAUmq1WpWk+ZXHF69f3y1XHNeTZdnQdNswfNvybdO1ddtCNrBQZxsVVNDlr2+X71blbtkKqNP1DNSp09YtUw3QOp12XW2AOqPZ1KU7t+5OT2YYstiQEiyZ4GjQ3NF0ArAOMj2mv7fkuc8b8zzYnh82527PUr1GlIoFmr7Wd3d0cqpCUfv7+4YOioQ602g6VtOzoPpEA/i64fwR2J4vxwomEAZ1eoE94c1C84e4puWY3n4zW94bnJ7svze5moznBbaiK6gvbqC54+rE9nxXYHt+2Jy7Paua+mBjoyiK6XL566vX5heXLMexbRuNxWRotqF5tuk5JkQoKENbTbgb2/PluKBOBxHW7KBOz7f9lqdaepVnx+dnrw0PPE0TaY7aLu2mkd26RYnt+Q7B9vywOV97AqiViWEIslqpyavRKCRQECgnCI4D0VPXNQWd9zQ0y4AVxTY154S/vl2+W/b0XMNF3lQByJ6uazmebfkOvHn3lxauDt59uL0B6kww5E61GGPILtmF6sT2fFdge37YnLs9d8hqWdei1XK0VJQcZ2Bqum9kdOnxE1XXLQtqdsd1LIifUMJ7YCtdBad2+evb5TtkT8fRfNdwLBV9z1hgUtPx4IYmqfVIJnl7bGjy0UIOtdYxO2QpKbIxlgqb8M5Bmkd0f96YY2B7fticrz0JgY+yTJShYwwVJau7srydzy+ub/SPjSezOVGWPc/zPRfi077vdXzPC06Gdvnr2+W7ZU/P0W1TMfQG1Ox+07E9S1JrFYG51HdzaPZepFjYa8gxprpDVwiJi3IvsOfRCdAe8dznjXkebM8Pm3O3ZwwEynPg0IwsPclmRd9fiUbH5ubH7s8Qmayiauhfo+Hb9r7reaj5o9tf3y7fLXs2PUtXa5apdjotzVAonnY6zS+vXx57OLeZz6R5JkZXokw1gS6+5AiJP2svzjen+yPHHAPb88Pm3O0Z4bgIjwQa47gEwDApmk6T5O3RsZnFJYrlWq026iOvG75pe4bpWpZjm8f9ZZk6cPyed8l3rXI3m75jWpoBBbsigzrH5u9fHbq7HN9JcVShLsVZKsqScYGJ8kwcUme37HpO90eOOQa254fN+dozAfbk+R1eCATKxzg+zrAEwyQpajWR+OjrC49W1xyvKcs127Q67Y6l6dier6LZtFWtvt9pMSIrG41YIf3xxa/GFufiZHFPlQuKHGXIKEOh62exPb99sD0/bM7bnuBNYYcXQaBRQTgSaIKmIYHOr619c+PWdpwAgeq60fJblo6z56txDV7i/E7TaNpEMffZ1W+GH9xfyxJJlsw3pJTI7lCVGEsTAhfnmOMXfp0b3R855hjYnh8278ieEUGKCiICQijLgUAzvCA47tD9mUs3b3FSrdXuCIIEAnXsF9sTTQr5Io4/8jz4DtnTcrSGXnfaHt8QjZZ7Z2L485tXNvKpvMwnWBLUGWfJCF1NinxKEgieTUvCCdn1nO6PHHMMbM8Pm3dhz4gggz1DgR6U8Cy7pyjRcjlL09cGBkam75mur2pGu72P7flSLEdXTMXvtCmZm1991Dc9Nr+5GgtqdoKnUxIf52iCZ3J1OS0KCYZJo4+2S3Y9p/sjxxwD2/PD5rxbjaBgB2+CPeWoiBx6IFCWTYlCnCLjpdJqLH57eHR+adl0PEXVsD1fiuXqzr7Lq3I0n/z8xqWV+Hac3Isz5YzMJSU2JYM9GXjTczU5yXFxisqI8Ol2ya7ndH/kmGNge37YnL89ebBnLSqGyEimvBDj+G2SZD1vNZ3OMezTWPzijZvxVEYQZfsl9jw+ot1xjj/yPDhfe1ruIUFh3vXTADSxcIjp6l6nnaaK18cGBufvPU7FchK7QxZTEoR8JsEzUYZKcExGkiB4xig6J8vBx9Dlu97y3OeNeR5szw8bsOdpxwM9BWDPGC9FeTkmHiKIUQ61HWVqtc1SqWroGZaN7e3NrDy+MThU03VwJwjUtkCgBw4N7ek56HJ49wTnfV38OdoTpGl4mu6hpeFqIEeozUNRNn1baUitpgsvT9MajmtqhtLstAsCPfFk6dL40L3NVYKjYkx1u1rM1iUCNa/Dl1V4xALCAc8dzJh3DLbnBw98vucFAQgnEQiBT8tSWhISHL1Z3M2K/HIy8c3QwOzaKq/U250OOFQUhU5nv+m7ssTvtzzPMULQKBloFh/N8VS0tINhht6YLjm+lvO1J6hT8w8EemTPYPCkYAQQ14bXb8F2OKbj2YJaexjdvDg+NLa6DG/cDlmOs9Se1oDlc0csfB8eqPPso7diegG2J+Ytgf3n8HB+dkTzW5VSnKVTIlfUlV21vl4sTKw9uTE1nicrFMe6TX+/09F1TdeUpu+ANH2kTkigoE7NcVVQp+0pNggUDROMhPNmdMvxtbyL7AmYqHg/UCdg6I12yzMNzfcgi5tgz9Z+M5ohbkyNXr8/sbaXKxlqNBj5GFaCSzCPHbHYnt8VsD0xb88LBJrgWIJHlAwNPLBdLYFJ+2bvTc3P75XLjus2m03PdcBfjmm0PceHKBYI9Jg9Ee+zPR1UsAepE5n0SJ1uAIROQ1cBTVNc31F1ZfbRwvXJkce5JMHTMYaE9wu8CSvw5fPcEYvt+V0B2xPTE7oFWlAasIMlglbi1UIWBFo2tUfx6JXbt2PxhCRJmqq2fH+/2XQMPRyFPhBolz0159A5b0a3HF/L+doTvHmozoMvAaROS2/7jq7Um/ByNUVR66ZtxJPxvrGh2c3VtMRmZH6zvJcMhvyIUJVsTXzuiMX2/K6A7YnpFccEKggEz0VpMleXQaBQwmdkcYcsJ8jKzMOHo6NjiXjcNi1LN5qO02nvG/V688OzJ3DU1B5GaFCnZyHanq3W5c5+y3GshlovVorj9yb6J0Zi1b0IVQrmd+PhCycjw7dQDd64545YbM/vCtiemB5yKFBBiLNMlKbAnuBQUCewnEkW63I8k5mcnJqenCbLlZbn25phNpSO5zUtsxnaEzwTCPRDsGdIoE7tSJ0hULijPlmezUv83OL8naG7q7GtFEeCPRMcBXkTgmc40VswCvKxIxbb87sCtiemtxwINC1JqMM8y0SoKuxpED+3KqWCJBbKVSKRGh0enZ+ZM1Stabs1ju+026E9fTDMQfzUPhB7HqrzOXtahtLZb8qyCDV7iSpfvnl1YGKYViR0ZZEGYbO0VSlWbYPgmce59J6GToI8O2KxPb8rYHtieg4SaILlMrK8Q1azNTkjS5BDszVps1AgeaFYLD9cWBoaGIpuRzqtNlTuZqMBlXtgz7B4Rz2WkDpBo91+fDXdcnwt52xPW3UdvdW0gzOcsmOoLdRDSdG1etDHU63rjf7RwdvDd1PFXLnGZUQ6JTIpEZ3xDINnSEo8QkiJYkoIQNcaYXt+i2B7Yt4S+RhHdx4k0CAeIQieA9IsW2b5YplMZ3Kzs/P9/QM8y/u2s+/7rmk4hmZoddvSmi3bslXdqMHKCT++mm45vpbztSeo0zIVzzV81wBpupbe9CxDb+hGo91pgjofra1MzN9b3n6aKGZzXDV9zJ7HBYrt+Z0E2xPzloA0awEvEWgAwfNgzxTLFmimTLP5Ynltc+vBwuL6+kZNkpuu55gmmv/MtVzHCLKnDvKB5Qk/vppuOb6W87Wn75mGDl8IKgjUsdA0mfDyTEtV9Xqr06Il9sur3zxYW86x5Z1iOsNXUwKVFECgB/Y8Eii253cSbE/MW3Jkz1cJlODAnnyS49IUVRaEPEkmcvnNWOzu4GAun3ccx7ZMF4p33/E9yzLBNiYET9NSTvjx1XTL8bWcsz19ZM8wfkLk1HQo2A3HM+t6rWEp6/HtC7evPk1FciK5VUoXGnBAInsmBYifB/Y8BNvzOwi2J+YtOVLnqwSK7Mkhe0L8zAt8miSTpdJWMnlrcPDRkye1Rt0Dv+gq6M+HCGqiCSXbbZDqe37e0/UgaSooQrt6Q5HqimR5htOyFVuJ5YkbI3cfbD4m6L04s7dN5tI1mhAP7BkO0oHt+d0G2xPzNoTBs/5agYb2RPET9jqOJRgqSVXXksTC06dDk5ORRKK134Z0ZhoaxE8A1e8OmNQ64cdX0y3H13K+9gxPQHi+adpqrSEqRt30DM3VeEWYWpz57Po3Gb5SaLARuhATSjvsXkIgCR4EemDPYwLF9vwOgu2JeRvAkqDOLnuGAj3+MDG0Z4Ln4wIX5eikyKUFbmu3kKpWBqcmR6en6ppqu6iA91y70/ZdWze0est/z1uNQJq2p7tN07CVhl6zm5bpm0yN3SC2bk8MjC/P7SlcWia36HxMKm8xu6E9CR7i54E9Dx2K7fkdBNsT8zYE9uRCgR53aJc9JYJD8TPO8zGRjwhMQuJAoCmOyQvc8vbWneGhjcg2JE/f93S10fIczza0htxuOqDRE4p8Bd1yfC3na0/DUixIoL6h24ruaM1Oy2haOWrv7vTwjYmBrESlZWqDzK2WMxGxEpPIhIDUSRxOzv4SewYCReD+nt8u2J6YtwEsCa48rk64JwR+CrvWAaE9EzxHyGJc5CIcFWOpQkNOMmRBYIdmpm8O9YuNmtf0eI6xTa3lWqZS6zRd98QYdK+mS46v5Zwrd0eTa1yz7fr7LlfnjLZd87S5jeWPrl9Y200lhOomtRvhq5tMKQKps84TIriy6xA9CRjziK4fYd4l2J6Yt+S4NI/ff6TOsMvnwfGeYFmCQ+fx0hJEKA4NAUyWYuXdG2ODMyuLstawXatRl1qO6elKB0KoBU7snq7jFXTJ8bWcoz1Rm5el+p5pWqrTtBpWQzTreyJ1efTuwOIMqDMh01tsOS6zWxy5zVMxCQ2BfOIQxXxnwfbEvCUnvQkcevOZOoP9jeeTDJdi+bQghHOaJTgqzlRiZHHy8cO+qbECVW4YaMRPrSZ2fKh4tffbnr6jN134M5pmNAzPYBr83NqjL+5c3ShmdnUpKlS3uUqyIewINNgzIqCpho8dnJjvONiemLfkdPZMcwLi0J6oeZmnkmx1LUfcmhieW1mUFLnV8lSwp+e4mupZ3X58NV1yfC3naU9ba3uWInEt39YNRbXUdDF76e6N8eX5lEDmVXGLLm5zVbAnqBMEGhXRFO3HDk7MdxxsT8xbcjp7ZgQhwwspnk/xcA8bXFbDpsEbdHlu/fGFG1cqTFXV6k3PtpRG0zLd99qe+55t1GVTrftoLCVufHbq+lBfiiqleSotsZtkMSrSRJ3fYCoRkY2J4cxFmPcFbE/MW3Iqe3LIngLYkzuyZ1riMhKX4iiCLH198+ry2hOGo9stX6vJHc97v+3ZtI1Ou8XTZNN3svnMl5cuLG0+rSpSaM8IW03WhbjMrdPlqMRFg6nfjh2cmO842J6Yt+S09oT4iYJnaM+kiLougUAJjtyVuOlHD2/09+X3CvV6zdS0puO8//b0XK0msQw1vzB3d2QwXdkrCHRGoMGecZ5K1cWoyG4w1URd3OHwec/3C2xPzFtyOnumAwJ1Qp3KEmKAwCTYaoanY3v5q3fvLD5ZKVcrruNYuu5aaP73N6dLjq/l3Ct3mWU6++3trY2rN65FM0SWKhPVIiTtpMBEWTIhcTs8vc1RKbW+w+Hznu8X2J6Yt+Q09hSQN4/bMyGycYGBEBalK3GqnOPo0dmZG/13d0tFy7JURXHs99qevgvqlCXh7sDdhZWlTHkvWSmmwZUCiDJ48SIHxEQ+JqELCYhj7xTmOw+2J+YtObU9kTp51EACqRMEAhCotZmJkuUMS8f3dq/fvTs1M+M3W5Zlu45t6Gqr6XmurWuK66BLOX3P6ZLmEV1yfC3naE/b0XWt0em0tyPb/SODj7fW80w1SZWzqEs8Ay8+VGdc5IGYKMRFNBH+0TuF+c6D7Yl5S85kTxQ8uVCdhwJlEyxFUCRRLs2trIxMTaVyec9HsxaDPcGYAMgOTQUU0CXNI46b8U04R3tajt7Q6qql948Nj83e284m8zxF0GXUw0Bg4DUfqRO8GYLt+V6B7Yl5S97GnmH2Ao0ggaLO81Q1RVZTpdLI9L3h8UmInj4asc4wDQ3s2fRd8GZovS5pHhH+9M05T3u6urPvbmcSl/tvL2yuJqlSiq1GqWKMqybE4/Y8UGcC2/M9A9sT85ac3Z4HlavEIlDbEZvm2TRDlyVpbvnx5Ru398rVJvzz3bBmb7f8I+t1SfOIowe8IedoT9PV7Y5/fWygb2Ziay+TlZhIoM4IW4mLTPClgewJ0kwIB2B7vldge2LektPak00FwwaBK0N7xiQEWueY3UY9w7I5ht1OZ4en7j9YWjYMY7/d1NQGmA7sGYygjCx5VMJ3cdyMb8I52tNw9V2B/Nn1CzNbT1ICma6x23QpITNRkY4dfGME9nymTmzP9wtsT8xbckp7Cs/ZM3ZoT1iJcjTYM82yiXI1T7FPtmN3h0ZLpRLqOa82LFNvNT1YgRK+y5jH6ZLjazlHe+qePrJ4/8r08KNMNKfwcYnaYstEnY8IyJ4H3xiH9iQEkeBFbM/3CmxPzFvSM3vG0aAhTJYXUiSdp/lEvtQ/MrGysgLeBGkGw867sOJ7DlivS5pHHDfjm3Aqe6rHQUMoPU/wGB2wAjTP+PGlL2aia8uFRE6XtrnKNldFIyqx1fA1H9gzECiyJwLFz1MRvLmYbwXYv7E9MW/DC+0JhAINHHog0BDkUNRdCbUahX11AsCnEr9RLmZEMS9KySoV3yuPzz8YHBupNWRFa5im3gyyJzjUNnTPthCOCbjH6JLjazmdPR1HcZxGsFRdG9A8wNL2W66h1S1TU7XGfqftNj1OFh5HNq5MDj+t5BIyvU7uxkQaavYNuhSXj9R5INBDhLggvjmJQLjH3mjMu6drp8dgekXXnoZq0wQvhMR5NNQ8Qgin62AgeyZFPoXG/eQTHB2jqtFKsW9qbO7xQ7/TbhiKYemq2ui0fScYuS5Ad22EY6PMBxw36XGOj6B8nNPaE9SJ7OnagAre9BG6a2pu6GPb8ltN23MpgbsxMti/OLvFlJJ1bpMugj3jErvFVA7PeHYRNr6LcVF6UwSJ6H7HMRjMhwkc7AlejB8AAn3m0BjHospd5CCBEpDMBCbCklG6PPlkcWBmXLZVQZUt3zYtHc35bqIryAHf1j0H4bq6HRBOJ3eS5yfwOOIs2RNxZE/wN2Bpyn4TdQhwfVfRNfhr+XLp44tfz0U3EzKTavDg0LjEEDV+hyNfZM/D7InticFgXsQr7Bnng/Oeh/ZEvSEFhuCptRxxffRufDfNK5Lfabqe7YBAA3V+B+yJynawp+GaRhsNIm9YjlVXFVlTllaf3B4fXdvLQvAkahA5y2DPZF2IPeurdCTN4+rE9sRgMC/gFfZMCDyo8xAWBAqkJTbNVQdnJ0fmpgSt5u37Lpqs2Gw6VtM2v0v2RCNBWehiKNvSHWu3Wr5468ZqIhahygmZhZp9m63AEuyZkHjg+bx5BLYnBoN5MaeyZ1LmMzJfkLnl2OaXt65QdV5zINuZ++2m/8yehucA34I9D1qNgtOuhmXquq65ED9dR7GMRxtrH339ZZIsxzkqwh8QFegEfCfIYMmj4HlcnQC2JwaDeTGntWdK4rIik6RLlwdvb6XjXENqaMr+fst3bIifLXCoY/mO6buG5xluyAlvhpzwZsgp7Wkf2FN1DtSJ7AnPYhoaBE8L/Sm7zDF3xkb6pyfTDJkU2R2ORN6UuVjQRQvsGa5ge2IwmDfnFfaM80fqRPY8EKjIJrlqTqTnN57cHh8usZRQk3zfa7pO07GP2dP0XNOF4IeyX7c3Q054M+TU9gx7eoI90QB0R89iWUZrv6WaRsM0tlPEVzevb+cyGZZOy0LYTJSqiyBNICEJUR7s2VW2h948jToBbE8M5nvDq+0Zthod2TMuMAmBjjPlFFdNs5Xf/ezjPFlmJcE0jabrBvZECRSqeN+13o09gcN+8sfUadum7dp+u2V4rmRokwsPfn7lEqk0IuViEl5McKKz61xnQhKPIR0id/vx1WB7YjDfG15rz0OBInui+CkyCY5c38tA/JxbW7kzNqxYhu06HmDbvm0Bnh12mDccN+CZGd+EU9sTccyeSJ0WZOB2y2n6NUNPlYrXhwf779/LC1xOEgjxQJ3H7InC5vPqlI/o9uOrwfbEYL43nNaeCZEhRJrgSYKpLO6sX+q7WaJJcGcTjVsH9kQCDa84Qv3hQ1Af+ON+fDVnsmfYLz8ANWMhe+63Td9l6vLD9bVv7vZt5DIEVS1pCnGozkNOps5n6sT2xGAwL+MM9owLVEGVouTeViFztb/v8ca65diei/p9Bva0A3uiK9wdxLuzpxEC9jQdy/JcKNuLLDP2YH7owdyuLKU4JlsT4cUcFeyBOl+UOsVndPvx1WB7YjDfG85gzwhbSclshCzmBXZq6eHtoUGpXjMMI7AnEmh4wfuhPUGt52vPZ6nThrI9CJ5gbM2yNNfdSqXuTE48SSayIr/bqO9Q1cCeLzvXeaTO2hEgxG5FvgJsTwzme8NZsqdEb5J7UbpckPmtTOrzi9/slUs6mmvT9hCWa5nhrMWo8QYN/fnu7Al/72DGT911BFWdWly8OT4er1Z2KqVdtbFZKcUEDjh+rpOQJUIGb3bZsx6A7YnBYF7MGeyZUaW1cj7BkQRdSZOVb25eX9vaUlX1mD2RQMMhkwO+DXuavlegyFtjo6OLD9M8GyUrCY6J0FRgz+dairA9MRjMGTiDPVN1YZsupyU2TpYyVPXe4sPx6WmGYQ/taQf2PD5J3Lmf9zwU6GGDO/qbnhvLZS8P9C9FI3lZzNbEp3uFGMeCOmOCAMRFVK2DN0MO1PnMnrhyx2Awr+LQnqE3j6szsGdAYM9nAt1hq3GBzjfEFEuCPcFRV2/ezGRzzqE9j+In4pztqVpWw3U13zN1rW5bmudamqaAQGu6NnxvemR+Ls3QBAupk0zV5S2aOtYN/jjdEnxG149eDbYnBvO9IbDnC9XJxzgOOLwZaPTg2k0mwpQLipSgK1v5TLK4e3dkeOXJY8e2GZLqtFrgL9c0fAc1H5ma6p6rPW0b7Km6jmYaDfh9sKdp6o7nFCrloemp6eVHWY4N7Eml6rVNmn6BK3sIticG870hHB35yJjHed6eiGC+H56Q2Chbydb4JEsmyXK2Wp5+MDf7YF6SJFmUWp7nGLqta55lNh3bMY3ztSeo03EU2wLU8PeDrlPu0uqTwemppe2tvCiAPaMMnazJ2wyD7YnBYHrCGeyZqvFgTzRUHU/vyUKOrm7EIyMT4+lMxkAt7xZI04LIaRqwAgn03O0JmEbdC65qsizda7qqpfeNDI/NzW5mMwVJBHvGWIaQpR2WxfbEYDA94Qz2TMpwJ0nwFNiz3JDTZDlXLY1MTTxaWTZN09L1TqsJ8RPwbavpOudrTwienqcZes33TPh909Ic3ynR1Uu3b848Xiaq5RykZZaJc+BNIQovA9sTg8H0gjPYMy7QSYlBAuXIXYmPFwsVgZ1dXBidGIfiXdfUzn4bynawJyxRt/lzbjWqt3wjtKdpqYalma75dHvjxmD/ajyaYekki1qNEjwX5blAndieGAymB5zBnlGOTNXgR1WwZ5qjYsX8Hketx3buDg9m8zlVVTrBcJ9QuZ+7PR1H1VVxv2WZes3zDN1o6JbWMJTh6fHR2fvx4m6OZ6PVMsGzhMDvoOJdxvbEYDA94dT2FMFC1XSdjzAgJQo1HFFlorybKu32j40srz5RNMVzbcc20RzFlnm+5z3BnmqD77Rty6i7rq7qdd3RJa126fb12SfLWZbK8uxmcRfsmRSFbYpK1mrYnhgMpiec0Z4KuKiYEpkkT+VEZruQ3uWo4XuT0/OzdbWh6yqEQUvXIHi2PPd87em7mmXU2k3LMBuKXrObdnI3c23wzk4+EykW4lSFgMqdZ+McG7yYcNjjE9brFdieGMz3hlfY88Wga8SZmEjFBSohUGDPJMiUrWSZysTC7OT8TDxNtPabTd81NbXTahqqcu72tI1a0zNMS1GMutm0VqMbt8YGI3u5WHkvQVcJngF7Jvjwq0CIC9ieGAymB5zNnnGJDu2JineeTLGVNFNZje+MzkwvPlm2PRsCp64pbd8798q9CdlTl73gpgYCtdWx+emhualYeTdOlQmOSgosARuNuvvDC8D2xGAwveHM9kyINNgziJ9kiq9muCrEz+GZqcHJUd0xHM82Dc21zPOv3B3VMWpoNk3P0G2Vltlrg7fvry4RdJlgyaTAJEUO2xODwfScs9vzUKAETyb5apqr7orMxMO52yMDkiIHk7xblo4E+g7sWQd7Oq6uWkoin7x49/oKsZ1kqymBToksticGgzkPzmDPhMwm5KB4DwRKiBQhVEGgWYF6FNnonxpN72Z1U2v6oE39fK/UBHt6tuJZSli51/Xa/OOHN8f6I6VsiiOP7Bnn2dCeCVAnticGg+kFZ7ZnKNAggVKECKYiMzwZLxdG56bnHj2oKTXPcyB4nu+1RmBP12q0Pb3pGYbZEBvC7ZG74w/vZ7gKZM+kQCeDUaHiAhvjQKDYnhgMpme8jT0PBXpgzyRThuJ9anGub6TfdEzXtaFyP/dWI8est32j5Zu6Uedr3NfXL86tLe/KbJIlCZ4m0OikbIxnAntyQXd/ECjS3HmQwPbEYL43nMGehPScPQEo3pMCFansFmXu/vLCtbs3252W59tqQ7Z05YQfX81pK3dH6ezbvm9wApXaTX957eJOPp3l6DTPpngu6KsE3gwGeRZQh1VCEEFw50rXW4zBYD5ITm1PMFIwKXFCZII5jph4sAJVcpwqV9Xawvrj0fvjO/FN19NNXdZVCZz4bPT313FqezZ9rdXU/aYpK+JaZP3mSH+kkE3RZJrnjuyJBBoMkU8IPLzgrrcAg8FgzsBp7ZkAIwnBlO5Immw8AFbgTnTdkUCvJaPzKwszD6ZNq77fNg1NdJDoui35Uk5lT9tRWy3DcRpgT9VsjM9M3H+0ENsrEGQV2xODwZwrPbOnyGQkNlbdS5F7T6MbV29dVlSh03EsQz5fezabumXX/balGPUL1y89je0Q5WKagco9tCcHWxwW7wkB2xODwfSMt7MnKtuP7JmCdbJYlJjtVOybqxekGgv2dKzG+drT9TTHVW1Po7jqJ19/FsmmU2S1IImBPXmCC+2JOi0R2J4YDKZ3vIU90anPoOd8GD+ZOFvNCPSexG5nEneG+6p00bLqLpgNWe6EJV/Gae1pWhA8zZoqRpORC9cvJ/byGYYq1muH9oQtRhsdbDefFIQkticGg+kFb2dP+sCeEoqfCY7Ky3yep2OF9PTCzE58q1bnfN/o9uOrOa09FV1sd7wKU5pdmhu+N5Es74E9g+zJp3jh0J4IAqkT2xODwfSGM9gTXTsuPG/PYL7iTA28RKWZapYqPXi8OHF/oq6Inm92+/HVnNaedVUAe2aKmcHJ4Ydrj9PVUpqh0iwLwRNcCfY84lCd2J4YDKYHnNWeVJc9gT1diZClNFsty/z846Urt64Zlur5VrcfX82p7Gk5mtM0Wh0/tZf++TdfxHezuzxLkFWCoSF4BvYMwfbEYDA95vT2ZCF4pgQqiS7QfM6ehMDFqGpB5lNkaYOIfX3lYn4v7/q27ZymeD+9PU2348RziZ9f+jJRLGQZOklTSZYNXUlwIUig2J4YDKaHnNWeZBg/u+3JUHlZSJLlzVTi0s3rRDZlu5A9z8+erua0rZpZW09sfXHtYqpaSpKVLMRjZE9kyUN7IoFie2IwmB7SQ3smYMnSOUlIUtWtTOrW8OB6ZFu3DOtc7enu26RILaw9unT3ZpapxsrFvCgmUdLssmfoTaxODAbTG3poTzT0J8dkRD5FU5FCfmx25sHyo7raOGd7dtxstTA2P91/fzzHUVGwpywds2dIaM/uF4/BYDBnpif2jElcTAwGEBH4NMDQ8WJx/smToclJXpYs5zTN7mewZ6yQvD7cN7e2nOUogqrmJLBnWKQ/Z88ULwHHXzwGg8Gcmbe3Z+zQnlGeS0kgKD7NskSl8jQWu3LnDsWz52hPE9nT28nGv7xxaSMTz7BUhmMzgoDticFgzpse2ZOPiQKypywTHJfhuBRJRnK5zy5dLNHk+WZPr+PvZOI/v/RVolTIMFRw0pNDXZR4kRCkBCcGoH5LKR4Eiut3DAbTG05vT9TfMyXQsCSgbBdR8IxKfFQSIjybqoGvmDTPZWiaKBY/+urLPbIa2NM4wQlvhpzOno7OiOzK+tMrfbdS5VJJllMMk4TXw6GBkOOCGEOvDb08tN3BtZu44QiDwfSE09sT/AOgU5wJESInFxHZSLCMStwmVU6JXJKlIXtmqtUbgwOPnq6aNhpn3jR1wPddxzENU3XcLoEeKVE/nT2rNLm0+rhvZDhbre6CMUmKQAPIC3ERERN4AA2IjzIzGi8Z2xODwfSEM9kTdZ1EjgI7iTx4cweA+l3mIsH06RkBVe5gz+F70/cXHuim7jiWZRlgT89zYN20tBPXIB0p8ZT23C3tzT9aHJ+bydFUjudiJIk2UZZiaOOwPTEYzHlxNnuGl0FCwjuyJyzjMrraneCZvCwmKTJZKT9YfXJ3ZFjVVTCmDenT1CGEIpPaut+0e2PPTD47OXt//vFynqEzLJNgGFS2S2Joz2hoz2CIT2xPDAbTQ97GnhA/44E9Q8CeSZmPMWShJqFrzculDSJx4eqVulIHaaJJ4iwj1CjYs2fZM5FJ9o+NrGxtQPYEe6ZhmzguCiH0mD3RwFA8m8L2xGAwvePt7Rk9tGdUZAiJA3sGlxuRYM9YPvfJV18KsgjS9H0X7InUaRm9PO8ZIWLX++9spYgcQ6cYOifLMZaNcCy2JwaDOVfe0p6o4UgAbyJiIhMXmARHpzgmy7EpshrfK/z8m6+rNBm2F4E6j+z5vDrfwp5Ptze+uXktVSnmeTZBkxlJijAMOt2J7YnBYM6Tt7UnshMbgnovcVRGFkBiu7IENtsp5L6+cS1byDUaNajcQZ0QQsPznj3Knq6+srH65bXLBY7elYRotZwU+B2WiT9/3hPbE4PB9Jy3tCeBGrQP7Alle5Ql8w05RlXLamNPFjfzmSsDfbFkQhT5MHWCOlHDkWue8byn4+q6UYelokrNls0I1OLTlQu3r+dA2zyTAmFTZLpR32LoqITticFgzpG3tychoNGV0HS/Ehfn6UxNJDh6p1ws1mWCLN8eH4kQMbCnYWhQv7daPthT1erPq/ON7el6hmkpftMCh7baDthz/vHSpf7bWY5K80xSYHdoKlmvbbLInojj9kRge2IwmN7wNvZEBPYMSUocVO5pWSB4ZqdSKoBG6crw3P3VzTWeZ9HpTkPzQZ6urekNywYZnsaelo2ml/N8E1bAm+BQyJ5Vpnx/eeHW5EiWp1IcDfaMsDRRk7c4BtsTg8GcK2e254FAj9tT5KIMCcu0xEPxnhbYBF2Z31i9/2CWYSiQJtgTKnfPA/WBDEGJZ7InLNv7LtyEHJovZicWZoYXZjI8RXAUAfbk0EnPbZ6NnLBnGoHticFgesPb2DMQKJ8EbwakRNRdCV1rJAtxFtkswVbXs8m7I4PVahlSp66r4eVGL7rO/ciTL7en4+pgT0idYfYE4qno0P2J2fXHaR7+GAn2jII3RX5b4LA9MRjMufKW9kyheX+5AwQWpBnaE4r3GFONs1WCKl25db1Y3A3tCfETNb6f2Z6QNw2zEdpTN+pbsc07E8OPkxGwJ/w9sGdM4CB47og8ticGgzlXemjPJMeAQNH1kCLIiomxZIyrpjnym2uX8/ksKtiDgULO2GMptGfY5g6VO6yAQMGeN0b6N3fTYM8oXUmgTqf8Fsdge2IwmPOmh/YkWDorC6DOBEeDPRM8Hdrz4vUrmUwq7LEUdpg3TNX1ugb9PPLkS+wJroRlaM/9juc3LVjfSWxfG+qLlPNH9owH9kTePGbP8Dp33GqEwWB6SG/tma9LqMmIIcGeSYEBe6bY6qUbV9PpZKhOKNthRTeUU/f3hOzZbNmKKoE34Sao0/PN2Yczl/tvJakSSJpgqThLpWsy6JKAZdBbPhynDpSKelQJLGrewvbEYDC9oJfnPcN4B5X7ASBQOstR1+7cjES2w9QJ6oQSHux56uwJ9gRdqpoMDgV1gkOBB0tzV/pvEWQRJE0wJGg7LUsRjiVk6dCbB/YM5vwM7Clge2IwmB7Qe3seEQxBD/a8cff29vamaepva0/Xg9RaD+3Zajsg04VH89cG74A9Cboc2jMliTssk5BEbE8MBnOuvAN79g0PbGysaZqC2osCe57lvGeIaUHNjzrMg0Nhfe7hzM2R/hSoky4ng8o9tCfOnhgM5rx5B/YcnhxbXX1cr8uop2c4S4elndqekDePNxxB2V6r89Ozk3cnRzJsNclUUkFbVVi5J2syxM9jCAmJJyQuKfLYnhgMpie8A3tOzt5bXl4SBC4cpC7ssXRqe4ZlOyxhXVElSKC8QI1Pjw7dn8hyJNgzzTMpkcvUZLBnql7D9sRgMOfKO7Dn7OKDhw8fMAzVavmhPW3HOIs9VU0GacJ6OMASy1Un7o0Nz0yCPaFyD+0J2RMqd5w9MRjMefMO7LmwsjQ/P0uSlXa7idQJAnXNs9jzqLsSaHS/43E8OTUzMTI7BZV7giqFo4SkJHGboQlZwvbEYDDnyjuw59LqytzcTLVafit7htcawRIwzAbYM5WOTc9OTj6cDbMn2DNKV5MimkczlGY8WIcl2JOQBbAnbjXCYDC94h3Y88nm2vj4KFTuVjApMWp29+1Tj7EEdNkzmYqCPacW57A9MRjMu+fd2HNsbISmSfBmyFvZE1ZMS2nvu0QyApX7vUcPjs57oiGeRCHKoxmJsT0xGMy58g7subq1DvakqCrU7Ge3J6gTluDN0KGtthNPbE/eH59ZeQj2TFAlsCca4kkUIhwbGBPbE4PBnCPvwJ5PtzdGR4dJsgL2DAXqetap7Rn2VQJ7wgrY029a0djmxL2x+dVHOZ4Ce2YEFl1dLwpoVjhsTwwGc868A3uuR7ZGRoYqlRKoEw3uCcu3sWfYaQmWO5H18enRhbUVsGecLGZFLimwYM9thg5PfWJ7YjCY8+Md2HMjuj08PFguF8Gb4eVGjmue2p7hBZpgz7DTEsh0a/vp2NTI4saTI3umQKCisEVT2J4YDOa8eQf23IztgD1Lpb2e2dMJBpnf3FodBXturuYEOlYtZiU+sCe/TZNRnj1mTzBpaE8+gaZhwvbEYDA94J3Zs1jcdRzr4GLNM9gT1BkKNFQnOBTsOTI5vLiznuLQhJoELEUuwTMpiY+xdIxnYjwbFzgASTPoM5+UJGxPDAbTE96BPbfikcHB/kIht7/fCmfmOOO8RiftOTw5/HBnPclTSZ5OBldqooFCkD2pOAf2ZOICiwaWFwRCRH2YEiK2JwaD6Q3vvT0XttcIjoQ/FtozwdGhPRMoeCIgeGJ7YjCYnvPe23N+czW0JxrO/tCecY4GeybQmJ5B2S6K2J4YDKa3vPf2nF1/nGCraZFNB23uYM+MLCR4hhACdUItL6Lgie2JwWB6y3tvz5m1FbBnRuIyQZv7kT3RcEpoXCUIntieGAym97z39rz/dPnInlCzH9kzJQnJoJcSticGgzkP3nt73lt9FGcqWZnPygI648lSYE8CDfSJ7IkEiu2JwWDOgffGnqBO3ajDCqgTHArsFbNgz+knS7kaD38M7EnwTLYmJpE6eWxPDAZzrrwDe27GdsCelUopvNDIsiA4Oqalvq09i6Uc2HPq8eIr7IkrdwwGc068A3tuRLeHhgaq1XI4REg4Qt2p7QnSNMwGrEDlbgaTa5YrhZGpkYnlhazMhfYMT3oGIy0Fk3DgViMMBnNuvAN7rm6tj44OU1QV1exvM0Id2BPUCetgT1ipknuj06NjS/NpkUnydGjPsNkd7Il7LGEwmHPlHdhzee3JxMRYODMH2POM17mHkROWULODRqGQp+jS2L2x4YUZ+DOhPdHoyAILS0JgcW95DAZzrrwDez58/OjevSmeZw1DC+15llYjILQnLHWj7jcthq2M3x8fnL9HcGRozxhDQtmOloE98ZWaGAzm/HgH9pxbWpidvS+KvKYpULwDlt2lTuBIkq+zJwRPsOfBfO4zE/2zUwm2CvbMSHyUrkLZDkuwJx4lBIPBnCvvwJ73F+YePJiTZVFVG6E9TQsK8a74eSTJl9gzbGcPz36CPVtth+PJSbDnzCTBVAmWykgceDPO0RFkTw7UGeXZWDhCHWpBAnsKhIzticFgesNb2jOJbnIIIZgXQwBpHgmUSfN0jqPuzc8sLMzXapKi1EN7GqZ6FnuGveUBCKFgT3Dow5WFy4O3MzwVI0sJhtzTGjtUFVwJooxLYkwUogIfOxwdOSGhkZKxPTEYTE84sz1hmYSCGA3WHk54wQUN3YFAeTbNsxmBzQpMgaOHxkfW1lZ5nrVtU9dV09Q9335enWe15+Ljh1cGbucEKsVWCYYsKLUITUHABHUCUUmIiHwMmTS0Z9AEj+2JwWB6wdvYkwjsedCyjWAJ8Zg9eSbD0XmGvHn39vr6U8iermsf2fPUbe7AUT95sGezZYM9H60uXbp7M8uReYkFe2ZkIcYyqXrthfYksD0xGEzv6KE94wLTZc80S4E9v7p0YXNz3TA0z3M0TQl7y5/dnrAC9vSbFtjzyfrK5bs3k2Rxt8Yn6CrB0gmeS9dqcfF45Y5nhcNgML2n59mT4BmQWIpjsgKbE9iSyH302SeRyDao0/ddyJ5gT8c1T32tERDaM+y0FF64uba1eqX/1s5uZhfMSFdjVJXguZQkRXkucWhPPKcmBoM5D3poz6TMgz0THJ1gqCRLgz13ZYGsSz/9+UeJRKzV8kGgULbbtmnZIMBT2hOkGXaVB4eCPWEJ9tzYWbsxfHc1Gc3xNNgzDvbkwI9CjEP2xDMSYzCY86OH9kR2ej575kWuLPGfff1FJpOC4OkEV2qGbe7Pq/P09oR1sOdWdOPu5MhSZCPNVNMcTTBUShDiHIc29Jg9iYMRQ7A9MRhMz+jtec+EwCQFNi1waZ4FmyXparpaunzjaj6fBWmiM56eAyua3jh1j6Uje0LNfmTP7djm8MzkwtbTFF0BW6O/Kopgz4QAeRPbE4PBnCM9tGeEJcGeaYnPyWJW5JMMGSnt7uTSt/rv7O0VoGY3DK3Z9FzXVtSa65mnsycYs9myQZ2KKmk6/L5RbwjZ3fTo3PT0ysOwcofEm+S4tCTFWBaK92cbjS515w8GXuKxPTEYTA94e3uGV5MDMZ5OBtf7ZES+UJPAZtHy3mo8Mjg2XC4XQZrhSU+0coZrjSBpgjEhcoI6DbMB66omV+jS9MO5obnpPE+nWVS5pwW0Zah4f96e4E2wZwqW2J4YDKYX9NaeKZmPMSRU7jlJSLEU2HNhfXVseiIc3DMcJeTs9oT4CZV7uAzvkRVxYXX5ymBfQWDyApcgqzlZjjMsbNlxe8JWYntiMJje0lt7ZuoiGh+OpdM8mxVYsOfo/Mzsw3mWpT3PCe15xlYjkCZETvAmxE+4Cetwj+nqj7fXP79+Oc9RBZEHe+7W6xGSSknSkT1hi2ErwZspbE8MBtM7em5PgmcSDJWgybzER0q714cHnmw8lWUR7KnramhP3VCeVyfy4SEvbzUCdQJQs8PNMIo6TWs9vvPZ1YupainMnqE905BAsT0xGMx50lt7pmtCUmDBnjGyUpAFsOfXt67HUglVbYA9NU05uz1DaQKhRg/qd9/cTEQu9t2MFLJJqpqkqbwsxyg6LaLsCdsaEtozLXJpKOGxPTEYTC/ooT2PrtRMckykUsqJHNjzi+tX8qVdNBuHa4NDw/OeZxljCQBvHiXQcN1wtPXY9p2JkbVkPFLczXBsRhASDJsSxASL7YnBYM6RHtozIbIxDk2NkRF5sGeGZ0J7VhgSjAkoSh3sia44siBKnr7HEugSVsKzn37T8nxTs5S16Nbo/MzjWGQzn80JQorlkhxSZIKFbRUCjlXu2J4YDKZHnMmefCpwKEgJ2ZPnQghRiNAkwbE5WYpUyimGjpb2vrp5jRbYsJN8o1GDpeehNnfPt05nzxdiugZRyA3PzNx//LiqqNFyJclw+VojSrFxFrwJrw29PNB8UhQCgYL1sT0xGEwPOL090UDIqWB5aE8AngHsVENPyPI5USYoJlaubOULF+/cUUzIiyoYM5iQAwr2kOPqPIZtnM6eRZqaWHjYNzVVrtXjVSrFCvm6EqW5OCvGeSnOw1JICGJSBGATsT0xGExvOJM90QDyh/ZE3kSC4sWUWE/yMsEIeameqNKJCrm0E7k7MaGZumEqpqWeGJXuRZzWnpKuPFhd/fzatV1BTDFskuFztUaM4eMcbBaoE9sTg8GcCz20Z1KQ0XOy4q7cIKp0skoOz809WH2imdqbqhM4pT11zbOfRHZ+/OUXWZrOC2KcpLNSLcGJXfYkAnsC8IKPv34MBoM5Gz20JxE8YZqTClKdqFLJSvWbvr5oNqNbaDy6c7Gn5eq6a+1kUh9983Vsb3dXkmIVMsUhP560J4HticFgekfP7ZnhpbwoJ0k6Utj99PKVIsMYthGq03GNEx2VTnA6ezq65hjEbu7y3dsr0Z1d2JoqmWKFlCBje2IwmHPlbPYMOFG581KKFzMAx2doZjWR+OLqVUaWTAd1TgrV+XqBntaepmdly7sDU+PjC3N5lkkzTJpDJzexPTEYzLlyBnuGE2cetyc8A8GJKQHsKWR5IU0zeZZ9uL5+ue8OV5MsxzyuztCeLy3kT2lPzWk5+cru5IOZW6ODWZrM8Vwm6I8aZ2FzT9oTEjK2JwaD6QFvb090OQ8H9hTSggixD/VYJ8ldnptdWb41PMDKguU8p84e27PV8YtUaWXjyVfXLmepSmS3UKzX1wq7sEGo7ejQnmi8ZNS5H2dPDAbTG97enoGRgkuPWC7NchmWzTF0JJsZnBqP51KSIoM9uxX5Ck5rT8szFLOxFdv66urFXYZKlIpZjk3zfGjPBLw8pM4je+LsicFgesOZ7Bme+oQlnwwGIw7J8CLYMwf2pEliN3e9/3ZmL1fX6+doT0DR5P1OM5VPXrh2MV3aS1Ur8Uq5ousEB9sKupSO7Bl0WkIvuOstwGAwmDNwentC3gRvhjxnz5wkpRimwHPpaild2v3y0tcluqzbEBDP1Z6quN/xd8v5m/23VrbWU5US2LOsqbBBKGkeqDM87wlge2IwmN5wJnsigQagyj1UZwrsKUopmtrluWRxN7mb/ezC50Kdd5qW1eXHV3NKe6oITyPZ8uzS7ODUWLK8l0PzecIGwcYhe4IxExIgAsieeFY4DAbTC3pozwzHpyiqwDKJ3fxGfPta3zXNUlodt6vJ6DU45uns2Wpbqi5KDS6ejX9z82qiWNgV+TTLhPZEpfqhPQlZIiQZ2xODwfSEntgT1JkGh9J0lmHyDBXPZ+4vzs0vzZmu5rfsbj++mtPas9nUNV007EaFKX9+6etILp0kKzmBR9PDidKRPZE6sT0xGEzv6J09eYKi9iQhR1djudStwdvZ3bRhKaC4bj++mtPa07Jqtl13PZ0V6c8ufLlBJDYymWK9kRYkdOG9KBOADIA9xaSMGpEI9LJPQddbhsFgMMBp7QmE6kyIwTTpyJ5iihfTnEhQdKVez9FkLJf++vLXYo3VjZquy66ru47x5ryRPcMR5m1bsUy51dT9pinWhIGx0SeR6GY2n+HENF9LCvWkUDu0J6hTSMqoBSkuSG9IQsD2xGAwL+b09gRvcnEEH0d9KOEZ5CRXS3MyQbJEhaTrjaWnq7fu3jSthu/rJtjTVl1be2PebHTkI3uaurTfMptNU9Hqs4sPJxcWN7OFFA32BHU2QKBgz6ByB3vyINC4KMZF6U3B9sRgMC/h1PYUwJtsAA8iCuxZS3L1NFdLVLlUlclTzIPlR7Pz9zVN8hzFMWuerXq29uaczp66KnT2bc/TLceIpoiLt++APZMUn+JqsGWg9tCeCUlISFxCQhvdrchXgO2JwWBeQi/sGWbPWqLC7PHydjozfn+aSMUbDd61G+/Enh0HVhzPrHLMT7/8ci2ZSTNikoWXJ4P7UKkuQuTksT0xGEwPOUPlfsyewfgb6HlkVLlXWVoxHm9v3x7sZ3mqXud8V7V06XztCZV7p20ZRs12DVltfPrNxcXNnRwnpViR4BDw8tBlmuhcA2w0LLE9MRhMDzjTeU80fSboKLh2/GBYzzT4iuI43Xq4+vRG3+26IimK0G4ausKfrz1tq9Zq6mBPy9Hrujo4MTk69yBFMimGJxj+cGZNLi6wMYGJCSBQbE8MBtMDztZqdGhPPrDnQZt7lhX2OGF+5fHQ+KhmNHRdBnsaqnCO9rRsxXURptWAyr2m1DeikUt37uzkCmmaSwb2jHNcnEMzJoM6Y+i8A7YnBoPpAae3Jx/YM+CoxxInZjihIIhrROL+0sNHqytQRkMc9OyGZ9XP156eBwm07ria61tiTSxS1Y+/+uppPJGmmCTNEmygTo6N8Sh4YntiMJhecRZ7osgJ6gx7y6OJ3dMcukyzKEn3lpemHszG0wm/5UD2tHRp39fPxZ6HqI6jWFYdsqfXtGVFbpj6neGhuceP0yRVEKUUx0UoMiUJmYYcE7koticGg+kRZ7BnlGVSspipSXGOiTJ0kuezImRPLk2RC0+f9I+NcBLneqZrq01X8y3l3O1p2w0IoY5nNPS6YmrTD+bG52YTxWIejZrHx2kqKfKpuhSTuJjEx7A9MRhMLzhb9gQdAQTPAmmBzwh8iqGju/l7Swuziw+kuuj5gT2dd2JPJFAHFfKmrWmWFkkmbo8MbaSILEPnRYFgaXR1lCyAPeOwxPbEYDC94Az2TMtSgmcheCYFLi3xaYFLcXSapVZi20P3J6PpuGo2/KblOVrb0z2zce72dF3VcTXDRA1HmqmyEn/97p3F9afJSmlPFtM8izYXynYR7Clie2IwmJ5wWnsmQntybJylkT1FLsnRKY4qSOzkowd3xoeqPGm6erNpQfZsuzrKntZ52tOG7OmqrqeDPV2wp6FYnjU4PjL5YDZayO5KQmjPmBCc9JSwPTEYTG84gz0JQQB7Aqhy5+gEXU1zZEFk+qfHRmYmZL3m+KbvmRA59129BcHznO2J8HzDRAM66aped5vO8trjvrGhNSKW55kUxyQ4Ji7yUQGd9MT2xGAwPeEM9oyx4E0+KfAgJbBnkiUJuhwr5e5MDK1sryq24vqW6+ggzbajA+dqT82yUWdPzzdhXTfqqoYuOtqt7F3tu7mys5mmqwRDwoYmJAHsGYGtx/bEYDC94Az2jDJMWpTSopjk2IzE5WV+p5Rf2Fodmp3YY0sqlOqobNc8S2sGAoWVLj++mtPaUwVpolYqz1BUSVFlzWjUtfo3168srq8S1SJBVxLgeBnsye1wwbVGaJC6LpAoEc+rE9sTg8G8jDO0GsVYJi1LaUlIckyhLpZUeTOfGp6fnnw4oziqYjbQePKBB11DaTnG+drTdXX4Y4Btq6alGqaigUVN9d7C3LWBvkRpN1LaTfJUUmLXq8WsWg+6+IcE178fThuXCEbzfCZQtC4D2J4YDOaFvMKeMY4Djt8T2IbfJMuZhpSpCTG6nBXZFF3Jc+T1ob7tZFRsiK2OD6UzpECwWatp+66BcuhpOKU9nUN7Wqplgj1VzVBVQ0vms9cH+uaePs6wVJypZOr8DlOJchRxOCPoMYEeORTbE4PBvCmntqfIJ2tChCMjTDktMhmezvP0/ZXFkftTu9USVMxe00HdLvWa4+o+lPCu/m7taai6qYE964Y2ODV+a3xkV+Kj1WJKZBICvU1XsD0xGExPOIM9M4oc5akdugT2JOhKpSFdvHPj4eqKrDU0SzNtHQpoy1bBnq5nOOC3E358NaezZ6jOI3tapmaYmmbqumMtra/eHB2KlfcIphohiymJS/AMticGg+kJZ6jct5lqXqsRIh2lSgmqXBCYS3dubhEx07N1S9dN1YY46OpgNuTQd2lP24Klbpq6YRmKqecqxf7piamVpWJDitHljAy6ZLE9MRhMTziDPTeqxbKtJQUaCuIczzxYXx2cnsiVi5ptaIZqWhq6yN0zDLOBOrCfd+UeqhP+BhDa07IM0zYhe4pqY3x+9vLAXSje8zKflfkIVQnVie2JwWDekjPYE8rfXF1EYY6ni7Jwse/W7PIiV5MUHZ1ytB0D7AlaC+35LrJnqM4A3bGN0J7N/bbV9B4+fXJtaGA1Gc8LbIqjI9UyticGg+kJp7UnOCdbExMsGYeaXeSSldKFm9ehbNccKJl12zZdxwwioBqGQstU3oU9w98M7enYpm1b7U7H77S3icTo7Mzw3Ey8vJeollMck+SxPTEYTA84gz3TAhenKhDm8jzzNBEbnJooUlXb9xz4z3NAX4beMI0GuljTNUy9fo727E62lu4e2lMz9FanU6KpxbWndybGMlQ1TZM5gU/xXErkwZ5ouo7gFcKrSkoyticGgzkVr7BnyGE4Q6CbPLeWz2Y5BoInq6tX7vZBvONlSTNRuQxFs23pYfYMcSw0mfszv70Bb2VPzzJcy3Rsy/d9RVO9NsRP4uLtW0RpL1Eq5gN7htkT2xODwbwNp7UnwXOxShliXIqs5Gjywo3rhUrZch3P9y0LanZkT+fbtadnma5l+Z4nyzLU71WOu9rX92D1SYas5jg2CZywJyGiiYuxPTEYzJtzBnumWSZFVpPl4uOd7VuDg7Kmtjv7nufZgT2dwJ4gzXduT+uZPT3L0hqKZVkO6nLqzj16dH2gP0eSOfYF9gxeJD7vicFgTsep7clxOZ7Ls0yyuDc4NbmysWFD6rRtTdOgXIaiGZXONuqlFLYawcq7s6dv6b5l+Cayp1pvQPys1Ru25+dKpZsDA0sbGwWOS7IMwSOBEqj3wNHJXXh52J4YDOYUnMWeLFvi+a0kceHaNVoUbc+rNxqGrkO5HMS+b8WeKHgG9jSRPX0TRG4bmm5ZtmZavFxbXl+/fPtOlqYJhk4Ew+Jje2IwmLfhtPZMctwuz2er1XsPH97o7zddDwzVaDRazeaRPYOmb6S1d2VP5M2QZ/b0LNvSDdf1TMupq9putXrp5q2dbC5BUyftGePC4h3bE4PBvClnsGdRFLdTKVDndiJhuZ6mG/Cv3Wx5tu3blm+bvm34ju4dcu72DAUaEJ73RKc+lVqt02qpitJqtxuaSnHcxMz95a1NgiIJlglb3oNTn2yUZUGgYM9n6kT2ROrE9sRgMC8D7HnclQFgySNQj/KQJCw5LsWxRVFY3tz46spl0/c00zRMC/6Zhg729GzLQ/YMBWp4CLi/24+vwjqNPQEHjbF0HCOk6Tuug7pQ6QYas66u1LZ2Nr+8dIFs1J4kEwVJTNBkGgTKMQmeTdfkuCjE0bwdEhAXkTdjPABvUPdbhsFgMACBIhcfE7gYWvJRQYgKYoxHoEKW45O8sF2uVA0jSdNZnmd0jdgrDE2NPXr6mKuJdV0xHctreoahO47lBoBDA4EeECTCU3A6e9qO/kKCgZ7CS+4RcDOVjt8ZuLOw9jhRKWZYNFlxXhbTEtTvbFIWD+2JBBqkzhrYM47ticFgXkJgT1BnaE9QZ2hPCYiyfFKUMlItxfIVVY2XyzmWLbD0amTz7sjAHlni66Ks1tqdtlgTm23IelYo0CCBgkCtpoUIz4S+Ob2xp2EqYMwjewIUXV56vPTNret5liYq5WJNzop8RoZczRESqBPZM+DAngkErtwxGMyLQfbkuUCgyJ4BIoIXIwyXlmtpUc6JUkEUI7u7ULNH8pmxmamZh3OqrSuW1jAUsCcv8e1Oy3ZM2zYd23RRd3WInGbQfnPQjvTm9MaeoMtwBTSqGw3HhQK+tlvavXT7xlYmlayUCyKfoKspgU2BOvljrx8Fb/TtAfYkeGxPDAbzYoJWoxfbM8YJKVGOklRWEJMUGS/uVWRxNbpzc7AvW8zrrunu+07LVQ3VtA3Hs0N1PmfPgG/Hnp6PeivBCiRQNNFmcCeE5KmFuYF7U0RpL8cyBFVNcky2JkVoKspzUZ4HDl48sieoE9sTg8G8mFfYkxDgAeJOpZoXxTRFZshKulq6v7QwNXdfbEh20wHcpqsZqhU0z4TqPG7PQ74Ne4I6w8jpN+1Qo4DckHeSiQs3r68R8RxD5QUuzbM5WdwmK1GOiXAsEigXxk8R2xODwbyCbnvyB+qIgz0hfjEcQTPlRn2XZwss9WhzbWhqLF3IinURCnZFVyB1Qs0uy6Lr2sFF7oZjItxjfGv21PQ62LPVdmEZ3qlodZJnhqYnJx7MJcvFXZFLMxQINMaQMZYGgQa9l3h48UBCkOALBNsTg8G8kAN7Io76KgXq4MW0KCdoJi8IRUnM0dUCXR2dmZqYmW4YCngTUqeiNQxLbza9RqMW2vOkOr81e0LkNEwlbHaHJYBqecfQbGMrHr3W37edTmaoarxSTNDVtMhFGSrKhvZElx5he2IwmFcT2BO8ecKenJCTa3GSKogCSCaxl8tViwNjQytrj8Gb+502L3K2a/m+W6tJ7XbTBC19p+zZRShQ09braoPi2NuD/YtrqxupRFEWCKpKsOjyzTjHHnZ5Re8FticGg3kFh/Y8MMZB6uJEghMiVTLDcQWeyzMkr9ZvDdyZX5qv0hWo1q2QoFQ/znfdnpatm7ZpOvbK2urNgbur0Z0cQ23msxmBCwUa5PCDtyMhoFO/2J4YDOaFBPYEYz6zJ9TsBCcmOSFB0fn/f3vv4d02liVu7h+/e/bsnPOb0DPVoarscpJk5ZyTZVuZGcwZORM5Mu19AElRlC1Z1XKPA9xfo0AIpCjgvQ/3ItzHsalatUTihUZlcW1x52DLsDSQZsiEND+pTuDbsqfve6ZtcaLw7PXL9YO9WDGfatbSZBh+kmmWHgk0hR6xQmd/JzZZREREBDBhT5gHdULgmWHYWktJNRqpaqXGUGu7Gwfv9hKp607XDarHf6k6gW/Jnpbuug7DMN1+D8LPV/Oze2cfaFO7qhQzNJEe2jOMxof25Cc2WURERAQwbk+YSTFgT1Anh9Es67qXhUKZpnL1yl/+9pdY8lI3ZNNsoerxY8b8nDRHfFv21NSW0pJd18Upcm51eX5zvSHxabwe2TMiIuJRfN6eTEUUs3izzjFbR3vPXv5GkLV+39M1CVWP/zJ1hit8W/ZsyWK/3xNFQZDEi0Rsdm157/xjVeKRPemRPZFAg+IokT0jIiI+TWhPcEXALXteFAtNWYrlsGevX8QSl5attNtGxzddU5+w5CcZ6BWN0vbN2BPCZog++72uLImqrjIiv3G493z+bYZoYDf2HAg0smdERMQ9fNqeNJelmVgVDUO5sru9tL4iK4LvG67T6vqGZ+qeYSDuGHOcb9GeEHt22p7As7Zt9vo93bbeX57/Pv3mOHaVpQiMJjMMPRLooDYfsuejmNzEERERPyzBNaIhEHWCOtkcReOt1ubx0fOpN5lC1rR1y1T6XUdX+C+0JxAIFIT4DdhziNEB/1u67VoQe9qeS7LM/Nrq87czmUa9KgrX1WqKIgst6V2pgLf9s2YDaTQgw7FfABcJNCLiuwb7JIw4ToYWAviy2ErUmnmGrYpyuoHnSLomiKlKtcnza7u7U7Ozuolqdxq60u91HEMLbPhYJhV5D1/XnqBOyzFd37Ecq9PraqZxcn72x/T0+1gsXi43FCXWbMRIvGRoHxu1jCwG9kQBaSYYifMh2MieERHfL4EoP0noTSkkQ4NARYwWCoyQo5g8zRZoJosTWL1ZwIkqTV+mUs9ev2oQOEGS/X6v32m7ltnzvcfa8LF8XXsqWsvveGBPx3Pa3U67120y9Pza2quFhUSlkqOpgsjHKSKvyudkMy0JkMVH9oyI+En4vD2BG4EO7clnSLrICzmGwUgyR5KZRgOrN+osO7Ww8GJ6qtvvN3E8tKeutL57e5q23um1dVOD2NOwDLft+70eOlBMTx+cn8eq5YauggdTPJMS2ASHzoFG9oyI+Em4155AKNCbzD1NMxjL5HkOo2mMJEoMnWs2r7LZf7x4kchisqpAlOa5DgSejmkEI218z/a0XUjbbYhAw1Ofmql3+n1aFPZOTv764kWWxLMsnaTJsqZcEo2MyEX2jIj4eXjIngPAmwiGy0tiDKQhcDmei9WqRZZO1aqz62u7747tjk/QZK/f81wbQrW2a3dc+7u2p27ammFpuql6bddyTM3QIPw0XBsrl17MzR5cXlxVyrAhUgyVl8UETUb2jIj4ebjPniw/IrjUju5PKiqtawpPcwwI9KpWua6UjmNXz99OEzzr9TqSIkPYCfZ0bcPS1a4H9pz03dPyde1p2Zppaa5nwRSNxOQ5qo4ESnBsLJt99vbtUSxWEvgLSOEtI04RkT0jIn4ePmvPMXWO2ZPFRCElsNdUM8PROY45jF8v7u0cnH4wfdfxPd3UFUW2TA3saWrKhOm+Bl/Xnp5vuZ7pt21R4kxT6/U7Ukvyux3VNCsE8WphYXZrM9moNXT1olopKXKozsieERE/A5+2JzImN054g2eaYWI0usJ81qgkaRw3tY2P79+sLBE8Z3oO5OyyLHbarqErvmv2Oq5jqBOye3K+rj3H3mWEQP5uOhaE15TAJ4uF//7j+UkycZbPnZeLFUWGFH4kUFQDNHgeCaL0O94MiewZEfEdM7wu9Dl1suNAly9qckZkr4lGgmh8yGberC5vHh1A4Gm5djDCsDnE8CB0A+747mn5l9kzBNnTAnsGf1+VJhe2Nud3tpP1ap6lLyol0CUw7lDgtjHHiewZEfEdM7qqPqbOkT3BmOgK+wjI1guKeFLKYhyVIfE3aysLWxskz0Iu6zi2G9jTcwxQS4COsCd997T8a+yJcGzdsQ3bRvZ0PLfT7/OqgtWqv7+dPknESzwL4WeKCqomB/YEP2I8OuB8XqCRPSMivmMCb0o3Ar2xZ6hO+gaOAuJkI4bXaor0IZ389c3ri0S82+97nuc6tmdbno3sCSEn8qb7A9nTGdozFGi724YjhqipVq8D4efLpYUP6VSBY7McC8YEXY5ydiCyZ0TED8nQnqFAP2lPJM0AErioFFnXzhHN+c2Ng/cnYqvVbrd1RfFty7fNgMCeru4E/Aj2DNR5Y0+g0/EhAuUV2e/3c43as9mZue3N60opx3M5kQeBhil8ZM+IiB+Yh+15o04iyxI5mqhwzMbR4eL6Gs1xnU5HFgRLVdqW2bHM9o9oT3XMniGG56En3xVT1z3H6fdOrq9m1lenNtYSRBPUCcYEdSYoIpyP7BkR8UPyZfZE6sQ4PMfieEtc29979fZtIp22bdsxLbOl9Byna5ldy+hYRmhP8Kbtafb3b081sKca2HMsAnUs27Xdti/pKoSfnK7tnn745c2rd9lMjGiGxgSBhpYczdwhsmdExHfMA/YcJuwje8aLuemFhf3jdy1FBXXaqt732n3b6ZmhPXXfRqc7HVezwJ4ess2E756Wf409R+oc2dM0baPb77V0VYV4u99PFLKvlxe3L87PyiV09pNlUgwFikyxdIzE0xw7IpBmCKoHGhER8T0ytCcikCksATjUtVFsBBnnMGdH4AUGX9xcPzo5oRm20+lautGxnX635ygKxJ5B5o5iT9cxgthTH8aextfja9vzkxiebwO2a1rwp3q25VqCLJSbtem1lfX3J4SuJZsN0GhFUy4atZwkosHfGQZAY3gw4FYOY/gsJwS1piMiIr5LgvGE0biYKSYYsAgSTQSd5KiMCJ2dSNGNptEqS0xDFea3198uzmFYxjQgwLQtXfcdp+M6lqYGV43Ca+6jWz4Rzp0qyE/L/4o9ddczAcc1YN7zrXYHMnlNbIlL21tTK6tXhQKpaWVRhAgU0vYUyyQYOsHA9JY9IciHl+EoUREREd8XQV9GT6+nGDSf4JgERwdQGYlLgDoZvCAxl5UcY6nJWnF6ef70/JShyW7Hb/uuZeqeawOGrgY3e04CUdmPaU/w5sieMAP2BGzPLjWb85tbs+sbBYoqC/xltQxZfIxoJmgqFGgYfgbhPSKyZ0TE9wv03xCYH9qTAjCJBXVmearAUxhRK7HEws762t5Wk2jYltHvd0f29D0HZia8GfKD2xOAeTR+XLAEUnin3T6NxV+8nd04OoqXSxhJ5DnuulFP0GScJkOBhps7HAdpfE9ERER8dyS4EQN7JjkyGaizKNAZoi551sbx/j9eP8dKuZYigS573TbIMbQnaNSxIUmfVCfww9oTjBkKFNJ2eGlaKgB/q9hq4TRzfHoKAj04O6vyPAg0g+79JBI0gQTK0gi0oYNTJHd2RkRExPcCSDM+JLRnkqNSYE+qAVFngSFyRB2rl3+ferV9tCeqkh4k6SBQMCYEoTATzn+SsEbyhO+elv81ewJhzu637UEK79o8z/f6fV6SZpeXpxaXTpPJRKVcV+Qk2QwESsYZMs5CbB+eH4nsGRHxvRKqM8Yj4nwYDyF7plkyTTXzDJGoFHKN6rOpV0sbq0JLVLQWciIElW74VPtgBjQ6Ls0RP6w9QZdhwg7qHNnT8+zg3vm+67opDHszNze1tBgvFco8mwZ7UjgKPxkizpJxjozzdBxt7sldEhER8V0A9gzVGeOZwJ50ElUFojIMiVF4jmhcZdNHpx9eTr8ulIvdbrslixPeBEV+Tp3AD2tPdI0oSNjD/B1MiuZtw4cwXDd0Vev1esfv3//+6iVWLV/ksAzRTKHwE48zeJwlYhwZ4ykQKOyAiV0SERHxXRDa85pnAntC1EmnkDqpLNiTaOTxegxLv5h6lcbStm1qaktXW2GkOTKmZeowDyYdGXOcHzn2vIvrmKaq9Hs9vdWyLVMQhKXVlan52fexK0KRcwzZtNQE0zxrlDIt7pxuxJA9o+Q9IuK7BOx5DfGmLCRE9gpySobIiVwOHNqslSDLzGNL6ytLK0uOY3XantoSPRDiHUXew09nz37bsxSl32kbquI6Ns0w03Oz82srqUqxLvPXtWKSqmcl5oKsZhQ+JaEzzRO7JCIi4rsA7JkQuSuWjHEUJgtZkUuSTVBngcKztfLSxtr80oIkC6LA9bu+A2GmoUX2REx4MwTs6dtm33dtXfUdC93V1fYz2czz1y+2jg/KDJFslFNkPSfSF3glKZAxFo8uu0dEfKeAPVMSH5x/ozCRzfJMhsIzjVq+WT/8+P7Fm9epTAok4LmWIgv9XgcEGtkTMeHNENcxLK3V77VNVe56Ttd3PRfVUTk6OX49O71zclSXWAwSdrxSULgrshpj8BTPpjjuLhP7KSIi4psDXXOnc6qESVyMqKfIZlngMvXqyeX5zMLcZezK9Wzb0vv9jsjTEFJBBBrZEzHhzRCwJ0ToLYEFgUL4KbI0zBiGJsrC0vrK31/8/i52kaMaCaJS1viiyhVUIcUzE94MmdxPERER3xqQOEK8KXEZ6MWQrdNkjmieXF28XVrYPdx3QJ22YZmapsqdtsOzRL/nowJDdyz5OX46e8IG0lXJd03Pgv/ylqGiS0mWXqlX3i7N//3181g5B+HnZb1AeFq+BaKM7BkR8Z3CZET2kqgnKLyqSEWOPr6+WNxcm1tebGmKbqiqIhl6CwAzGLoMM5E9ERPevMHW+l1XEmiYth1TCMJPWRb8jleslWZWFtZP9s8KqdNSOieQKQbP8Gya5+9yZz9FRER8azCYwF7j9TRNlgTuopBd3t3ePNjD8rlev0fTpIkEKva6Xkvmez1XUYTInohJaY6wNVUR+j0PZiwDjcssSxxsMssx3LabreR/n36xebJfYpuXxUyWwbMck+Eh8h+QRgT2vLkPFOLQcW4KE0RERHx9mM+RYeg0RZR4tiryiUpp/fBgcWM9lcO6/b6iKaapK63B+Oyea2q65HrgxMien2fcpBCuAw6qdWq2O65p67qpJjLx129f7xzukBxJSlxdFghLPy8XYnit2BJRHWU4mtFkgueAJNIon0IIIUFtkZtCyxEREfeCavF8IRmWy7JslmFhGhSHDwbERdBJhszLAsbTGZbEWOqqVo41qqxt1hgaZ1mSZT9eXEzPzp5fXWqGLkqSbVu2bQYYiNAPMBPcKv+FgDp/XnuOAz/SjZZla6LEnp6dzM5NHR3vESxZ5aircoG0tCxLHufSZa1VNtRzvJ7g2aE9B95McWI6sOdwVI+IiIj7Af0NSkF+CRjYk2FzDJMF0KhE4YfQoT1TDBEjahmWICyNdgyMxs+xNFYoMAx7fn6+uLgYj8cFQVAUpd1uj9nTtJ3Inl/GhDRHGKZiWiqE7r2+z7DEyurCm6kXl4mrbLOaZ8iiwMQalYomw/HtIJeuOUaSZ5I8mwwEOog6WYCHw+nt9hEREfE5HmdPIIg9h+oM7AnqTPM0up1TYvMSm2bwi0oh0ayWRLYp8aVaDcOwpaWljY0N8Gan05EkCaaRPf8ME9IcAT/yfCtE0+VCEdvZ3Xi78LZA1FONylU53zSUosyf10qFlhBniBt7okvwIE1kTzRqSmTPiIgv5dH2HI6FOVInk+LpFE8VVPESryaYZkkRIE2EwLPAU3miUW02IOpcX1+nadpBg52ZMLUskOZY5h7Z8wuZkOaITteFKagTIlC5xcOaTbw6Mz+9ur8dr+QxolbkqSRRxziK9KwP5QK6D3R4Iz06KYNGoYrsGRHxKB5tz+GAbgiIOkGdSQSqFX9N1lIcUZC5PE9XZA7seY4ljz+8f/PmTTqd7vf7lmWpqtrv9zzPBYUiXHNI4AEHVVT6ciJ7DoCEHUJOv22DQEWJhSksvE5ePZt+tXf2Pt0oXxQyFYmta/LHUq6qKyhfQPtvcNo72K/BiH2RPSMivpTH2TPoayjeDAmjzqSAiNGNgsKXNCFOVK/rpZLIZOnG4eXpwspyPB43DAPsaaN/VqfT9v3QnmPeDInseT+3NtYYrmdC1BkWA1U1yTAVWCKrUqqI/T7zaufDUQ6vphrlHINnICmQwZLoXPVQoNAIhkMhRfaMiPhSHmtP6GuhNMOQkw7VmRTIONNMcXiCguwQJy2lrvCHsfMXczMX11eQrfd6PRCo73sQdWqa4rr27agTtBACTkQl6b6QyJ4DIPCE5B1CTt1owUtYE2Ta7nqKqcSyyV9fP998t1cVqCJLQEaQIOsZjgrGgwYGTWF4C0Vkz4iIL+TR9rxR58CbSJ1ArsViIpVhmyWJyZC1leO92c3Vi3SipamO44A6dV0Lo07T1Ntt93bUGdnzyxjbZA9ju6bf8zRbu0xdPZ95+XZjKUvWGqpwVsrmBDonMBiEnwyZYSmMpTMMnSTJO+0jIiLiczzanslgRMykQKdEJNAET6QEMi2QFUO6bBQJW2EcbXF/69fpVx+TV5TEGbYZXiNyQZiu7XmO59tDLNeDpBN6OpgByQeNRhHZ8x4m/PggtmsYllona0dnx/Nby2vvdq9KWEViMAZPU80MQ+Z4JsvRKYpI01Q2CjwjIh7Bo+0ZZ4i0yGRkNsbi10wjLdGYRCfZZoKuFWUmxzR3zk5m1pY+xC5YRdQdw7QN2zYdB+xpB/a0XQ+kiQaLvGNPFU0je97DhBwfxLZVTZc0U6YF6vj83R9zr5cPt3JMoyzSGN1EY0vxDJAGe1IEeqwzIiLiS/kTmTuV5Igx8DRPYAKBcXieI3bPT14uzBydvWck3vQsy4Wwc8yeyJshYE8zVOfQnqDOyJ4PMSHHB/FcQ1E43ZDdjtVkm1vvdqbX51eOd4osDgc6EGiWIXIcnYXknUYCvdM+IiIiPsej7Zlv8ddk7Yqo5mSY52JULU5VUdTJ4psfj18uvj0+fy9psumYUks0TT28qTOw58CbQ27U6bgaYDsKEmhkz3uYkOP9uK7hu7quCorKO77hgEA5fP/s6MXi1ObHw8syVhKoAg/exMGheY7CGHL49FhERMSDPNqeGZ5OME0gI1CYSKdgnqymiOrW6bs/5mfWD3ZYme/2u6BL09Cc4Gb48Nr6KOoMdKlPqNNx1YAo9ryXCT/eDyojYquO1dJ1UTckv+e4fadC13ZOD395/fvGh8MMWQOBpsl6iqgVOKoiC+jy0WQTiYiI+CSPsyesf42K9fBAjKhd45UsT8bx8n7s7I/Ft/tnJ5TEOW1HDYZl73c7jqG56KZOEOUo5BzYc8i4OiN7PsS4HB8E7Ok7mg/xvCkbpmz7ut21BFMqs423W8tzu+s7lx+uq/ks3ciQdYxslEQusmdExBfzaHsmaTwvsQWZSwUj6FzWC4fJy/n9ra33h4widvpd0zZURfYss22Z7h17Bv0aPADGnFSn62muB+qM7Pl5Rmb8EiBzb3uGbbYsqwVbFpJ31ZQlQ2rZapGorBxs/bE4s3/5ocA082wzhVeSRC3DUcFd9AH8bcYek/hnuNMEIyK+HUCIj2FoxvCZvTuEVexGMBVFShIo1au2+DxHHMTP53Y3FnY2KnTT67ct19I0pd/vdj1HZpl+x0MlKD9rz5FAkTcBDzp7ZM+nwnJ000XAzO0foVtBW7q8sb/5amFq8/3eaS5WEogcINM5lcvIdIxtxjk80+KAGEckwjt7g3t9gdCnN7f+fhnojZONNSLi2wFsyGJBKbkvYaTO2/ZkUxyblcVsSzzH6zGOBM6IGqYIOYlH4wmTzQpL1TjqNBl7PvNmdnWpyVCmZ4M60dUhhOkFo+f6tuHauvMYJvx4P5E97wOkabi6PhQobFzYGa6FEDhK0+Vuvz29OP2fv/13nqqk8MJVIxenq1d0NSngOZVNy/QlXbtmm5jCJQQSGBfojTqFLyayZ8Q3zSPUGTLuzaDmDlJnimfeV0sf6xWy76dlCD7Isq19bFbSHJXFG5ymVMjm2t72y5mprYO9Jk12+30bPXwZqjO0p+HZiEfac9KP9xPZ8z7usSfsHtNScbrh9b2908P/89tfks38Qeqs0KITTP2arKS4Zk5hsBaTFAiIQ8PnyYIHy25iycepE4jsGfFN8zh7fk6dACbxTc8+a1RiDAGcNio1x0gxRIkhC43qh6uL5c31g/fvuJbU6fdbmorsOVBnZM9vg9CewF17+q6pG61232NkRrRbp9jV//0//3acvkhR1VKLLUj0VbMUIyslNLIxf4mXU/zInoiEQAOTcnyQyJ4R3zR/xp5hmcfAm2BPBoBGXlTka6KZ4ZkMT+ckLi/z10Q9x1ENkds83JtdXjyPXzOS4Pd7gT2VMXuakT2/CcCY4M1P2hNmUDUms+X2Xa1tlNn6eT72YgVyiaXLMoYbckmkr+vFJFktiFRRZtM8kebJwKGgzoE9Hy3QyJ4R3zSPtid4MxiggQVQ1Dm8NHrZqF43ayVZDJ+ELst8imzkaGLj6ADU+eHyvGXqXq9jOLbju+1uG9L2wJ5InZE9vwlCe4bc2DOg49u2rbdU0e06fr8tOUqFa1wVU7+8/n16Y+lDJlYR6JrEZohahqxVZBbjiAw3EGhgTyoeEFxNumPJzxHZM+Kb5s/ZE6kzGSTsN82boWjXuiwX47UKSDNL4VWRW393+GpuBqJOpM5+FzRpe4iWKgf2DNWJbjRE9gyI7Pm/xl17AqE9ux1XFJhevw32JAXSg5l+u84TJ7Gzuc3lqZX5k9h5hSXrApOnGhhRy7F4dihQSOETAhkXgcieET8Sj7fnKOq8USeNsRBsiuBNXG3lSbxAETWeXdzdfrO0kK+WVcuA2JLmOVbgLNf2O54V1jm+sacOeAFjZvwSJv14P5E972NkT5gZ2TPEMtV+v63qsm6rnX5bczVW40Ggum/FssnZ1YW3Kwsnl6dFvF5lyALZKDJ4fijQIPwM7Yni0ElF3kNkz4hvmj8Re05GnaDOLEvlGSpPQcfBqZZ8iaVfzM6s7G4n8zlOFsPB1N221+v3wJsQeLa7XlivMww8Q3uGjPfZL2DSj/cT2fMBQm8C4cvRhoadBHvLdg3LgyOhYfgI3TdFTXY6Ls4Qq1trr2beHH14V6hVis1anacrPJXCK2mmicYP4PBTvJzVhcieET8Qj7ZnVhQSLHWFN+DtWYFNkniOYyoinyOJhsg3eO46m1nf313b3U4XcpptQrBpD0HZOiIIPAf2vPFmyKjDfhmTfryfyJ4PMK7OkMGGDh5GAnuGAg0danqm23VVQ5VaYgOvHxztv3z9cmt3G4cDab1SIOqULuc54qSQLqg82XNOavnBPUxfSGTPiG+ax8eeLJ2gyYqmpGlUYryhK2m8kaxVaU3JNeo7J8fTiwu7x4fQg9r93ui+Ticc0y0cmyh4FhOpc9KeWmTPb5Rwh4WMOdS0fcuC3MFHA6c0GrXDw/2FhfnF5cUa0Szh9WyzmqObRQkkiF9TtZQI6ozsGfHD8Dh7YiybIImq1koQzaLIZxnqvJCry2Jd4Ooce3D6YWZpcf/kuNpstDRVMw3DAlfaw5s6b66wT2TrAZrrgj21O368n0k/3k9kzz/JuD1HgEMVTfI7TrvjGAaqFWhoSiJ+/eLFHxtbG4VquULh6Xq52uLKChcjqwkWT6JBAe9Y8nNE9oz4pnm0PQsCdwn9QhIT9WqyUWvIUqJcLFPk9PLi7MryyfkpJ4t+t6ObRktVHBf+N6HO8ETn3agT7KlG9vxGGZfmaBfCvGGpg0wfPY9kth3LUFosRU69eT2/OB/H0rjI5ch6gSfrhpRgGsGQqncs+Tkie0Z80zzOnlmWvSgVa7IUr1TKLENpapmhkqXib29eLayvZctFzUFFO1RD99p+t9+DtP12yBnac9ybgToh6gR1Rvb8ZrlrT3RzGRpHRYMIFFVzsXVLVxxDa1tW13Fq5dLy8tKrqdfvLk4hf081yimiWpDZyJ4RPxCPtCfDljnuulgkWi1cknJ440M89mL27fzaKq/ITrfd6fehI4E9Hc8FFE0BY47u5QyZVGcYdUb2/JaZsOdwRxqa0XI7tte2TUOxtFbbMh1VFSmq53u6puwf7v/64tnW8cF1MXtZzibJWmTPiB+Ix9kzx7B1USzTTLJUzjebRxfnf7ydOfz4sUFTVtvjWhIrCbbndvt91/csx+p0fWTP4CGiAajfjalzFHVG9vyWmbDnMPY0TVs3bN12Tc+1LF3VW5Ktqj3X7fe6sLlVtZXJYVPzb1/MTn9MxWoSm2XJoCQoleYpZFI+fI7zFujKUvCjFI9GkMdYBo3/MTYECCh1yKASIrTjITerRUSMNYyHCUtqjnPro8JGiECF6UJGVT8myh5jqHLdLSBtzzNMmaKaLJvM598uL08vzCdzWVFRdNtWTQPQTEMPLhaZtgn2tG0IPO+xZyDQ29zx4/1M+vF+Ins+EcHWDwdRscPb0G5ObyM6bQ/2vWnqUkvEctjmztbc8sLK1kauUcNw9ABvQWByIpPiyRiHX/F4QqavBeKSa17DPI+GD0yhO+2pHEvnGTrH0FkWPZIBMoV2PGzcYXMP2u6g1Ybd4Hajj/h5CdtDoLYvYGBPyHhQ0jPMe4KDN5AdgDyYZSANDywpS0mBj7M0kOLZNM+lWCZBkzmRTwbDJhZFviCwSaKRxOslhsJZemd398XLl/sH+41GvdWSbdvq93swHeGi6+yDS+0guPBhvxG39fdPMunH+4ns+bQYY3ei3QK8GY7wF84TRPP9+3czszNwvL3MpJuSkGfI63oZFJnThIzGx2QKiEtUQoLYkwztmeWpHEN90p7Bs26owNfInmFyFNkzYoxAncN48EGQPYeFaCfsOVTnpD1BnTGBjfFMQmASPJMEODrJUFfNWk5giiKXIhqxejlDNYscXWbI1Y217a2Ny4szmiIcCC1dyNRtCDWC54kGjEIQyO1AcHeU94RM+vF+Ins+LZ+1J+D7LthT19V22+v1OiSJHx4fvpl9CznL9sm7WKmQ56gMR17R9XO6BlFnTCSTMp0EgQaxZ5onMZ7Ksij8HFMnatOgTtRMA4GG4Se048ieEXf4k/YcDjYz/Jxh+DlklLazcZ4BeyZEFuwZ5+gER6dQq6bTLIUFo3ZjZLPAUnmaeBe/nllZ2thaz6QTLVkYmhGd/gpnRsCSEcGSCeU9IZN+vJ/Ink/Lffbsdtue57RakqYpIFAwKQkZTbG4tLHx66tXq4f78Vo5Q+NxugG6vGQbkLMjghE+0gKVATgS44JWiFpkeLYUGvdAnYE9B/k7NP3InhF3eLw9UWZzY88R4x87WgjNL87TcYg6RdAofU0TMZpIwfFeZPMChKgUxJtVkUvXKzsfT6ZWl17Pv20SDVUR0c0qhqKpEkxtSzP01rjUwusKIePLvwKTfryfyJ5Py2ftqSgyTDsd33Xt0SD9lmVatkMy7Merq9m11am1lb3YBaTnhG+GdZhAnRCKxthmRqTBngmqAQIFIA4dXmUa1gm9sSeAAoGBPdGpz1ttPeIn5rH2BG7kOODmNOiQcEkQpSYh6gR1CvQ1S1ySjRjVhKizAAk7iVL1Ks9e5bH5zfWXczPHF6e8Iml6C1xpmSqY0fcgNwcraeDQcamN1AmML/8K3JLjg0T2fFruiz0tywDAnhCBhks8z+20O+12R1bV60x6bmPtj6W55ZODD8UMupFeIIAYU0+wTUyEMJO4IiqBN8Mqy6NCywAIdNyeKJkKT0hF9owY43H2RCsPc5ewXUEDC0+DIlEGwCF8BHopsUkRIlAqxuLXVCMB9mQIiDozROO6mNt5/+7t6vLq7lYqn5V01ev4uq7omgzG7Hbctm+DRgGYGZdaZM8vZ8JH3xeftSek7TA1DA2AqDN0aNv3Op5vGSb8gwNumWhsvT96sTz32+LMQfrqsllM83iKaybZBswAMaoGDh2qEwWnoM6gyvIgAg3tCW09OK8f2nPUcyIiHmlPxKAJIXuOpDlsbwHBHXVBgxwr2gAyDZIkBk8S9WSj+j4Z23x3MLu2vHN00KSJbr/X7rYpmvBcC7QF9oQpeHNk0jGjRfZ8BBM++r74rD01TYFpu+35kLsPw0/T0B3T6rUhoW+jNN5zJEu/ymderS683ds4TF+l6HqGbSapGkyzPJHmmoE9iaSACOxJjmrU37Enmo71nIiIR9szuDcTXSa6sWdgzPCwHc4E3kR5EipcyxLJ4MxSTmQKEpPnyGSjcl3K/Tr1cmFzLZnDFFMDb1o2hAx623fAlSBNSN4hWwc5dtoO5O+wZFxqkT2/nAkffV/cl7nfBY6wbcdt264HmYqNdp1qmy3HlBxjYW/z2cLM683lD7lkjsXjjdJVNZ+m62WNh1D0iqxAe83IdJwngLQM6RKdEpm0wGQAHsULwcXQic4T8ZPzWXtiHP9pUAYzaEhpDtkzjDdjkACJVKbFYi0W2mFaICA3yvBEeF4+wxI5jixLbAavrh7t/edvf/sQu6iRTQgWHM9B568cC6LOMOR0bJgixm56/yy3Zffk3JLjg0T2fFoebU/ftn3LAXuCPtGZUdSgLMUxZce8yKZeLM3+derFyvFeqlGuSWxZpK8bxTTbLLQYTCTjTOOKrsU5HNpxCuwpQCOmwZ6j8DOyZ8RtHm3P4PzPjT3Dq5QQdaZlBuU9XDPBQj6EYwKBwQxVL4ko3syzxHWlsHSw/ev0q7XD3RpDVIm6qEpW+CAJuq/T8hzDRNfWB+qM7PlJInt+FnQDsGVBpgJTyGGQPyGdd+DQbBltR287VZbYOjl4Nvtmem3x6Oo0Uc1j0EAlptxi00wjTlYxkcIkaNMoaQrP3KOrn8F9oFgwPsztzhPxk/PozP2T9kRpO2Q8Emp7GR5PkpUUUcmzzRqk6lSjQDU/pGJTq4uvl2Yh5CQERjYUHXqHZ9ngAxNVbmx7yJ66KrqRPe8lsudnAXuirW+ZwODJCgdaGLKpCU2t4wKi0cpWCzsnBzMr8y/mp8+zKYhDCyxR4Mk8APYUyATTCC/EB/ZE94GGN9IHjHeeiJ+cP2fP4NTnIHMfnOu8hoM3tDqumReIkkCUObzCNCt0I5ZLr+5tv1mc2/9w3OQoq+OYvq2YSqBOw7TU8KZOsKfvGrapuLbqDJl4RP1TTMjuyZn04/1E9nxaHmdPAPbBSJ2Dx9FcGzL5dhcSelO3dLfr+f02ZD3xTGJjb3tqcW51fweS+hJLVCBPp+oJsopGmuPwFBc+Dk+O7Inx4z0nIuLx9mSQQDH0HFF41WhwhR1y9muyEifKoE5K4xs8cZ2J7R7tzi7Nre9sXiVjjMhZvh1kVYbXdgxLs2zNshRDl02jBR4Ee/ouCEt1HCXkVnmkT/Loqh+PZdKP9xPZ82l5tD0HIedQneFzviBQTW11u+1ev6MZKhqu2rNd32mprd2j/fnV5dnVpYOzD+laqRgEoUWZSTHNFIsnWeTQIPxE+Xtkz4jbfNaeaYb9JMMH2Mftia6wJ9hGocU0dbEhM+lK9ujj0fLqwszbN0fvDht43bQNVVdkRXI9q9P1IOQEXNfwPAO8iS6OQqTpap6nj9QZ2fOTRPb8IsbtibCtDvzHMlqyqGtKt+P3ex3bNkGgqWxmY3d7YX1198O7y3wm2SwniWqKbqQYHAQK9kThZ3D2M7JnxG2exp4pgUiyjZLMlEUqVkhvH+2sri8dHm7HYxf9fgdarChwvufAvGVqutbqtB10WtPVkT09A7q5ZSvAuDoje36SyJ5fRGjPgUNthKVrkNT3um1oiEpLAofCfLff8Xs+LbDvTt9PLc5OLc1tvNt/l7jEyEaGamRoPI0cip7jBILr7//UqU/UZxDDZ/ImCVeDPvntcPPlvw0mvt6TM/Hr7ge9JazCNUGKZT8JiHVQBAS9HdoShXFkliWKPHmeS60ebM8sz61tryVS13KL6/d9z7WglXbanmmoGuRMth4+fwn2tEzFNFuWrYY9HexpI4E+6rxnZM8HmPDR94URPsD+CO7skpBwx6CZYWoPwPqapeqObnqmZmsUR8aSseWNld9ePl/d2Tq+PE/VyjlQJ4Nfk7VrppFAj3XS13gdgDg0L/FplkrSxPCC0sMgRQ5ETH8CiG2HD9R/CYNIB8Kfr8WfEMpXJfw+E1/yCWExHmDuMtiDweXyEeimd5ZJBYTVZEJS8DmSkBb5JM8CKYEDEhwTY6grEo9TZIIi0hRZ5Ngqz+Ua9ctUcvtgb2F5aWVtJZGKK5rc7ntO29StVlDoFhrtiHE3ffvc6oMPEtnzaXkye46YsKfX83RP1x0N2muv3+50PZLEY7HrZy/+eDP3dv1g7wxLQQSaFekrun5SzX2sFECaxZZ41ayd18pZgS3IQpxs3vLjbW4tHzzCTKYF6i5IoI+xJzAQ6FciEModhf0vEqhz4ks+HagcDA8wE3zOnkCapVMsnUSli4OH1rkBGZFL8qBL8orCr2kiwdGYLORVueHZZU3JMHSaIMosWyGp92fnU1PTM1PT5x9PBY6DMLPd9UxXV+2WCvb86pfFvyqTve9+Ins+LV/dnrIut6xWy5BZkW4SNZJsqorcbvuqrl0l4q/n3/4282b15DDWqIBAUxxRkNCIrxeVUl7gSrIYJxrXzRr4dNC7BoocY9DtB8sDdU4SDhMyGCkksud9fG17AqOzKzdMfI2ROkGmoTrH7Rm+5aJRg2lFV6uGlpfFJEPFKSJBkzVDizUb8Xo9T1JHF5e/vXj16s30xcUlhROWbnR939BVXmRlTbR8w+850AvuKOk7YrL33U9kz6flXxF72l3b6dh+z4XYs9cDc7qObblB5ViCY95fX7xcmv91bnrheO9DPpPn6BLP5mgyVqtc1ys5ji7LQlHiwwqhwyKhAy1+yp6TjBeJgO4X2fNevro9IelGYwqEBDn4KB8PfjsieFYdATsL2ZND9gylOdzdTFltYTybIHAAPFuUxIIoZDl0q/x5obC0f/Db1PTr2fnTiyuG4aAl9nv9fq/nO46uKbqp2r5pupqsCdAL7ijpO2Ky991PZM+n5avbU1AEQRMg9jQczYGEyTEtXdNaMho+yzTgDRKEDzTxMRVb2Nv8fX5mfmfrYzpZZulGS6yIXFlg8yyVJhtZlsRuSoWGd9oH15qG3SnsWoMIZawAM4AGXQiAebDDhB/vZ6L/PzE/oz0h4x4A8yHhhaBga6DVxjZ+aE8qBW0gOECG+QfG0hfVcorEC5CgCHyWphKNeqxaAdaOj//xZurXV2+OT88Jmm21VMdyQJ2eZdmG4dnoGlGYueu2aqHr6RM++r641fUeJLLn0/LV7YmeeOvYbhf+o4oSK0t810cDdoJDfc/t9brdfk93LFLking9UynOrCw9n5maXV+9zGUqLJUjGulmtYhGlyOyLIGhkeaIND8CfArdadD5kT1DwJjcgDjAM7EAeBnZ816+uj0TLJuAaUCSHZAKxrYcrTa2/cGeoE4yHYzkGlxAR2O9AHmWzhBNoCJwzZacrtcW93b/49df//Pv//hwdS20VKmlKYrWbXe7ni+xfL/TcQzdUBUwjuOauqlA+Nntt6EX3FHSd8Rk77ufyJ5Py1e3p+1bitmSNdG0tV7f7/c7tqFJHNPvtKEpixynqUoPbNrvqZbepIlcpXQev55dWfrLb39/Nf/2AkuWGALDqwUGz7N4lsUxDpEJ6odmAnuOzpFB/x8IlGeTLJPkoKOycTR2DRsLgJe3O+fDjLr0V+FntOdAnTAzVCeXYrj0cB3Y5uE9mwiUvIM6iQw6cJIYS0IKkmcQsUqxyrO4LF7msJnVlV/fvF7Z3YlhmKzpBjQ6v9Pxuw60Vk3vOF6/2+86roe6t97x0X2dnZ4HyRA0S+gFd5T0HTHZ++4nsufT8tXtado6ZEmWo6PnNwzFNlXPNnq+C+2467n9tt+GJqCrmqZ4vtPrdy3HkpRWkyJjmdT67varuZmZ1aWdj++KNB4KNAcODQQaQEASlx3WBkUOHaTwELMwSRTmDAXKcUBkz4f42vbkkyyfGALzKZZPMwD6abjBQZq5AWyOYTCGxBgcgMwD8o88QxQYokgTmXpl5/27Z9Nv/vHqxcr2VhzLNGiKFQSO46Hd9Tu9tuu7puWZtmdYlqICXceBhucEA2m4vuV3XZje8dH3xWTvu5/Ink/L17UnYDkGqBOAX+c6hgfYhm+b7SEwD3iDMQjR4B9e8H/DMhmeS2Dple3NZ9Ov5zZXDy4+XpdzyWY5TdYKPJnjiRRZT5NNjCKBDElkaDLLMVlQJ0NleOirEIGySY5Li2JKFBM8n2C+uj0nCqaNmFhtwLdnT4yf/OYhk9/8kYQPAkGYGaPgqCZkBCnNi2mYYQWME3K8UJTkHMtjFA0UWb7MCxVBLAtcrFpM1Es5ulmTOVwViwx+mo6HVd/XdrePPr6/TMbz5RLNsdA0+/1+ULXGBmn6CAtoh1iIoKUFRYtdw3bR9I6Pvi8me9/9RPZ8Wr66PeFXgEDRLxrZ0wF7Gu0x4CUQ/Mh0LQuUa8MUfpllyapCsHSpUVvaWn+zNP9i4e387sZx4hIjag1VoG01SzYzeD1PkxWRh86WofAk0cwwNLoUy7OFllxotVIcf03RVyQVo2joyRN+vJ9xBXwJE9IZMbHagJ/NnihDF65x+rJBJGkuJ8gYRKAklWgSKZzASCoLEESm0UhWq4lKOVEp5YgGo7eAVLV4ePFx8/hgbn3l11d/gDdTOYwTBa/T7vX77XbbMIyWJE+o0zfNdgjYc3CcHtgTCJ4pmvDR98Vk77ufyJ5Py9e3JzBmzzGB6rdwAM1DU1TmxrYgDrV932t323637XQ8w7WKzereh+Pp1YXfZl49m32zcrB9loknyrlMvZwnGgWyWaAguyfKAlttiYMR5EGjJHlNEAmayUpy1TCgJ0/48X7GFfD0/DSZ+8ieGC/FCDpO0DleKklKgRchQ8+zbJ5hKjzfkMSGINRYtsrQTYFjFJmU+Csstf/xZP/Du8WNtdezM+/OPrYMDdqZ5TqO58JR1kLVZu227/c7XQ8CzIE3kToDDOAmy0Hjrd9wx0ffF5O9734iez4tX92eQPBGWA6NFZw4aLUgygAw5ggVpm3X9h3bGz5BD+81LF3VFcd3TM9SbZ0UmWsssbyz/mp++uXc1MHZcaqUrdJ4iagXyXpdYEocCeleqllPEc00ReR4riTLRVnO8HyMJKEnT/jxfsYV8PT8ZPZEAqXZHC8WBMjTBYxiIE8vsGyJZbFmI080SyReJvES3ig161Wy2WTIncO9mYW5Z69eLK6uYPmcZhrQpERZanc7EG96ngfeNA3D1A1oLD7oFFWaHccIgCP0IMUZZTkh0CzvKOk7YrL33U9kz6flX2HPITf2REmTq7uuFjBelkYLxBqeA0XAu0xL0w1FkDjNUNyO04Ysrd/WbK1YL72//PD3P/7+77/8+x/TL05jZ6kSdpq6ihUxWm+VebrIMXmWydJUhiZTFJkkiThBTMjxQSYs8MT8fPZMECTEm3mWj9XqsUqtQDNllsk0arjI5+qVeC6DVQrlZjWBJRdWF/79v/59aWUxlUqKoiDLEkw1TYVY0wSHGuAOq+17vW4HROqhJzBMy4Bj8ECLtwmNOSZQFIQi3MieT0pkz3u5s0smmJDmeCH60J7hKSfH1R1Xc1zV8UIUhKv6num5sDIa7jUcqhBeer7V6brdngdvFGUOMG10771qtryuDaSy8f/5x3//23/929re+nUu8SFxmaoWs2SzxDE5mkw0avFmPcexNU2dkOODTFjgQUaamGBitQHfnj3T7OQ3D5n85o9k9DkpgsrSTKrZvC5X8hRN6zqpyPlmHauWcrXSdTq+vLH8t99++eP1s4ur05Yi9PsdaD+mprZdp4sqIpnQ/9ue0/FdmEKjskxoKuAR04eFbTc8PH+OMYFCFh9UQ47s+aRE9ryXO7tkglCUIaE6Q9DLgT3Bm7oNeJrtqQFKCGi03bbCEmGgTtNQ0E1OFgpIlZYAzaXt2zBVFREwTdVv25at1JqFaqMgqVyNrKxsr/z1+d9+ff18bnN1493h+3QiUa+CQCEChSw+RULs+XWf1BxpYoKJ1Qb8ZPbMABSdIYh0s4nhOLo6VClfYumzZGxmef717NT0/PTu4XahlFVUwfUMOHBKHN333X7H1yRB5tm2Y3U9R5VFS1ftINJs+0iaoNWgNKfsDg7MIaNDNeK2QE1oO5E9n5zInvdyZ5dMcMueI4Gil9B2kT0hZ78RqHtLoBB72pYK7bvtWcFIMvBG3UGjdmr9HjR4VVclWNjveh3fhhW6Hde0FM83TEeRFE7WBMPRBEUoNSrbx/tTS/O//PHs729eLu3tnGZSGISfFJFnmOBGwhtu7s1mxr3JBwQ9f/goYQB/A3sHdD9jcJvUbcJnacYJvAkzsDy8QfUrckeRnyW8TzYAfecxOPjrMgPQbUboTiM0Da/Fg1tvGD2lDoSjDGUZFqMZAKLOPEnmms0yReWbjYPTj7+9evU/v/79xfTrq2QsV8rRLKnpqO6RbSMVqi2h322bigzq7Lo2ONQ1NMfQYGHHtaFBOJZum9A8dJAgmDT044P2DAQ6uIgU2fNpiex5L3d2yf1A6wwa6IiJ3a87jnaDrYEuXegMX4htmIZqWlqIAVNbN2GhbSi6QtAkls++P/u4tLby7NWL569fzizO77w/OUun8zhR4/gSzeQIMkuQBZqtCDJGMWmSznNCjhOTJJ2m2VJLzfA8JghZUYQpyBE9GIN8KuSkFsbLaVZMMQIAMxlOSgtSUhKSEp8QubjAAglgWIAyDfBcJgSFnKE6x+pFPTno2fBJRYZkBQ7AeBT5oipwDAUkaDLGkDGOiqPRKFn0NDrLJhg2TjEphs/yclHUSqKWZxWMkoAsLWY5LrjHlsnxLFAQuKLIlwS+LPBZgkjVahBgVhi2zgslis7V6yenpyvray9evXwzPbW7v5fJZhrNBkWRpqkDYRoOUWQIMqOhA+P9c+K60Ci5CfgiFaI2FjTLYeOcXOH7YbK73U9kz6flq9vz08b8DKE6vS8Gmj50trDjDbqfZYSoasswNFiiKDLDUOVyMR6//nD64e3S4tTCwszS0tre/mkikWs0oWM3RLnIcCUOXQWO15spgi4GF4Uvaw2UckKcyPMAeDNBMwFskuZCaY6AlwmGi/HstUBf83RsSDwoUJISwKFsGuBvx5t3ikI9Lbd+12cIam4i4OtlZC4lsSmJS4l8GkB/uJDhhDhBJ3AmhXNpnE/jQrop5Eg5D/Zk2DzPFEWQJlcUIJanMBJPN+qZRr1IkjWWLeL4RSq9eXA4vbj0Ymp6e3vrw/t3WSxNEk1J5HVNMQ0N2snQm7ewDcT99hwnyHK+axs+lsnudj+RPZ+WH8qeI3UCoE7Pc3zfddDzS0677Wma0iSa8Uzq4/XV3sm7le2ttysrMysrb9fW5za3dk5PU7U65I1NBWIrLnzoJYkTGYZJj4FxfFFuVVQdklYMbMsJWV7M8hKAcRB+CqguCRgTDSMeVHUS2WTgzeSw+FNYx+RGXnd899TcKjc1IsHRwOhlKHcgwVEARKBxhkzQVJKh4a/OMCzE43A4KbBSiWtVBLXKq1VOKbNSjqayDIHReJYmCixVEdiGJOAt6SqfPbo4X97Znl5aeru8vLy1tXlwsHd0hGEZisShYfR7nXAQF6Db8celOSKy50NMdrf7iez5tPw49hxXZ/hVYSEEnpIkQBwKDoUlCkQ6QdomGxrO0sl87vjsdG1/b2Fr6yyVWtnff7O6unJ4+D6VipXL6UYD1JBsNtIUkWXpPGSmEKbRVIokUgSZQ09kh6fzYArAS3SeFD1fD1ZCI4kPdBm+vNHlJHfS7acD7IlEOSzQNw6KLiUO5A7zoe5Rws6GJVPRt4WkHlL7vMCXJLEkiiVBzNFMso4nqo1MncjhdIniShRTYukyT5c4Kks2rgrZ3bMPc1vrLxdm908/bB4dbOzvHZ1+zJZLjCSqNrp02G77oEsH9o6hGboKwAwwLs0R99hzfMkIpIbInp8nsufT8iPY8643AZiHYFPXVTSAnQudVYd5E76M7+qQ10O26Dmdfs/rdzlFLjbqH+PXi1sbv029fjk/t7y3u/X+ZPf048Hl+eq7w72rs/MChpHNEs+UBa7EswWWTuPNDIFnKTJH00CWojI4mYJYlaYyzIA0S6UYMkkTCQqHefQyINATIsNPmPSJQe4eVoaeIC1zACTpSRFkipbEeRR1oq9KERhDwQGjwLNFHv5erizwmWYjTxI1niNbLarVqjJ0qlQ8Tca33h9tnhysHe0u728t7W4s7qwvbSNi2VShXmFlQbV007Es1/Y6fg8CzraHng8atA10aRHUqamtcWmOiOz5ELf62oNE9nxavnt7wq+4q04ApIl0aerhAymhSWHehl9gaorW0gzVci3bh19sSrrSMjW/3wMoifsYv1rd236zOPfb1Kv57bW5HcTC3sba8f7+xekZloqXC4lKKV2vglDKLFPhWJgWKCpHEHmGArcWOaYA8EyOo7MshTFkhibSAEOMSlWiapWoWnNQW+/rEAgUws+bsUk+SUqkQzICDV8VIu4cSeZIIkvguYA80UxWislSPlHIXmGps8TV8fn77eO9tb3N6eW5mdW5ha3l7Xd7F6mrKlVX0BhWHdXWVEtVTUVWJVkRW6qEKro7yJWuY4FDRxpF92aAXu+oE4js+RC3+tqDRPZ8Wn4ce05+zyD8NAwNpAnqBOAlOg1qG75nd9qu45i6oWiGYntWt99Btcza8CV0UZM0x2j3u36/ozg6VstfZuN7p0dzm0svF6b/mHvzZnnu7frSu6uz48uzowuYnn+IX19i6WSpgNWqmXo106hl8UaOxPM0AWQpHOLWDNnEaDxD4xgDkR2BBaXyszwqThpW2PsaoKp9wZC8d0mQ9RFJqhGSIptFhs7jRLpSvc7lzlKpj4n4aSJ2mrgGY+6e7M8svf3Hi3/87flfp+Zf7x5tn8dOi40CzuGyKbt9t91vO11bMVtCi7M8yMZh87Yc1+j320EdD9s0FHAlZO5t3wVjQtquawrsQXg54c2Qe+wJC+8CK0f2vIfInk/Lj2nPMPAMLxaFZzzDy0eKIsP3gT5s6C3b0tq+3e7Aj7WWKra7LipC6up+z3XaVkuXJFXQbFWxFdXVNE+XrRYl0cVmKZ5Lnicv367Mv3j7+q/Pf/2vf/zytz9+m16a33l3+DF2Ca45TyVAprF8NlHMx0uI62IuSzYwspGlwKHNLIOHpfJzHJVj6By6BfWrkAcVolrCqCzmBPFaCYhVi8B1pXBVzl+WctfF/Mn19cGHjyvbO1MLi7+/fv3riz9+ffH895fPljeWD0720rk4wdQkjdEs0XRky1W8ruW0DdNVdbulWTJg2C3TUXRLdlzN8w3X1QxD0jXRsdVO23ZRyeKwYZgQewIwMyHNEZE9H+Kmo30JkT2flh859vwU4S3T+i1ubq4Ob+DXLUcbAD3fU3WEdhudFhlGYgGcJXOVwun1xeb+zvzq0l9//xX42/Pff3/zcmpxbmFzbW1/Z+No/13s4mMqdl3KphrlLFnHyHoar6aa1SA7JrI4DmDNZkim0QhnYCH8FJ0QIEkgPDkwvj6sCaTr9Ym3w5KAWrZezTeqJaJRJpsVCoeZQrMGS7bfHW4c7i1tb0wvLzybfv3L89/+4++//J9f/vvZqzdTs/Orm9snH08zuRxBUy2tZbqGBZvFVUGXlgtylExHtBBSsOQWNuCgBx9uVzAIQBsc5ekh4M0R421mxGN7O1o5sufniez5tET2HDIQqAbYjjoCdGAi1ABtxKCEpGcYjq5ZqqzKgizwEi/IIi9LtMBV8EYyh328uth//277+AAk9XzmzV+e/eP//e//+H/+6//8f7/813/+/ve/vvrjj7nZ14uLM6uri9vb64eHux8+HF1cvLu6Ok+nx4EkOuQ0mRxntBz4mEgcX17CJ8DnwKfNrq/PrCy/nJl+8eb1b388/9tvv/7P3//2l7/+ErKxs729v3f0/uTs6jKVxYrVSoMkSIZRNUPVzGCqK6qqoMGBZEUVg6YOG6Rlu7LtSpYjBMDyFrhyAsdRJr0Z4miRPZ+UyS12P5E9n5afzZ7QdQfP840xEuig5hN0/hHggmFUpVog01FY6mimrRqWqpsI04ag1XR9UILltv12rwtTzTTElsy3JEFt1Ui8yVCkwNGygAssRILpciGWy1xj6Ytk4uP11dHpx+3jw9Xd7cXN9bnVlanF+Tfzc6/nZ1/Nvn05O/NiZvqPmSng+dQbAGZgCSyHn8I6AMzD+rOryyvbW/A58Gkfri/P4zEsl4V/uVyuVCrVajWSJHmelySJ4ziYEUVRlmVVVXVdN00zGHfSAmDWRkUzkeBgi3nwpwXbx3FajiM7juS44E3BtsVgyc3mGnFLmiMiez4xk1vsfiJ7Pi0/pz3HGRfop+05JIxGb+199PS0ZzkuyEY3IGLTIVJTNANUFNzNqGsw4/hep98DW6CTfL7ndtshDnpW323SBM6QBEtRPMOIHCcLgiKJqgzLGxQO1MlmFa9XmjWgDDl4vQJTmIeFNaIBPwVgNXg7QHI0fFr4XvQjvNFqyZqiWobpOW7b8zt+G6ZAt90J52G5A6o04BujC2yGbgCmjiQa3KCJttLAnmj7wMYZChQBMzfbapwbY46DPgQ+6hP2/CQufIE7/fMeInveT2TPp+Wns6cDnXaMTwgUPXE/JgLwBQJl9KPTo4BpgUl1z7faHbfddn3fdV3bdsxuvwOKsuGbWSBQTdMgrkN0eh3Xd8FMoDNA18FE6KJWIDMHFAz7IqxkquktVZM93x4BPx0Bpg4ZLQnXgfeCweFDYAl8YKfr9frtXq8T7AKkbgCVwDR0TVWUYDx9eBH40fJcMDzEyl6n7fc6XaDbbsM8mBWEP9xEg40TeBC2DDg0jDpVx/4EwZqI4aZDoKKukT2fkpuO9iVE9nxafkJ72mN8WqDICLfsGXb+G3WG9kR1SUwVME09+GRwmW27kPGaMIUsvtNF0V14rz58TzBRt+Oj0Ze7nQ4YyzQMtWXqCCsYbdSxgiIptu6BSYfL7ydcDQjfCJ8QLtRVSVNEALZ/uH3CTeR5DsScYFWYAeC7wdeG5fBT+JIWxJyaZuk6JPCOBRvE8j07KAEHGoXvP9xENxF66EpU3mWCYLVbWywAPiGy5xNyq689SGTPp+VnsyfoEnwR/rsRqONANGe4QzsMAqiBPe+qE5U76/Y8iDo9b6hg10Y3SHW84QlQ2wPj+LaNnobSTAPMqDvoFhzN1lXH0DxTR0OKQqzq2V3P6rowb7TBm5aGMFX08jGEb4RPQARL0GeiONS2Pdg6umooCkS1ENuaqm5pJgj6NpYL0jfbvgtmbUM0isZHAYWhPWjDh7sofw9S+LDGYBhLTkoTEfSL21tsxGftOVo4jgf7607/vIfInvcT2fNp+dfY80uBpu8OBqL5ImBl+BWPsed47Dn0Zsgte4IFwJ6IIPecVAAQbkB4owthZkDwIWgEZteHwBMiUN0wFd1ogW664NlhZclgQOYbHEMBV94Q2nPkwZFPgftXG/4IPnAEqohqtMCVYEbHt922A1OQKbwcAS9D0AqAMxyyAlUXRF0U1DluTyTQ4aZAM+PeDAn6xdjmuuGezH20cBwP1gk6/BeDDqi3/fJjc6uvPUi4lSZ897T8X7fl+CDjMvru+Or2fBSQLT5q78LKE5/wIIGmJ5ogcEcBiM+mpUDo2QBw7g13tnDA5K+7Icy4vx62dasOwP3AymNfafJPfiLgVwwOll8COqA+huBdN5v3R+dW236QyJ5Pyzdmz0fu3XD9iQ+5l7DNTfTnP8OYPW9xZws/wO3O8ORM/PkPEr5r8o99UkbfLeKfZ2L3PcBj+9efILLnvdzZJU/IY/duuP7Eh9xL2OYm+vOfYUKaI+5s4Qe43RmenIk//0HCd03+sU/K6LtF/PNM7L4HeGz/+hNE9ryXO7vkCXns3g3Xn/iQewnb3ER//jNMSHPEnS38ALc7w5Mz8ec/SPiuyT/2SRl9t4h/nond9wCP7V+PxjT+f10OO7QlQJTyAAAAAElFTkSuQmCC)

节点映射

  将集群中各个IP节点映射到环上的某一个位置。

  将各个服务器使用Hash进行一个哈希，具体可以选择服务器的IP或主机名作为关键字进行哈希，这样每台机器就能确定其在哈希环上的位置。假如4个节点NodeA、B、C、D，经过IP地址的哈希函数计算(hash(ip))，使用IP地址哈希后在环空间的位置如下： 

![image-20220206012827533](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220206012827533.png)

当我们需要存储一个kv键值对时，首先计算key的hash值，hash(key)，将这个key使用相同的函数Hash计算出哈希值并确定此数据在环上的位置，**从此位置沿环顺时针“行走”**，第一台遇到的服务器就是其应该定位到的服务器，并将该键值对存储在该节点上。

如我们有Object A、Object B、Object C、Object D四个数据对象，经过哈希计算后，在环空间上的位置如下：根据一致性Hash算法，数据A会被定为到Node A上，B被定为到Node B上，C被定为到Node C上，D被定为到Node D上。



**容错性**

假设Node C宕机，可以看到此时对象A、B、D不会受到影响，只有C对象被重定位到Node D。一般的，在一致性Hash算法中，如果一台服务器不可用，则受影响的数据仅仅是此服务器到其环空间中前一台服务器（即沿着逆时针方向行走遇到的第一台服务器）之间数据，其它不会受到影响。简单说，就是C挂了，受到影响的只是B、C之间的数据，并且这些数据会转移到D进行存储。

 扩展性

数据量增加了，需要增加一台节点NodeX，X的位置在A和B之间，那收到影响的也就是A到X之间的数据，重新把A到X的数据录入到X上即可，

不会导致hash取余全部数据重新洗牌。

为了在节点数目发生改变时尽可能少的迁移数据

 

将所有的存储节点排列在收尾相接的Hash环上，每个key在计算Hash后会顺时针找到临近的存储节点存放。

而当有节点加入或退出时仅影响该节点在Hash环上顺时针相邻的后续节点。 

 

优点

加入和删除节点只影响哈希环中顺时针方向的相邻的节点，对其他节点无影响。

 

缺点 

数据的分布和节点的位置有关，因为这些节点不是均匀的分布在哈希环上的，所以数据在进行存储时达不到均匀分布的效果。





1 为什么出现

![graphic](data:application/octet-stream;base64,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)

哈希槽实质就是一个数组，数组[0,2^14 -1]形成hash slot空间。

 

2 能干什么

解决均匀分配的问题，在数据和节点之间又加入了一层，把这层称为哈希槽（slot），用于管理数据和节点之间的关系，现在就相当于节点上放的是槽，槽里放的是数据。

![graphic](data:application/octet-stream;base64,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)

槽解决的是粒度问题，相当于把粒度变大了，这样便于数据移动。

哈希解决的是映射问题，使用key的哈希值来计算所在的槽，便于数据分配。

 

3 多少个hash槽

一个集群只能有16384个槽，编号0-16383（0-2^14-1）。这些槽会分配给集群中的所有主节点，分配策略没有要求。可以指定哪些编号的槽分配给哪个主节点。集群会记录节点和槽的对应关系。解决了节点和槽的关系后，接下来就需要对key求哈希值，然后对16384取余，余数是几key就落入对应的槽里。slot = CRC16(key) % 16384。以槽为单位移动数据，因为槽的数目是固定的，处理起来比较容易，这样数据移动问题就解决了。

Redis 集群中内置了 16384 个哈希槽，redis 会根据节点数量大致均等的将哈希槽映射到不同的节点。当需要在 Redis 集群中放置一个 key-value时，redis 先对 key 使用 crc16 算法算出一个结果，然后把结果对 16384 求余数，这样每个 key 都会对应一个编号在 0-16383 之间的哈希槽，也就是映射到某个节点上。如下代码，key之A 、B在Node2， key之C落在Node3上

 

![graphic](data:application/octet-stream;base64,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)![graphic](data:application/octet-stream;base64,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)

#### 搭建

```bash
docker run --name redis-node-1 -d --net host --privileged=true -v /opt/redis-nodes/redis-node-1:/data redis:6.0.8 --cluster-enabled yes --appendonly yes --port 6381



docker run --name redis-node-2 -d --net host --privileged=true -v /opt/redis-nodes/redis-node-2:/data redis:6.0.8 --cluster-enabled yes --appendonly yes --port 6382



docker run --name redis-node-3 -d --net host --privileged=true -v /opt/redis-nodes/redis-node-3:/data redis:6.0.8 --cluster-enabled yes --appendonly yes --port 6383



docker run --name redis-node-4 -d --net host --privileged=true -v /opt/redis-nodes/redis-node-4:/data  redis:6.0.8 --cluster-enabled yes --appendonly yes --port 6384



docker run --name redis-node-5 -d --net host --privileged=true -v /opt/redis-nodes/redis-node-5:/data redis:6.0.8 --cluster-enabled yes --appendonly yes --port 6385



docker run --name redis-node-6 -d --net host --privileged=true -v /opt/redis-nodes/redis-node-6:/data redis:6.0.8 --cluster-enabled yes --appendonly yes --port 6386
```

进入某一容器之后执行

```bash
redis-cli --cluster create 192.168.0.106:6381 192.168.0.106:6382 192.168.0.106:6383 192.168.0.106:6384 192.168.0.106:6385 192.168.0.106:6386 --cluster-replicas 1

# --cluster-replicas 1表示为每一个master创建一个节点


# 粘贴之后输入‘yes’
# 哈希槽的分配
>>> Performing hash slots allocation on 6 nodes...
Master[0] -> Slots 0 - 5460
Master[1] -> Slots 5461 - 10922
Master[2] -> Slots 10923 - 16383


>>> Performing Cluster Check (using node 192.168.0.106:6381)
M: e9cf6cd8b07d0fd420f6f6d09ff67f58ffb524e5 192.168.0.106:6381
   slots:[0-5460] (5461 slots) master
   1 additional replica(s)
S: dea8451728dd8005a3c513e89ce6c24f306a2a88 192.168.0.106:6385
   slots: (0 slots) slave
   replicates ab92082b8d1e79db3e96cfa231be5a4817c7eb85
M: 0890efd031ac30c23949cb770773ea898254f587 192.168.0.106:6383
   slots:[10923-16383] (5461 slots) master
   1 additional replica(s)
S: ddfbb3e515bca09210e190dc80871cc43302738b 192.168.0.106:6386
   slots: (0 slots) slave
   replicates 0890efd031ac30c23949cb770773ea898254f587
M: ab92082b8d1e79db3e96cfa231be5a4817c7eb85 192.168.0.106:6382
   slots:[5461-10922] (5462 slots) master
   1 additional replica(s)
S: 3e349f1ebbe5e130aa6e0bb47bfca06b44b9f2bd 192.168.0.106:6384
   slots: (0 slots) slave
   replicates e9cf6cd8b07d0fd420f6f6d09ff67f58ffb524e5

```

查看集群信息

```bash
redis-cli -p 6381
# cluster info
127.0.0.1:6381> cluster info
cluster_state:ok
cluster_slots_assigned:16384
cluster_slots_ok:16384
cluster_slots_pfail:0
cluster_slots_fail:0
cluster_known_nodes:6
cluster_size:3
cluster_current_epoch:6
cluster_my_epoch:1
cluster_stats_messages_ping_sent:286
cluster_stats_messages_pong_sent:302
cluster_stats_messages_sent:588
cluster_stats_messages_ping_received:297
cluster_stats_messages_pong_received:286
cluster_stats_messages_meet_received:5
cluster_stats_messages_received:588
# cluster nodes
127.0.0.1:6381> cluster nodes
dea8451728dd8005a3c513e89ce6c24f306a2a88 192.168.0.106:6385@16385 slave ab92082b8d1e79db3e96cfa231be5a4817c7eb85 0 1644155610030 2 connected
0890efd031ac30c23949cb770773ea898254f587 192.168.0.106:6383@16383 master - 0 1644155610000 3 connected 10923-16383
ddfbb3e515bca09210e190dc80871cc43302738b 192.168.0.106:6386@16386 slave 0890efd031ac30c23949cb770773ea898254f587 0 1644155608005 3 connected
ab92082b8d1e79db3e96cfa231be5a4817c7eb85 192.168.0.106:6382@16382 master - 0 1644155609014 2 connected 5461-10922
3e349f1ebbe5e130aa6e0bb47bfca06b44b9f2bd 192.168.0.106:6384@16384 slave e9cf6cd8b07d0fd420f6f6d09ff67f58ffb524e5 0 1644155611043 1 connected
e9cf6cd8b07d0fd420f6f6d09ff67f58ffb524e5 192.168.0.106:6381@16381 myself,master - 0 1644155608000 1 connected 0-5460

```



存一个key

```bash
127.0.0.1:6381> set k1 v1
(error) MOVED 12706 192.168.0.106:6383
# ???
# 因为是集群

# 进入客户端前需要加参数 -c 防止路由失效
 redis-cli -p 6381 -c
 
root@192:/data# redis-cli -p 6381 -c
127.0.0.1:6381> set k1 v1
-> Redirected to slot [12706] located at 192.168.0.106:6383
OK

# 获取集群信息
redis-cli --cluster check 192.168.0.106:6383

```

主从切换

```bash
# 干掉6381
SHUTDOWN
# 查看集群
CLUSTER NODES
master,fail # 代表死亡

# 再次启动6381 变成slave


```

#### 扩容

```bash
docker run --name redis-node-7 -d --net host --privileged=true -v /opt/redis-nodes/redis-node-7:/data redis:6.0.8 --cluster-enabled yes --appendonly yes --port 6387



docker run --name redis-node-8 -d --net host --privileged=true -v /opt/redis-nodes/redis-node-8:/data redis:6.0.8 --cluster-enabled yes --appendonly yes --port 6388
```

运行之后加入集群(在各自的容器中运行)

```bash
redis-cli --cluster add-node 实际ip:集群中的端口  要加入的端口ip

redis-cli --cluster add-node 192.168.0.106:6387 192.168.0.106:6381

# 检查
redis-cli --cluster check 192.168.0.106:6383

# 没有槽位
M: 254016403991879c282b8586dfd60c99888e30f6 192.168.0.106:6387
   slots: (0 slots) master

# 分配槽号
# 重新洗牌
redis-cli --cluster reshard 192.168.0.106:6381

# 您要移动多少个插槽？ AA 槽数16384/master
How many slots do you want to move (from 1 to 16384)?  4096
# 分配给哪一个id
What is the receiving node ID?  254016403991879c282b8586dfd60c99888e30f6

# 输入all 全部重新分配
# 接着输入yes
# 前面的master每一个匀一点
redis-cli --cluster add-node 192.168.0.106:6388 192.168.0.106:6381
```

为什么6387是3个新的区间，以前的还是连续？

重新分配成本太高，所以前3家各自匀出来一部分，从6381/6382/6383三个旧节点分别匀出1364个坑位给新节点6387

 

将6388作为6387de从节点

```bash
redis-cli --cluster add-node 192.168.0.106:6388 192.168.0.106:6387 --cluster-slave --cluster-master-id 254016403991879c282b8586dfd60c99888e30f6 # --> 主节点的编号

# 检查四主四从
redis-cli --cluster check 192.168.0.106:6381
```

#### 缩容

```bash
# 6387的ID
254016403991879c282b8586dfd60c99888e30f6
# 6388的id
1bc0e35ae1296f2c00ee5bd50efd6a6e38191257 

# 删除6388节点  redis-cli --cluster del-node  地址 id
redis-cli --cluster del-node 192.168.0.106:6388 1bc0e35ae1296f2c00ee5bd50efd6a6e38191257

# 检查
redis-cli --cluster check 192.168.0.106:6381

# 清空6387槽号
 redis-cli --cluster reshard 192.168.0.106:6381
 
 # 按照公式 AA 16387/master 
 How many slots do you want to move (from 1 to 16384)?  4096
 
# 根据id分配给6383
What is the receiving node ID? 0890efd031ac30c23949cb770773ea898254f587

# Type 'done' once you entered all the source nodes IDs. done的意思是从哪里剥去节点
# 
Source node #1: 254016403991879c282b8586dfd60c99888e30f6 
# 剥去交给6381
Source node #2: done


# 检查 槽位为0
M: 254016403991879c282b8586dfd60c99888e30f6 192.168.0.106:6387
   slots: (0 slots) master

# 删除87节点
 redis-cli --cluster del-node 192.168.0.106:6387 254016403991879c282b8586dfd60c99888e30f6
```



## 六、DockerFile

基础知识

> 1. 每条保留字指令都必须为大写字母切后面至少要跟随一个参数，
> 2. 每条指令从上向下，顺序执行，
> 3. #表示注释
> 4. 每条指令都会创建一个新的镜像层并对镜像进行提交



### 1） 大致执行流程



(1) docker从基础镜像运行一一个容器

(2)执行一条指令并对容器作出修改

(3) 执行类似docker commit的操作提交一个新的镜像层

(4) docker再基于刚提交的镜像运行一个新容器

(5)执行dockerfile中的 下一条指令直到所有指令都执行完成



从应用软件的角度来看，Dockerfile、Docker镜像与Docker容器分别代表软件的三个不同阶段，

\*  Dockerfile是软件的原材料

\*  Docker镜像是软件的交付品

\*  Docker容器则可以认为是软件镜像的运行态，也即依照镜像运行的容器实例

Dockerfile面向开发，Docker镜像成为交付标准，Docker容器则涉及部署与运维，三者缺一不可，合力充当Docker体系的基石。

1 Dockerfile，需要定义一个Dockerfile，Dockerfile定义了进程需要的一切东西。Dockerfile涉及的内容包括执行代码或者是文件、环境变量、依赖包、运行时环境、动态链接库、操作系统的发行版、服务进程和内核进程(当应用进程需要和系统服务和内核进程打交道，这时需要考虑如何设计namespace的权限控制)等等;

 

2 Docker镜像，在用Dockerfile定义一个文件之后，docker build时会产生一个Docker镜像，当运行 Docker镜像时会真正开始提供服务;

 

3 Docker容器，容器是直接提供服务的





### 2） 常用保留字指令



1. FROM	基础镜像，当前新镜像是基于哪个镜像的，指定一个已经存在的镜像作为模板，第一条必须是from
2. MAINTAINER    镜像维护者的姓名和邮箱地址|
3. RUN    容器构建时所需要的命令 格式1 RUN < >    2.RUN ["",""]
4. EXPOSE   对外暴露的端口
5. WORKDIR   终端默认登录的路径
6. USER    以什么用户执行 默认root
7. ENV     环境变量    `ENV MYSQL_ROOT_PASSWORD 123456` 引用 $MYSQL_ROOT_PASSWORD
8. ADD      将宿主机目录下的文件拷贝进镜像且会自动处理URL和解压tar压缩包
9. COPY    同上
10. VOLUME  数据卷
11. CMD 指定容器启动后要干的事情 会被覆盖
12. ENTRYPOINT  类似于常量  不会被覆盖



* FROM     第一行必须是 来源自哪一个镜像？
* RUN     后面执行一些shell脚本之类的 例如 `RUN yum -y install vim`，也支持json数组的形式`RUN ["yum","-y","install","vim"]`这样
* WORKDIR 默认进入的路径 没有会自动创建  `WORKDIR /usr/local`
* COPY   拷贝 `COPY wjl.txt  /data/txt`  JSON写法 `COPY "wjl.txt"  "/data/txt"`
* ADD    支持url 自动下载  `ADD url  /data/tar` url不会自动解压 本地打包的tar会自动解压
* VOLUME  `VOLUME ["/data"]`
* ENV
* ENTRYPOINT  后面也是shell或者JSON数组 想覆盖掉它的命令的话 必须在docker run时执行`--entrypoint=shell`
* CMD  配合以上使用时必须JSON形式

```dockerfile
ENTRYPOINT ["ls /usr"]
CMD ["/aaa"] # 此时cmd被覆盖 合起来就是 ls /usr/【用户执行run输入的路径】
```



### 3） 案例

docker pull centos

下载jdk 在同一目录下创建`Dockerfile`文件

```dockerfile
FROM centos
LABEL wjl="zzyybs@126.com" 
 
ENV MYPATH /usr/local
WORKDIR $MYPATH
 
#安装vim编辑器
RUN yum -y install vim
#安装ifconfig命令查看网络IP
RUN yum -y install net-tools
#安装java8及lib库
RUN yum -y install glibc.i686
RUN mkdir /usr/local/java
#ADD 是相对路径jar,把jdk-8u171-linux-x64.tar.gz添加到容器中,安装包必须要和Dockerfile文件在同一位置
ADD jdk-8u202-linux-x64.tar.gz  /usr/local/java/
#配置java环境变量
ENV JAVA_HOME /usr/local/java/jdk1.8.0_202
ENV JRE_HOME $JAVA_HOME/jre
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib:$CLASSPATH
ENV PATH $JAVA_HOME/bin:$PATH
 
EXPOSE 80
 
CMD echo $MYPATH
CMD echo "success--------------ok"
CMD /bin/bash
```

```bash
docker bulid -t imageName:tag .
```



查看虚悬镜像

 docker image ls -f dangling=true

删除

docker image prune



### 4） 案例2 jar

```bash
FROM java:8
MAINTAINER WJL
VOLUME /tmp
ADD demo-0.0.1-SNAPSHOT.jar demo.jar
RUN bash -c 'touch /demo.jar'
ENTRYPOINT ["java","-jar","/demo.jar"]
EXPOSE 8080
```

```sh
docker bulid -t demo:1.0 .

docker run --name demo -d -p 8080:8080  demo:1.0
```

访问成功



## 七、docker network

从其架构和运行流程来看，Docker 是一个 C/S 模式的架构，后端是一个松耦合架构，众多模块各司其职。 

 

Docker 运行的基本流程为：

1 用户是使用 Docker Client 与 Docker Daemon 建立通信，并发送请求给后者。

2 Docker Daemon 作为 Docker 架构中的主体部分，首先提供 Docker Server 的功能使其可以接受 Docker Client 的请求。

3 Docker Engine 执行 Docker 内部的一系列工作，每一项工作都是以一个 Job 的形式的存在。

4 Job 的运行过程中，当需要容器镜像时，则从 Docker Registry 中下载镜像，并通过镜像管理驱动 Graph driver将下载镜像以Graph的形式存储。

5 当需要为 Docker 创建网络环境时，通过网络管理驱动 Network driver 创建并配置 Docker 容器网络环境。

6 当需要限制 Docker 容器运行资源或执行用户指令等操作时，则通过 Execdriver 来完成。

7 Libcontainer是一项独立的容器管理包，Network driver以及Exec driver都是通过Libcontainer来实现具体对容器进行的操作。



* 查看docker网络 ` docker network ls`

docker 启动后 默认创建的三个网络

```sh
NETWORK ID     NAME      DRIVER    SCOPE
e870d25f9a27   bridge    bridge    local
1deb4410558a   host      host      local
1b29da45b6a7   none      null      local
```



```sh
[root@192 myfile]# docker network --help

Usage:  docker network COMMAND

Manage networks

Commands:
  connect     Connect a container to a network
  create      Create a network
  disconnect  Disconnect a container from a network
  inspect     Display detailed information on one or more networks
  ls          List networks
  prune       Remove all unused networks
  rm          Remove one or more networks
```



### 1) 网络模式



| 网络模式  | 简介说明                                                     |
| --------- | ------------------------------------------------------------ |
| bridge    | 为每一个容器分配设置ip等，将容器连接到一个docker0 的虚拟网桥【默认】 |
| host      | 容器不会虚拟出自己的网卡，配置自己的ip，使用宿主机的ip端口   |
| none      | 容器有独立的network namespace 但并没有对其进行任何网络设置，如非配veth pair和网桥连接，IP等 |
| container | 新创建的容器不会创建自己的网卡和配置自己的ip。而是和一个指定容器共享ip,端口范围等 |

* --network bridge 
* --network host
* --network none
* --network container:NAME/id



Docker 服务默认会创建一个 docker0 网桥（其上有一个 docker0 内部接口），该桥接网络的名称为docker0，它在内核层连通了其他的物理或虚拟网卡，这就将所有容器和本地主机都放到同一个物理网络。Docker 默认指定了 docker0 接口 的 IP 地址和子网掩码，让主机和容器之间可以通过网桥相互通信。

 

\# 查看 bridge 网络的详细信息，并通过 grep 获取名称项

docker network inspect bridge | grep name

![graphic](data:application/octet-stream;base64,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)

 

ifconfig

![graphic](data:application/octet-stream;base64,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)

1 Docker使用Linux桥接，在宿主机虚拟一个Docker容器网桥(docker0)，Docker启动一个容器时会根据Docker网桥的网段分配给容器一个IP地址，称为Container-IP，同时Docker网桥是每个容器的默认网关。因为在同一宿主机内的容器都接入同一个网桥，这样容器之间就能够通过容器的Container-IP直接通信。

 

2 docker run 的时候，没有指定network的话默认使用的网桥模式就是bridge，使用的就是docker0。在宿主机ifconfig,就可以看到docker0和自己create的network(后面讲)eth0，eth1，eth2……代表网卡一，网卡二，网卡三……，lo代表127.0.0.1，即localhost，inet addr用来表示网卡的IP地址

 

3 网桥docker0创建一对对等虚拟设备接口一个叫veth，另一个叫eth0，成对匹配。

  3.1 整个宿主机的网桥模式都是docker0，类似一个交换机有一堆接口，每个接口叫veth，在本地主机和容器内分别创建一个虚拟接口，并让他们彼此联通（这样一对接口叫veth pair）；

  3.2 每个容器实例内部也有一块网卡，每个接口叫eth0；

  3.3 docker0上面的每个veth匹配某个容器实例内部的eth0，两两配对，一一匹配。

 通过上述，将宿主机上的所有容器都连接到这个内部网络上，两个容器在同一个网络下,会从这个网关下各自拿到分配的ip，此时两个容器的网络是互通的。





![graphic](data:application/octet-stream;base64,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)

 

问题：

   docke启动时总是遇见标题中的警告

原因：

  docker启动时指定--network=host或-net=host，如果还指定了-p映射端口，那这个时候就会有此警告，

并且通过-p设置的参数将不会起到任何作用，端口号会以主机端口号为主，重复时则递增。

解决:

  解决的办法就是使用docker的其他网络模式，例如--network=bridge，这样就可以解决问题，或者直接无视



### 2) 自定义网络



```bash
# 自定义网络
docker network create ap

# 使用网络
docker run -it --name a1 --network ap  -p 8080:80 alpine:latest 

docker run -it --name a2 --network ap  -p 8081:80 alpine:latest 
```

效果 在两个容器内部 a1 直接 `ping a2`

a2直接`ping a1`都可以成功







## 八、docker-compose容器编排

### 1） 简介

Compose 是 Docker 公司推出的一个工具软件，可以管理多个 Docker 容器组成一个应用。你需要定义一个 YAML 格式的配置文件docker-compose.yml，写好多个容器之间的调用关系。然后，只要一个命令，就能同时启动/关闭这些容器

 docker建议我们每一个容器中只运行一个服务,因为docker容器本身占用资源极少,所以最好是将每个服务单独的分割开来但是这样我们又面临了一个问题？

 

如果我需要同时部署好多个服务,难道要每个服务单独写Dockerfile然后在构建镜像,构建容器,这样累都累死了,所以docker官方给我们提供了docker-compose多服务部署的工具

 

例如要实现一个Web微服务项目，除了Web服务容器本身，往往还需要再加上后端的数据库mysql服务容器，redis服务器，注册中心eureka，甚至还包括负载均衡容器等等。。。。。。

 

Compose允许用户通过一个单独的docker-compose.yml模板文件（YAML 格式）来定义一组相关联的应用容器为一个项目（project）。

 

可以很容易地用一个配置文件定义一个多容器的应用，然后使用一条指令安装这个应用的所有依赖，完成构建。Docker-Compose 解决了容器与容器之间如何管理编排的问题。



### 2） 安装

官网教程--> https://docs.docker.com/compose/install/



```sh
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 执行权限
sudo chmod +x /usr/local/bin/docker-compose
```



### 3) 常用命令

```sh
Compose常用命令

docker-compose -h                           # 查看帮助

docker-compose up                           # 启动所有docker-compose服务

docker-compose up -d                        # 启动所有docker-compose服务并后台运行

docker-compose down                         # 停止并删除容器、网络、卷、镜像。

docker-compose exec  yml里面的服务id      # 进入容器实例内部  docker-compose exec docker-compose.yml文件中写的服务id /bin/bash

docker-compose ps                      # 展示当前docker-compose编排过的运行的所有容器

docker-compose top                     # 展示当前docker-compose编排过的容器进程

docker-compose logs  yml里面的服务id     # 查看容器输出日志

docker-compose config     # 检查配置

docker-compose config -q  # 检查配置，有问题才有输出

docker-compose restart   # 重启服务

docker-compose start     # 启动服务

docker-compose stop      # 停止服务
```



### 4） docker-compose.yml

```yaml
services: # 代表多个服务
  tomcat: # 服务名
    image: billygoo/tomcat8-jdk8:latest # 指定运行的镜像
    container_name: "tom"
    ports: # 指定映射
      - "8080:8080"
```

#### volumes

```yaml
version: "3.0" # 选择使用的版本
services: # 代表多个服务
  tomcat: # 服务名 可以自己起
    image: billygoo/tomcat8-jdk8:latest # 指定运行的镜像
    container_name: "tom"
    ports: # 指定映射
      - "8080:8080"
    volumes:
      - tomcatwebapps:/usr/local/tomcat/webapps


# 不支持自动创建所以声明指定的卷名"idea_tomcatwebapps
volumes:
  tomcatwebapps:
# 创建出来会以项目（当前文件夹）加文件名
    external: true
    # 注意使用自定义卷名要自己创建
    # docker volumes create tomcatwebapps
```

#### networks

```yaml
version: "3.0" # 选择使用的版本
services: # 代表多个服务或者一个应用
  tomcat: # 服务名
    image: billygoo/tomcat8-jdk8:latest # 指定运行的镜像
    container_name: "tom"
    ports: # 指定映射
      - "8080:8080"
    networks:
      - hello
  #  tomcat1 tom2 都是可以ping通的
  tomcat1: # 服务名
    image: billygoo/tomcat8-jdk8:latest # 指定运行的镜像
    container_name: "tom2"
    ports: # 指定映射
      - "8081:8080"
    networks:
      - hello
# 自定义网络桥
networks:
  hello:
```

#### 指定命令

```bash
version: "3.0" # 选择使用的版本
services: # 代表多个服务
  tomcat: # 服务名
    image: billygoo/tomcat8-jdk8:latest # 指定运行的镜像
    container_name: "tom"
    ports: # 指定映射
      - "8080:8080"
    networks:
      - hello
  tomcat1: # 服务名
    image: billygoo/tomcat8-jdk8:latest # 指定运行的镜像
    container_name: "tom2"
    ports: # 指定映射
      - "8081:8080"
    networks:
      - hello
  mysqlgo:
    container_name: "mysql"
    image: mysql:latest
    volumes:
      - mysqldata:/var/lib/mysql
      - mysqlconf:/etc/mysql
    environment: # 加 - 就是等号
      - MYSQL_ROOT_PASSWORD=123456
    ports:
      - "3306:3306"
    networks:
      - hello
  myredis:
    container_name: "redis"
    image: redis:6.0.8
    volumes:
      - redisdata:/data
    ports:
      - "6379:6379"

    networks:
      - hello
    command: # "redis-server  --appendonly yes"
      - "redis-server"
      - "--appendonly yes"
# 自定义网络桥 external 必须自己创建
networks:
  hello:
    external: true
volumes:
  mysqldata:
  mysqlconf:
  redisdata:
```



#### 文件外环境变量

```yaml
version: "3.0"
services:
  nacos:
    image: nacos/nacos-server:latest
    ports:
      - "8848:8848"
    env_file:
      - nacos.env
```

env文件

```env
# nacos 必须加一行注释
MODE=standalone  # nacos单实例启动
```



#### 启动顺序

```yaml
version: "3.0"
services:
  nacos:
    image: nacos/nacos-server:latest
    ports:
      - "8848:8848"
    env_file:
      - nacos.env
    networks:
      - hello
    depends_on: # 指定这个容器必须依赖于哪一个容器启动才能启动
      - tomcat-service # 指定服务名而不是镜像容器名
  tomcat-service:
    image: billygoo/tomcat8-jdk8:latest
    container_name: "tom"
    ports:
      - "8080:8080"
    networks:
      - hello
networks:
  hello:
    external: true
    
# 此时启动后台日志可以明显看到nacos在tom之后启动
```



#### 心跳检测

```yaml
healthcheck:
      test: ["CMD","curl","http:localhost"]
      interval: 1m30s
      timeout: 10s
      retries: 3
```

#### 系统参数[少]

```yaml
sysctls: # 修修改容器执行的系统参数
      - net.core.somaxconn=1024
```



#### 容器内进程[少]

```yaml
ulimits:
      nproc: 65535
      nofile:
        soft: 20000
        hard: 40000
```

#### bulid



Dockerfile

```dockerfile
FROM java:8
ADD demo.jar /tmp/demo.jar
ENTRYPOINT ["java -jar","demo.jar"]
EXPOSE 8084
```

build： 运行docker bulid 并直接运行镜像

```yaml
version: "3.0"
services:
  demo:
    #build: . 这样也可以但是警告
    build:
      # 指定路径
      context: /docker
      dockerfile: Dockerfile
    container_name: "demo"
    ports:
      - "8084:8084"
```







```yaml
version: "3"

 

services:

  microService:

    image: zzyy_docker:1.6

    container_name: ms01

    ports:

      - "6001:6001"

    volumes:

      - /app/microService:/data

    networks: 

      - atguigu_net 

    depends_on: 

      - redis

      - mysql

 

  redis:

    image: redis:6.0.8

    ports:

      - "6379:6379"

    volumes:

      - /app/redis/redis.conf:/etc/redis/redis.conf

      - /app/redis/data:/data

    networks: 

      - atguigu_net

    command: redis-server /etc/redis/redis.conf

 

  mysql:

    image: mysql:5.7

    environment:

      MYSQL_ROOT_PASSWORD: '123456'

      MYSQL_ALLOW_EMPTY_PASSWORD: 'no'

      MYSQL_DATABASE: 'db2021'

      MYSQL_USER: 'zzyy'

      MYSQL_PASSWORD: 'zzyy123'

    ports:

       - "3306:3306"

    volumes:

       - /app/mysql/db:/var/lib/mysql

       - /app/mysql/conf/my.cnf:/etc/my.cnf

       - /app/mysql/init:/docker-entrypoint-initdb.d

    networks:

      - atguigu_net

    command: --default-authentication-plugin=mysql_native_password #解决外部无法访问

 

networks: 

   atguigu_net: 
```







## 九、docker 可视化工具



### 1） 安装

官方文档--> https://docs.portainer.io/v/ce-2.6/start/install

```sh
 docker search poratiner # 搜索 有官方和中文版 
 此存储库已弃用。从 2022 年 1 月开始，此存储库的最新标签将指向 Portainer CE 2.X。请改用 portainer/portainer-ce。
 
 docker pull portainer/portainer-ce
 
# 1.--restart=always 表示跟随docker重启
 docker run -d -p 8000:8000 -p 9000:9000 --name=portainer --restart=always -v /var/run/docker.sock:/var/run/docker.sock -v portainer_data:/data portainer/portainer-ce:2.6.3
# 访问9000
```

界面没啥可说的





## 十、容器监控CIG

docker system df/ docker stats命令的进阶



安装

```bash
# 创建一个目录
mkdir
```

```yaml
version: '3.1'
volumes:
  grafana_data: {}
services:
  influxdb:
    image: tutum/influxdb:0.9
    restart: always
    environment:
      - PRE_CREATE_DB=cadvisor
    ports:
      - "8083:8083"
      - "8086:8086"
    volumes:
      - ./data/influxdb:/data
  cadvisor:
    image: google/cadvisor
    links:
      - influxdb:influxsrv
    command:
      - storage_driver=influxdb -storage_driver_db=cadvisor -storage_driver_host=influxsrv:8086
    restart: always
    ports:
      - "8080:8080"
    volumes:
      - /:/rootfs:ro
      - /var/run:/var/run:rw
      - /sys:/sys:ro
      - /var/lib/docker/:/var/lib/docker:ro


  grafana:
    user: "104"
    image: grafana/grafana
    user: "104"
    restart: always
    links:
      - influxdb:influxsrv
    ports:
      - "3000:3000"
    volumes:
      - grafana_data:/var/lib/grafana
    environment:
      - HTTP_USER=admin
      - HTTP_PASS=admin
      - INFLUXDB_HOST=influxsrv
      - INFLUXDB_PORT=8086
      - INFLUXDB_NAME=cadvisor
      - INFLUXDB_USER=root
      - INFLUXDB_PASS=root
```



访问8080，8083,3000

都能出现图形化界面



......





## 十一、教程链接



https://www.bilibili.com/video/BV1gr4y1U7CY



https://www.bilibili.com/video/BV1ZT4y1K75K

