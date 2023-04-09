Spring Cloud



[TOC]

# **黑马篇**



## 简介

![image-20220119163420160](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220119163420160.png)

![image-20220119163707592](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220119163707592.png)

![image-20220119163859258](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220119163859258.png)



## 认识微服务

> 简单来说就是单体应用耦合度高，低代码量的时候没有影响，当代码量组件上升成一个大型的缺点就显现出来，不便于维护
>
> 而微服务架构就有所缓解，根据业务将各个模块进行拆分，每一个业务模块作为独立开发
>
> * 降低耦合度
> * 有利于提升扩展性
>
> 那么服务之间作为单独的模块将如何进行调用呢？怎么知道你挂没挂呢？、

* 不同微服务，不要重复开发相同业务
* 微服务数据独立，不要访问其他服务的数据库
* 微服务可以将自己业务暴露为接口，供其他服务使用

## 远程调用 

### Hello World

```java
 @Autowired
private OrderService orderService;

private final static String USERHOST = "http://localhost:8081/user/";

@Autowired //需要优先注册到容器中
private RestTemplate restTemplate;

@GetMapping("{orderId}")
public Order queryOrderByUserId(@PathVariable("orderId") Long orderId) {
    // 查询到 Order 对象
    Order order = orderService.queryOrderById(orderId);
    // 根据Order对象的ID获取user对象
    User user = restTemplate.getForObject(USERHOST + order.getUserId(), User.class);
    System.out.println(user);
    return order;
}
```

### Eureka

> EurekaServer: 服务注册中心，会记录服务信息，监控心跳【30秒一次】
>
> EurekaClient:  注册在Eureka的服务都称之为客户端
>
> ​	消费者： 根据服务名称在EurekaServer中拉取服务列表，基于负载均衡选取一个
>
> ​	提供者： 注册信息到EurekaServer，每隔30秒发送心跳续约



#### 环境搭建

父工程

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>cloud-demo</artifactId>
        <groupId>cn.itcast.demo</groupId>
        <version>1.0</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>order-service</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <!--mybatis-->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
        </dependency>

    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

新建子工程引入

```xml
<dependencies>
    <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>  
```

boot主启动类添加`EnableEurekaServer`开启服务注册中心

配置application.yaml

```yaml
eureka:
  client:  # eureka的地址信息
    service-url:  # 如果是集群 多个之间逗号隔开
      defaultZone: http://127.0.0.1:10086/eureka
```

客户端的pom文件

```xml
 <dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

客户端的配置文件暂时和上面一样

启动服务

打开服务端【eurekaServer】的端口

![image-20220119202251329](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220119202251329.png)



#### 实现负载均衡

消费者方

```java
 // 修改为在注册中心的实例名称
private final static String USERHOST = "http://userservice/user/";

@Bean
@LoadBalanced // 配合注册中心开启负载均衡
public RestTemplate restTemplate(){
    return new RestTemplate();
}
```

此时用IDEA执行`-Dserver.port=端口号`同时执行两个一样的服务，便可以直观的在控制台发现一人一次的`负载均衡`效果

### Ribbon负载均衡

![image-20220119205555338](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220119205555338.png)

![image-20220119210026177](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220119210026177.png)

> 调整负载均衡制度的方式

1 配置bean注入容器 【在消费者】

```java
@Bean
public IRule getIRule(){
    return new RandomRule();// 随机
}
```

2 在**消费者**的application配置文件中

```yaml
userservice: # 注意 这一同一个服务的实例名 
  ribbon: # 随机
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
```



#### 饥饿加载

Ribbon默认采用懒加载，第一次访问时才会取创建LoadBalanceClient，请求时间会很长。而饥饿加载则会在项目启动时创建，降低第一次访问时的耗时，通过配置开启饥饿加载

```yaml
ribbon:
  eager-load: 
    clients: userservice # 服务实例名
    enabled: true
# 以上就是对一个实例 不同的服务开启饥饿加载 -clients是一个List
```



### Nacos

```cmd
startup.cmd -m standalone //1版本可用 2版本要集群和MySQL
# Linux
./bin/startup.sh -m standalone
```

父工程添加

```xml
  <dependency>
      <groupId>com.alibaba.cloud</groupId>
      <artifactId>spring-cloud-alibaba-dependencies</artifactId>
      <version>2.2.5.RELEASE</version>
      <type>pom</type>
      <scope>import</scope>
</dependency>
```

除去子模块的eureka依赖,然后添加

```xml
 <!--服务注册/发现中心依赖-->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

配置文件

```yaml
spring: 
   cloud:
        nacos:
          discovery:
            cluster-name: localhost:8848
```

**@LoadBalanced** 可以和其配合负载均衡的

#### 分级存储模型

```yaml
cloud:
    nacos:
      server-addr: localhost:8848
      discovery: # 配置集群
        cluster-name: HZ
```

```yaml
userservice: # 集群名称
  ribbon: # 配置优先访问同集群
    NFLoadBalancerRuleClassName: com.alibaba.cloud.nacos.ribbon.NacosRule
```

此时消费者访问同一种服务时，会优先访问同集群的，但是在本地集群选择随机

本集群生产者死了访问其他

可视化修改权重

![image-20220120134040349](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120134040349.png)

【最好设置为0~1】

nameSpace

![image-20220120134715590](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120134715590.png)

```yaml
cloud:
    nacos:
      server-addr: localhost:8848
      discovery:
        cluster-name: HZ
        namespace: 03961c13-1622-42cb-b0a2-6f0035762fb3 # 命名空间的ID
```

环境隔离： 不同空间下不可见



> 临时实例： 服务提供者主动发心跳 宕机主动剔除
>
> 非临时实例： Nacos主动询问 宕机等待恢复健康

Nacos会主动推送消息 和 消费者定时拉取配合

```yaml
cloud:
    nacos:
      server-addr: localhost:8848
      discovery:
        cluster-name: HZ
        ephemeral: false # 设置为非临时实例
```

![image-20220120140503172](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120140503172.png)

![image-20220120140927309](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120140927309.png)

### NACOS统一配置管理

![image-20220120142526109](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120142526109.png)

![image-20220120143151650](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120143151650.png)

引入客户端配置管理依赖

```xml
<!--        nacos配置管理依赖-->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

在服务提供者方

新建一个`bootstrap.yaml/yml`文件

```yaml
spring:
  application: # 服务名称
    name: userservice
  profiles: # 环境
    active: dev
  cloud:
    nacos:
      server-addr: localhost:8848
      discovery:
        cluster-name: HZ
      config: # 文件后缀名
        file-extension: yaml
# 去除服务配置application.yml 中相同的配置
```

配置热更新

感知方式一

```java
@RequestMapping("/user")
@RefreshScope
public class UserController {

    @Autowired
    private UserService userService;
    @Value("${pattern.dateformat}")
    private String pattern;
```

感知方式二[推荐]

```java
@Data
@Component
@ConfigurationProperties(prefix = "pattern")
public class PropertiesConfig {
    private String dateformat;
}
```



#### 多环境共享

![image-20220120150744230](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120150744230.png)



> 云端配置文件加环境名》云端配置文件通用》本地



### Nacos集群配置

1. 搭建数据库，舒适化数据库表结构
2. 下载nacos安装包与配置
3. 启动nacos集群
4. Nginx反向代理



### Feign

消费者导入依赖

```xml
 <dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

```java
@FeignClient("userservice")
public interface UserFeignClient {
    @GetMapping("/user/{id}")
    User getUser(@PathVariable("id") Long id);
}
```

此时，自动注入Web层 可以直接根据查出来的order对象的id获取User对象

日志配置

```yaml
# feign的日志配置
feign:
  client:
    config:
      default:  # 这里可以写默认，也可以写某种具体的实例名称
        loggerLevel: FULL
```

java代码实现

```java
public class FeignConfig {

    @Bean
    public Logger.Level getLogger(){
        return Logger.Level.FULL;
    }

}
// 开启配置类
// 开启Feign客户端
@EnableFeignClients(defaultConfiguration = FeignConfig.class)
// 日志级别指定当前目录有效
@FeignClient(value = "userservice",configuration = FeignConfig.class)
```

#### 性能优化

![image-20220120192508395](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120192508395.png)

```xml
 <dependency>
     <groupId>io.github.openfeign</groupId>
     <artifactId>feign-httpclient</artifactId>
</dependency>
```

开启配置

```yaml
# feign的日志配置
feign:
  client:
    config:
      default:  # 这里可以写默认，也可以写某种具体的实例名称
        loggerLevel: BASIC
  httpclient:
    enabled: true # 开启连接池
    max-connections: 200 # 最大连接数
    max-connections-per-route: 50 #每个路径的最大连接数
```

##### 最佳实践

![image-20220120193731739](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120193731739.png)

![image-20220120193953445](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120193953445.png)

![image-20220120194649004](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120194649004.png)

### 统一网关GateWay

![image-20220120200723143](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120200723143.png)

![image-20220120200754276](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120200754276.png)

新建一个模块

````xml
<!--        引入网关依赖-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
<!--        将自己注册到nacos的服务当中-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
````

并且编写启动类

配置文件

```yaml
spring:
  application:
    name: gateway
  cloud:
    nacos:
      server-addr: 192.168.87.129:8848
    gateway: # 网关路由配置
      routes:
        - id: user_service #路由ID 自定义不重复
          # 路由的地址 前面的lb负载均衡 loadBalnd
          uri: lb://userservice
          predicates: # 【路由断言】
            - Path=/user/** # 断言你请求的路径是符合规则的 以User开头
        - id: order-service
          uri: lb://orederservice
          predicates:
            - Path=/order/**

server:
  port: 10010
```

此时启动服务

http://localhost:10010/order/101

/order/101 便可以进入

![image-20220120203327764](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120203327764.png)

![image-20220120203430297](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120203430297.png)

![image-20220120203643466](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120203643466.png)

![image-20220120204335144](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120204335144.png)

```yaml
 cloud:
    nacos:
      server-addr: 192.168.87.129:8848
    gateway: # 网关路由配置
      routes:
        - id: user_service #路由ID 自定义不重复
          # 路由的地址 前面的lb负载均衡 loadBalnd
          uri: lb://userservice
          predicates: # 【路由断言】
            - Path=/user/** # 断言你请求的路径是符合规则的 以User开头
            # 给单个服务添加请求头
          filters: #过滤器
            - AddRequestHeader=Wjl, WjlNB #添加请求头
        - id: order-service
          uri: lb://orederservice
          predicates:
            - Path=/order/**
          filters: #过滤器
            - AddRequestHeader=Wjl, WjlNB #添加请求头
            # 默认对所有的过滤器都生效
      default-filters: 
        - AddRequestHeader=Wjl, WjlNB #添加请求头
```



#### 全局过滤器GlobalFilter

> 自己写代码提供逻辑，前面的过滤器是固定逻辑的

![image-20220120210655207](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120210655207.png)

```java
/**
 *  GlobalFilter 实现该接口配置网关过滤器处理逻辑
 */
// 放置在容器中
@Component
public class GateWayFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取参数
        MultiValueMap<String, String> queryParams = 			 					exchange.getRequest().getQueryParams();
        String authorization = queryParams.getFirst("authorization");
        // 判断
        if ("admin".equals(authorization)) {
            // 放行
            return chain.filter(exchange);
        }
        // 设置状态码 UNAUTHORIZED 401 未登录
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        // 结束请求
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
// 此时 必须携带参数才能获取 // 不然会报401
```

![image-20220120213442742](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120213442742.png)

![image-20220120213754900](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120213754900.png)



#### 跨域问题处理

![凑服从扥](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120214036829.png)

```yaml
spring:
  cloud:
    gateway:
      globalcors: # 全局的跨域处理
        add-to-simple-url-handler-mapping: true # 解决options请求被拦截问题
        corsConfigurations:
          '[/**]':
            allowedOrigins: # 允许哪些网站的跨域请求
              - "http://localhost:8090"
              - "http://www.leyou.com"
            allowedMethods: # 允许的跨域ajax的请求方式
              - "GET"
              - "POST"
              - "DELETE"
              - "PUT"
              - "OPTIONS"
            allowedHeaders: "*" # 允许在请求中携带的头信息
            allowCredentials: true # 是否允许携带cookie
            maxAge: 360000 # 这次跨域检测的有效期
```





## Docker

### Hello World

> 为了解决：
>
> ​	`开发：我能运行！`
>
> ​	`运维： 放屁！`

![image-20220120220610543](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120220610543.png)

![image-20220120220902610](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120220902610.png)

![image-20220120221037848](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120221037848.png)

![image-20220120221527689](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120221527689.png)

![image-20220120221902966](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120221902966.png)

![image-20220120222102333](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120222102333.png)

![image-20220120222259773](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120222259773.png)

![image-20220120224200280](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120224200280.png)

![image-20220120224357589](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120224357589.png)

### 常用命令

```dockerfile
docker images # 查看镜像
docker load -i `镜像文件`
docker save -o `导出文件` `镜像:tag`
docker rmi `镜像:tag`
docker pull `镜像:tag` # 拉取镜像 不带tag默认最新
```

![image-20220120231209462](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220120231209462.png)

`docker run --name ng -p 80:80 -d nginx`

 起名     别名  进入 机器端口：软件端口 后台运行 镜像名

```bash
docker run --name rs -p 6379:6379 -d redis:latest redis-server --appendonly -yes
# 运行持久化的redis
```



### 数据卷

![image-20220121140221781](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121140221781.png)

![image-20220121140312353](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121140312353.png)

![image-20220121141626593](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121141626593.png)

```bash
docker run --name ng -p 80:80 -v html:/usr/share/nginx/html  -d nginx:latest 
# 将html数据卷【挂载】到Nginx容器的
或者说volume卷就是通用文件夹？
类似于Vue的暗箱操作
```

挂载MySQL

```bash
docker run \
--name mysql \
-e MYSQL_ROOT_PASSWORD=123456 \ 
-p 3307:3306 \
-v /tmp/mysql/conf/hmy.cnf:/etc/mysql/conf.d/hmy.cnf \
-v /tmp/mysql/data:/var/lib/mysql \
-d \
mysql

docker run --name mysql -e MYSQL_ROOT_PASSWORD=123456 -p 3306:3306 -v /tmp/mysql/conf/hmy.cnf:/etc/mysql/conf.d/hmy.cnf -v /tmp/mysql/data:/var/lib/mysql -d mysql:latest
```

![image-20220121153218348](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121153218348.png)

![image-20220121153259540](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121153259540.png)



### Dockerfile 自定义镜像

![image-20220121154511430](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121154511430.png)

![image-20220121154816294](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121154816294.png)

```dockerfile
# 指定基础镜像
FROM ubuntu:16.04
# 配置环境变量，JDK的安装目录
ENV JAVA_DIR=/usr/local

# 拷贝jdk和java项目的包
COPY ./jdk8.tar.gz $JAVA_DIR/
COPY ./docker-demo.jar /tmp/app.jar

# 安装JDK
RUN cd $JAVA_DIR \
 && tar -xf ./jdk8.tar.gz \
 && mv ./jdk1.8.0_144 ./java8

# 配置环境变量
ENV JAVA_HOME=$JAVA_DIR/java8
ENV PATH=$PATH:$JAVA_HOME/bin

# 暴露端口
EXPOSE 8090
# 入口，java项目的启动命令
ENTRYPOINT java -jar /tmp/app.jar




# 指定基础镜像
FROM java:8-alpine
COPY ./docker-demo.jar /tmp/app.jar
# 暴露端口
EXPOSE 8090
# 入口，java项目的启动命令
ENTRYPOINT java -jar /tmp/app.jar

```

```bash
docker build -t javaweb:1.0 .

docker run --name web -p 8090:8090 -d javaweb:1.0
```

![image-20220121163038148](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121163038148.png)

### DockerCompose

![image-20220121163628920](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121163628920.png)

![image-20220121163818755](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121163818755.png)

```bash
[root@wjl08 alp]#  docker-compose --help
Define and run multi-container applications with Docker.

Usage:
  docker-compose [-f <arg>...] [options] [COMMAND] [ARGS...]
  docker-compose -h|--help

Options:
  -f, --file FILE             Specify an alternate compose file
                              (default: docker-compose.yml)
  -p, --project-name NAME     Specify an alternate project name
                              (default: directory name)
  --verbose                   Show more output
  --log-level LEVEL           Set log level (DEBUG, INFO, WARNING, ERROR, CRITICAL)
  --no-ansi                   Do not print ANSI control characters
  -v, --version               Print version and exit
  -H, --host HOST             Daemon socket to connect to

  --tls                       Use TLS; implied by --tlsverify
  --tlscacert CA_PATH         Trust certs signed only by this CA
  --tlscert CLIENT_CERT_PATH  Path to TLS certificate file
  --tlskey TLS_KEY_PATH       Path to TLS key file
  --tlsverify                 Use TLS and verify the remote
  --skip-hostname-check       Don't check the daemon's hostname against the
                              name specified in the client certificate
  --project-directory PATH    Specify an alternate working directory
                              (default: the path of the Compose file)
  --compatibility             If set, Compose will attempt to convert deploy
                              keys in v3 files to their non-Swarm equivalent

Commands:
  build              Build or rebuild services
  bundle             Generate a Docker bundle from the Compose file
  config             Validate and view the Compose file
  create             Create services
  down               Stop and remove containers, networks, images, and volumes
  events             Receive real time events from containers
  exec               Execute a command in a running container
  help               Get help on a command
  images             List images
  kill               Kill containers
  logs               View output from containers
  pause              Pause services
  port               Print the public port for a port binding
  ps                 List containers
  pull               Pull service images
  push               Push service images
  restart            Restart services
  rm                 Remove stopped containers
  run                Run a one-off command
  scale              Set number of containers for a service
  start              Start services
  stop               Stop services
  top                Display the running processes
  unpause            Unpause services
  up                 Create and start containers
  version            Show the Docker-Compose version information
```

### 自定义镜像仓库

![image-20220121172415271](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121172415271.png)

```bash
# 打开要修改的文件
vi /etc/docker/daemon.json

{
  "registry-mirrors": ["https://uc51lue1.mirror.aliyuncs.com"],
  "insecure-registries":["http://192.168.87.129:8080"]
}
```

```bash
# 重加载
systemctl daemon-reload
# 重启docker
systemctl restart docker
```

新建一个文件夹

```bash
mkdir registry-ui
新建文件
touch docker-compose.yml
```



图形化版本

```bash
version: '3.0'
services:
  registry:
    image: registry
    volumes:
      - ./registry-data:/var/lib/registry
  ui:
    image: joxit/docker-registry-ui:static
    ports:
      - 8080:80
    environment:
      - REGISTRY_TITLE=WJL私有仓库
      - REGISTRY_URL=http://registry:5000
    depends_on:
      - registry
```

非图形化界面

```bash
docker run -d \
    --restart=always \
    --name registry	\
    -p 5000:5000 \
    -v registry-data:/var/lib/registry \
    registry
```



```bash
docker-compose up -d
# 构建
```





##  MQ

![image-20220121180054429](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121180054429.png)

![image-20220121200238380](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121200238380.png)

![image-20220121201349509](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121201349509.png)

### RabbitMQ安装

![image-20220121203819198](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121203819198.png)

![image-20220121204005182](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121204005182.png)

![image-20220121204204093](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121204204093.png)

```xml
<!--AMQP依赖，包含RabbitMQ-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
```

![image-20220121224455385](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220121224455385.png)

```yaml
spring:
  rabbitmq:
    password: 123456
    username: wjl
    host: 192.168.87.129
    virtual-host: /
    port: 5672
    listener:
      simple: # 设置每次只能处理一条消息 处理完成才能获取下一条
        prefetch: 1 # 也就能能者多劳
```

```java
@RabbitListener(bindings = @QueueBinding(
    value = @Queue(name="blue"),
    exchange = @Exchange(name = "dexchange",type = ExchangeTypes.DIRECT),
    key = {"blue","red"}
))
public void Driect1(String msg){
    log.info("Listener监听[blue]【{}】",msg);
}

@RabbitListener(bindings = @QueueBinding(
    value = @Queue(name = "yellow"),
    exchange = @Exchange(name = "dexchange",type = ExchangeTypes.DIRECT),
    key = {"yellow","red"}
))
public void Driect2(String msg){
    log.info("Listener监听[yellow]【{}】",msg);
}
```

![image-20220122135732552](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220122135732552.png)

![image-20220122135753472](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220122135753472.png)



## ES

![image-20220122141418230](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220122141418230.png)

![image-20220122141601897](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220122141601897.png)

![image-20220122142036103](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220122142036103.png)

![image-20220122142230314](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220122142230314.png)

![image-20220122142253291](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220122142253291.png)![image-20220122142631548](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220122142631548.png)



### 数据类型

![image-20220129225120185](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220129225120185.png)

![image-20220130144616044](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130144616044.png)

![image-20220130144735079](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130144735079.png)



### 索引【index】

![image-20220129225321929](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220129225321929.png)

![image-20220129225749183](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220129225749183.png)![image-20220129225938695](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220129225938695.png)



### 文档【_doc】

![image-20220129230427910](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220129230427910.png)

![image-20220129230526014](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220129230526014.png)

![image-20220129230855285](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220129230855285.png)

### RestClient

![image-20220130144908534](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130144908534.png)

```java
@Configuration
public class RestClientConfig extends
AbstractElasticsearchConfiguration {
 @Override
 @Bean
 public RestHighLevelClient elasticsearchClient() {
final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
 .connectedTo("172.16.91.10:9200")
 .build();
 return RestClients.create(clientConfiguration).rest();
 }
}
```



#### 索引

![image-20220130145433280](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130145433280.png)

![image-20220130150023744](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130150023744.png)





#### 文档

![image-20220130150554422](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130150554422.png)

![image-20220130153730189](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130153730189.png)

![image-20220130154838173](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130154838173.png)



### 批量增加

![image-20220130162146705](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130162146705.png)

![image-20220130161637000](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130161637000.png)

![image-20220130162125631](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130162125631.png)





### 高级查询

![image-20220130163355995](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130163355995.png)

#### 简单查询

![image-20220130170014319](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130170014319.png)



![image-20220130170255097](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130170255097.png)

![image-20220130170312050](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130170312050.png)



![image-20220130171441359](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130171441359.png)

![image-20220130172218024](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130172218024.png)

![image-20220130172340376](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130172340376.png)



#### 算法排序

![image-20220130173131465](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130173131465.png)

![image-20220130173519825](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130173519825.png)

#### 布尔查询

![image-20220130174603729](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130174603729.png)

![image-20220130174738139](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130174738139.png)

#### 搜索结果处理

![image-20220130202215477](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130202215477.png)

![image-20220130202353898](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130202353898.png)



![image-20220130202724638](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130202724638.png)

![image-20220130202943963](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130202943963.png)

![image-20220130203002093](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130203002093.png)





#### RestClient操作

![image-20220130203718084](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130203718084.png)

![image-20220130204515976](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130204515976.png)

![image-20220130204606288](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130204606288.png)

![image-20220130204915436](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130204915436.png)



##### 结果处理

![image-20220130205429580](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130205429580.png)





![image-20220130205719747](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220130205719747.png)

![image-20220130210011751](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220130210011751.png)

![](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131145526458.png)

```java
 // 2.算分函数查询
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                boolQuery, // 原始查询，boolQuery
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{ // function数组
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                QueryBuilders.termQuery("isAD", true), // 过滤条件
                                ScoreFunctionBuilders.weightFactorFunction(10) // 算分函数
                        )
                }
        );
```

![image-20220131150820173](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131150820173.png)



### ES 高级部分

 #### 数据聚合

##### DSL

![image-20220131152022798](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131152022798.png)

**![image-20220131152431221](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131152431221.png)**

**![image-20220131152744429](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131152744429.png)**



![image-20220131153856422](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131153856422.png)

![image-20220131154055367](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131154055367.png)

![image-20220131154407597](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131154407597.png)



##### RestClient

![image-20220131154627807](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131154627807.png)

![image-20220131154913447](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131154913447.png)

![image-20220131155136480](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131155136480.png)

![image-20220131155529246](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131155529246.png)



### 拼音分词器



#### 安装

解压放置在es安装目录的plugin文件夹下面重启后测试是否成功

```bash
# 测试是否安装成功
POST /_analyze
{
  "text": "拼音分词器", 
  "analyzer": "pinyin"
}
```

结果

```json
{
  "tokens" : [
    {
      "token" : "pin",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 0
    },
    {
      "token" : "pyfcq",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 0
    },
    {
      "token" : "yin",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 1
    },
    {
      "token" : "fen",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 2
    },
    {
      "token" : "ci",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 3
    },
    {
      "token" : "qi",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 4
    }
  ]
}
```

#### 使用

![image-20220131173139834](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131173139834.png)

在官方文档中有很多的配置选项 [详情](https://github.com/medcl/elasticsearch-analysis-pinyin)

![image-20220131173447823](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131173447823.png)

![image-20220131173545181](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131173545181.png)

```json

// 自定义拼音分词器
PUT /test
{
  "settings": {
    "analysis": {
      "analyzer": { 
        "my_analyzer": { 
          "tokenizer": "ik_max_word",
          "filter": "py"
        }
      },
      "filter": {
        "py": { 
          "type": "pinyin",
          "keep_full_pinyin": false,
          "keep_joined_full_pinyin": true,
          "keep_original": true,
          "limit_first_letter_length": 16,
          "remove_duplicated_term": true,
          "none_chinese_pinyin_tokenize": false
        }
      }
    }
  }
}

POST /test/_doc/1
{
  "id": 1,
  "name": "狮子"
}
POST /test/_doc/2
{
  "id": 2,
  "name": "虱子"
}

GET /test/_search
{
  "query": {
    "match": {
      "name": "掉入狮子笼咋办"
    }
  }
}

// 自动补全的索引库
PUT test
{
  "mappings": {
    "properties": {
      "title":{
        "type": "completion"
      }
    }
  }
}
// 示例数据
POST test/_doc
{
  "title": ["Sony", "WH-1000XM3"]
}
POST test/_doc
{
  "title": ["SK-II", "PITERA"]
}
POST test/_doc
{
  "title": ["Nintendo", "switch"]
}

// 自动补全查询
POST /test/_search
{
  "suggest": {
    "title_suggest": {
      "text": "s", // 关键字
      "completion": {
        "field": "title", // 补全字段
        "skip_duplicates": true, // 跳过重复的
        "size": 10 // 获取前10条结果
      }
    }
  }
}

// 酒店数据索引库
PUT /hotel
{
  "settings": {
    "analysis": {
      "analyzer": {
        "text_anlyzer": {
          "tokenizer": "ik_max_word",
          "filter": "py"
        },
        "completion_analyzer": {
          "tokenizer": "keyword",
          "filter": "py"
        }
      },
      "filter": {
        "py": {
          "type": "pinyin",
          "keep_full_pinyin": false,
          "keep_joined_full_pinyin": true,
          "keep_original": true,
          "limit_first_letter_length": 16,
          "remove_duplicated_term": true,
          "none_chinese_pinyin_tokenize": false
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "id":{
        "type": "keyword"
      },
      "name":{
        "type": "text",
        "analyzer": "text_anlyzer",
        "search_analyzer": "ik_smart",
        "copy_to": "all"
      },
      "address":{
        "type": "keyword",
        "index": false
      },
      "price":{
        "type": "integer"
      },
      "score":{
        "type": "integer"
      },
      "brand":{
        "type": "keyword",
        "copy_to": "all"
      },
      "city":{
        "type": "keyword"
      },
      "starName":{
        "type": "keyword"
      },
      "business":{
        "type": "keyword",
        "copy_to": "all"
      },
      "location":{
        "type": "geo_point"
      },
      "pic":{
        "type": "keyword",
        "index": false
      },
      "all":{
        "type": "text",
        "analyzer": "text_anlyzer",
        "search_analyzer": "ik_smart"
      },
      "suggestion":{
          "type": "completion",
          "analyzer": "completion_analyzer"
      }
    }
  }
}

```

![image-20220131215108731](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131215108731.png)

![image-20220131215541714](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131215541714.png)

![image-20220131215812595](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131215812595.png)

![image-20220131220021357](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131220021357.png)

### 自动补全

![image-20220131220416141](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220131220416141.png)

### 案例索引映射

```json
// 酒店数据索引库
PUT /hotel
{
  "settings": {
    "analysis": {
      "analyzer": {
        "text_anlyzer": {
          "tokenizer": "ik_max_word",
          "filter": "py"
        },
        "completion_analyzer": {
          "tokenizer": "keyword",
          "filter": "py"
        }
      },
      "filter": {
        "py": {
          "type": "pinyin",
          "keep_full_pinyin": false,
          "keep_joined_full_pinyin": true,
          "keep_original": true,
          "limit_first_letter_length": 16,
          "remove_duplicated_term": true,
          "none_chinese_pinyin_tokenize": false
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "id":{
        "type": "keyword"
      },
      "name":{
        "type": "text",
        "analyzer": "text_anlyzer",
        "search_analyzer": "ik_smart",
        "copy_to": "all"
      },
      "address":{
        "type": "keyword",
        "index": false
      },
      "price":{
        "type": "integer"
      },
      "score":{
        "type": "integer"
      },
      "brand":{
        "type": "keyword",
        "copy_to": "all"
      },
      "city":{
        "type": "keyword"
      },
      "starName":{
        "type": "keyword"
      },
      "business":{
        "type": "keyword",
        "copy_to": "all"
      },
      "location":{
        "type": "geo_point"
      },
      "pic":{
        "type": "keyword",
        "index": false
      },
      "all":{
        "type": "text",
        "analyzer": "text_anlyzer",
        "search_analyzer": "ik_smart"
      },
      "suggestion":{
          "type": "completion",
          "analyzer": "completion_analyzer"
      }
    }
  }
}

```

![image-20220201135648085](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201135648085.png)

![](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201135758380.png)





### 数据同步

##### 提出问题与解决方案

![image-20220201144224440](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201144224440.png)

![image-20220201144356082](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201144356082.png)

![image-20220201144508399](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201144508399.png)

![image-20220201144625270](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201144625270.png)

![image-20220201144720693](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201144720693.png)



### 集群

![image-20220201163253897](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201163253897.png)

![image-20220201172727375](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201172727375.png)

![image-20220201173236974](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201173236974.png)

![image-20220201173432047](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201173432047.png)

![image-20220201173543364](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201173543364.png)

![image-20220201174630213](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201174630213.png)

![image-20220201174750275](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201174750275.png)

![image-20220201174924058](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201174924058.png)

![image-20220201175019983](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201175019983.png)

![image-20220201175227011](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201175227011.png)

![image-20220201175519092](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201175519092.png)

![image-20220201180016991](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201180016991.png)

## Sentinel

#### 介绍与安装

![image-20220201200927642](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201200927642.png)

![image-20220201200949400](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201200949400.png)

![image-20220201201533680](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201201533680.png)

![image-20220201202114067](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201202114067.png)

##### 介绍

![image-20220201202632866](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201202632866.png)

[官网](https://github.com/alibaba/Sentinel)

	##### 控制台安装

![image-20220201203547222](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201203547222.png)

![image-20220201203608842](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201203608842.png)

##### 依赖与配置

```xml
整合sentinel的依赖
-->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>
```

```yaml
 cloud:
    sentinel:
      transport:
        # sentinel 控制台信息
        dashboard: 192.168.0.104:8080
```

> 然后随便访问某个开启的断点，触发监控、



#### 限流规则

![image-20220201212531193](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201212531193.png)

![image-20220201212605718](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201212605718.png)

![image-20220201215522174](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201215522174.png)

![image-20220201215538137](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201215538137.png)

![image-20220201215917784](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201215917784.png)

![image-20220201222511750](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201222511750.png)



![image-20220201224121822](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201224121822.png)

![image-20220201224439863](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201224439863.png)

![image-20220201224924140](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201224924140.png)

![image-20220201230212436](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201230212436.png)

##### 防止一直点

![image-20220201231222255](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201231222255.png)

![image-20220201231959180](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201231959180.png)



![image-20220201231400331](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220201231400331.png)

> 所以要在MVC资源上添加`@SentinelResource("ID")`注解填写资源名称

```yaml
 web-context-unify: false # 关闭context整合
```



#### 隔离和降级

##### 整合

![image-20220202152345979](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202152345979.png)



![image-20220202153355942](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202153355942.png)

```yaml
# feign的日志配置
feign:
  client:
    config:
      default:  # 这里可以写默认，也可以写某种具体的实例名称
        loggerLevel: BASIC
  httpclient:
    enabled: true # 开启连接池
    max-connections: 200 # 最大连接数
    max-connections-per-route: 50 #每个路径的最大连接数
    # 开启feign的sentinel功能
  sentinel:
    enabled: true
```

![image-20220202153811374](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202153811374.png)

![image-20220202154421024](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202154421024.png)

```java
/**
 * 业务降级的处理逻辑
 *  实现FallbackFactory<UserFeignClient> 接口 泛型为调用者
 */
@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserFeignClient> {
    @Override
    public UserFeignClient create(Throwable cause) {
        return new UserFeignClient() {
            @Override
            public User getUser(Long id) {
                // 编写降级逻辑
                log.info("查询用户为空");
                return new User();
            }
        };
    }
}

@Bean
public UserClientFallbackFactory userClientFallbackFactory(){
    return new UserClientFallbackFactory();
}

// 日志级别指定当前目录有效
@FeignClient(value = "userservice",
// 指定降级所处理的字节码文件
fallbackFactory = UserClientFallbackFactory.class)
public interface UserFeignClient {
    @GetMapping("/user/{id}")
    User getUser(@PathVariable("id") Long id);
}

注意
    @EnableFeignClients(clients = {UserFeignClient.class},
        // 指定配置
defaultConfiguration = FeignConfig.class) //指定字节码文件
```

![image-20220202172549197](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202172549197.png)



##### 线程隔离



![image-20220202173408489](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202173408489.png)

​	![image-20220202173800936](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202173800936.png)

![image-20220202173906454](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202173906454.png)

#### 熔断降级

![image-20220202174526082](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202174526082.png)

![image-20220202174805074](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202174805074.png)

![image-20220202175323379](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202175323379.png)

#### 授权规则

![image-20220202190922687](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202190922687.png)

![image-20220202191214023](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202191214023.png)

![image-20220202191348110](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202191348110.png)

![image-20220202191442280](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202191442280.png)

```java
/**
 *  监控
 */
// 放在容器中 不然不生效
@Component
public class HeaderOriginParser implements RequestOriginParser
{
    @Override
    public String parseOrigin(HttpServletRequest httpServletRequest) {
        String origin = httpServletRequest.getHeader("origin");
        if (StringUtils.isEmpty(origin)) {
            origin = "bank";
        }
        return origin;
    }
}
```

![image-20220202194036805](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202194036805.png)

![image-20220202194109233](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202194109233.png)

 ![image-20220202194815656](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202194815656.png)



###  规则持久化

![image-20220202195411629](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202195411629.png)

![image-20220202195615954](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202195615954.png)





## 分布式事务【基于seata】



![image-20220202201338486](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202201338486.png)

![image-20220202201545358](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202201545358.png)

 #### 理论基础

![image-20220202212400278](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202212400278.png)

![image-20220202212448098](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202212448098.png)

![image-20220202212526050](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202212526050.png)

![image-20220202212910940](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202212910940.png)![image-20220202213048401](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202213048401.png)

![image-20220202213320936](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202213320936.png)

![image-20220202215713372](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202215713372.png)![image-20220202215744182](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202215744182.png)

![image-20220202215922483](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202215922483.png)

#### seata

![image-20220202221129593](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202221129593.png)

![image-20220202221209916](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220202221209916.png)

#### 注册

使用前的配置

```properties
registry {
  # 注册中心类型
  # file 、nacos 、eureka、redis、zk、consul、etcd3、sofa
  type = "nacos"

  nacos {
    application = "seata-server"
    serverAddr = "192.168.0.101:8848"
    #最好和所要使用的事务微服务一组
    group = "DEFAULT_GROUP"
    namespace = "" 
    cluster = "SH" # 集群 和nacos一致就行
    username = "nacos"
    password = "nacos"
  }
  eureka {
    serviceUrl = "http://localhost:8761/eureka"
    application = "default"
    weight = "1"
  }
  redis {
    serverAddr = "localhost:6379"
    db = 0
    password = ""
    cluster = "default"
    timeout = 0
  }
  zk {
    cluster = "default"
    serverAddr = "127.0.0.1:2181"
    sessionTimeout = 6000
    connectTimeout = 2000
    username = ""
    password = ""
  }
  consul {
    cluster = "default"
    serverAddr = "127.0.0.1:8500"
    aclToken = ""
  }
  etcd3 {
    cluster = "default"
    serverAddr = "http://localhost:2379"
  }
  sofa {
    serverAddr = "127.0.0.1:9603"
    application = "default"
    region = "DEFAULT_ZONE"
    datacenter = "DefaultDataCenter"
    cluster = "default"
    group = "SEATA_GROUP"
    addressWaitTime = "3000"
  }
  file {
    name = "file.conf"
  }
}

config {
  # 存放文件的位置 最好交给服务作为管理
  # file、nacos 、apollo、zk、consul、etcd3
  type = "naocs"

  nacos {
    serverAddr = "192.168.0.101:8848"
    namespace = ""
    group = "SEATA_GROUP"
    username = "nacos"
    password = "nacos"
    # 配置文件名同nacos的云端配置yaml 自己先添加
    dataId = "seataServer.properties"
  }
  consul {
    serverAddr = "127.0.0.1:8500"
    aclToken = ""
  }
  apollo {
    appId = "seata-server"
    ## apolloConfigService will cover apolloMeta
    apolloMeta = "http://192.168.1.204:8801"
    apolloConfigService = "http://192.168.1.204:8080"
    namespace = "application"
    apolloAccesskeySecret = ""
    cluster = "seata"
  }
  zk {
    serverAddr = "127.0.0.1:2181"
    sessionTimeout = 6000
    connectTimeout = 2000
    username = ""
    password = ""
    nodePath = "/seata/seata.properties"
  }
  etcd3 {
    serverAddr = "http://localhost:2379"
  }
  file {
    name = "file.conf"
  }
}

```



```xml
 <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
            <exclusions>
            <!--排除低版本-->
                <exclusion>
                    <groupId>io.seata</groupId>
                    <artifactId>seata-spring-boot-starter</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
<!--    seata starter-->
        <dependency>
            <groupId>io.seata</groupId>
            <artifactId>seata-spring-boot-starter</artifactId>
            <version>${seata.version}</version>
        </dependency>
```

![image-20220203131428692](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203131428692.png)

配置文件

```yaml
seata:
  registry:
    nacos:
      application: seata-server # 注意和seata配置文件一致
      group: DEFAULT_GROUP
      namespace: ""
      server-addr: 127.0.0.1:8845
      username: nacos
      password: nacos
    type: nacos
  tx-service-group: seata-demo
  service:
    vgroup-mapping:
      seata-demo: SH
```



#### XA模式

![image-20220203133910766](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203133910766.png)

![image-20220203134131679](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203134131679.png)

![image-20220203134340165](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203134340165.png)

![image-20220203134819176](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203134819176.png)



#### AT模式

![image-20220203140157378](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203140157378.png)

![image-20220203140304719](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203140304719.png)

![image-20220203140410205](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203140410205.png)

![image-20220203141003956](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203141003956.png)

![image-20220203141647021](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203141647021.png)

![image-20220203141904571](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203141904571.png)

![image-20220203142009328](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203142009328.png)

![image-20220203142045050](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203142045050.png)

```sql
/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50622
 Source Host           : localhost:3306
 Source Schema         : seata_demo

 Target Server Type    : MySQL
 Target Server Version : 50622
 File Encoding         : 65001

 Date: 20/06/2021 12:39:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`  (
  `branch_id` bigint(20) NOT NULL COMMENT 'branch transaction id',
  `xid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'global transaction id',
  `context` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'undo_log context,such as serialization',
  `rollback_info` longblob NOT NULL COMMENT 'rollback info',
  `log_status` int(11) NOT NULL COMMENT '0:normal status,1:defense status',
  `log_created` datetime(6) NOT NULL COMMENT 'create datetime',
  `log_modified` datetime(6) NOT NULL COMMENT 'modify datetime',
  UNIQUE INDEX `ux_undo_log`(`xid`, `branch_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'AT transaction mode undo table' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of undo_log
-- ----------------------------



-- ----------------------------
-- Table structure for lock_table
-- ----------------------------
DROP TABLE IF EXISTS `lock_table`;
CREATE TABLE `lock_table`  (
  `row_key` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `xid` varchar(96) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `transaction_id` bigint(20) NULL DEFAULT NULL,
  `branch_id` bigint(20) NOT NULL,
  `resource_id` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `table_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pk` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `gmt_create` datetime NULL DEFAULT NULL,
  `gmt_modified` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`row_key`) USING BTREE,
  INDEX `idx_branch_id`(`branch_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;


SET FOREIGN_KEY_CHECKS = 1;
```



#### TCC

![image-20220203142944801](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203142944801.png)

![image-20220203143459371](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203143459371.png)

![image-20220203143622074](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203143622074.png)

![image-20220203144145908](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203144145908.png)

![image-20220203144647125](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203144647125.png)

![image-20220203144811892](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203144811892.png)

```java
@LocalTCC
public interface AccountTccService {

    @TwoPhaseBusinessAction(name="prepare",
            commitMethod ="confirm",
            rollbackMethod = "cancel")
    // 设置字符串可以方便BusinessActionContext获取
    void prepare(@BusinessActionContextParameter(paramName = "userId") String userId,
                 @BusinessActionContextParameter(paramName = "money") int money);

    Boolean confirm(BusinessActionContext context);

    Boolean cancel(BusinessActionContext context);
}
```

实现类

```java
@Service
public class AccountTccServiceImpl implements AccountTccService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AccountFreezeMapper freezeMapper;


    @Override
    // 加上事务注解防止扣减余额失败导致扣错
    @Transactional
    public void prepare(String userId, int money) {
        // 获取事务id
        String xid = RootContext.getXID();
        // 悬挂的问题
        if (freezeMapper.selectById(xid) != null) {
            // 代表有冻结的记录 执行过了 直接结束任务
            return;
        }
        // 1. 扣减可用余额
        accountMapper.deduct(userId,money);
        // 2. 记录冻结金额
        AccountFreeze freeze = new AccountFreeze();
        freeze.setXid(xid);
        freeze.setUserId(userId);
        freeze.setFreezeMoney(money);
        freeze.setState(AccountFreeze.State.TRY);

        freezeMapper.insert(freeze);
    }

    @Override
    public Boolean confirm(BusinessActionContext context) {
        // 1,。 获取事务id 根据id删除冻结记录
        String xid = context.getXid();
        int count = freezeMapper.deleteById(xid);
        // 证明是否删除成功
        return count == 1;
    }

    /**
     *  恢复冻结的钱 这些可以直接从数据库获取
     *  也可以从上下文对象获取
     * @param context
     * @return
     */
    @Override
    public Boolean cancel(BusinessActionContext context) {
        AccountFreeze freeze = freezeMapper.selectById(context.getXid());

        // 空回滚的判断
        if (freeze == null) {
            // 获取id通过注解修饰的字符串
            String userId = context.getActionContext("userId").toString();
            // 证明try没有执行 此时插入一条数据证明回滚过
            freeze = new AccountFreeze();
            freeze.setXid(context.getXid());
            freeze.setUserId(userId);
            freeze.setFreezeMoney(0);
            freeze.setState(AccountFreeze.State.CANCEL);
            freezeMapper.insert(freeze);
            return true;
        }
        // 判断幂等性 是否执行过
        if (freeze.getState()==AccountFreeze.State.CANCEL){
            return true;
        }
        // 恢复钱
        accountMapper.refund(freeze.getUserId(),freeze.getFreezeMoney());
        // 将状态改为cancel 冻结金额清零
        freeze.setState(AccountFreeze.State.CANCEL);
        freeze.setFreezeMoney(0);

        int count = freezeMapper.updateById(freeze);
        return count == 1;
    }
}

```

> 注意 之后将web层的service置换为该接口 
>
> 由业务的不同所以逻辑是不一样的 只是一个demo



#### saga

![image-20220203155832157](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203155832157.png)

#### 对比

![image-20220203160005597](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203160005597.png)







## 分布式缓存

### RDB和AOF

![image-20220203162429100](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203162429100.png)

![image-20220203165849953](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203165849953.png)

![image-20220203165908039](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203165908039.png)

![image-20220203170707126](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203170707126.png)



![image-20220203170729760](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203170729760.png)

### 主从架构

![image-20220203172917832](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203172917832.png)

![image-20220203175206124](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203175206124.png)

![image-20220203175716049](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203175716049.png)

### 哨兵

![image-20220203185258526](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203185258526.png)

![image-20220203185632397](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203185632397.png)

![image-20220203185747184](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203185747184.png)

![image-20220203194253328](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203194253328.png)

![image-20220203194429017](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203194429017.png)

### Redis分片集群

![image-20220203195619578](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203195619578.png)

![image-20220203201256675](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203201256675.png)

![image-20220203201449344](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203201449344.png)

![image-20220203202141670](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203202141670.png)

![image-20220203203422583](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203203422583.png)

![image-20220203203746617](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203203746617.png)

![image-20220203203838136](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203203838136.png)



## 多级缓存

![image-20220203211240094](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203211240094.png)

### 进程缓存

![image-20220203221350520](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203221350520.png)

![image-20220203222119337](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203222119337.png)

![image-20220203222801537](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203222801537.png)

```java
@Configuration
public class CaffeineConfig {

    @Bean
    public Cache<Long, Item> itemCache(){
        return Caffeine.newBuilder()
                .initialCapacity(100)// 设置初始化值
                .maximumSize(10_000) // 设置最大上
                .build();
    }

    @Bean
    public Cache<Long, ItemStock> itemStockCache(){
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(10_000)
                .build();
    }

}
```



### Lua

![image-20220203230402125](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203230402125.png)

![image-20220203230726552](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203230726552.png)

![image-20220203231256699](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203231256699.png)

![image-20220203231553073](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220203231553073.png)

### OpenResty

![image-20220204153451860](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220204153451860.png)

![image-20220204230111119](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220204230111119.png)

![image-20220204230334931](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220204230334931.png)

```nginx
```

![image-20220204231701674](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220204231701674.png)

![image-20220210170805425](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210170805425.png)

![image-20220210172229389](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210172229389.png)

![image-20220210172618091](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210172618091.png)

![image-20220210191737241](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210191737241.png)

![image-20220210192216287](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210192216287.png)

![image-20220210192251883](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210192251883.png)

![image-20220210192627292](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210192627292.png)



![image-20220210193625492](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210193625492.png)

### Canal

和数据库的协同安装查看文档



![image-20220210202648638](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210202648638.png)

![image-20220210202825022](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210202825022.png)

![image-20220210203023794](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210203023794.png)





## MQ

![image-20220210193509440](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210193509440.png)



![image-20220210204010033](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210204010033.png)



### 生产者消息确认

![image-20220210204721855](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210204721855.png)

![image-20220210210007420](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210210007420.png)

![image-20220210211533881](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210211533881.png)

![image-20220210211744157](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210211744157.png)



```java
@Test
    public void testSendMessage2SimpleQueue() throws InterruptedException {
        String routingKey = "simple.test";
        String message = "hello, spring amqp!";

        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        correlationData.getFuture().addCallback(
                // 成功 到交换机
                new SuccessCallback<CorrelationData.Confirm>() {
                    @Override
                    public void onSuccess(CorrelationData.Confirm confirm) {
                        // 判断是否成功？
                        if (confirm.isAck()) {
                            log.info("发送成功");
                        }else {
                            log.info("消息投递到交换机失败 id为{}",correlationData.getId());
                        }
                    }
                                                },
                // 失败 到队列
                new FailureCallback() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        log.info("消息失败 原因：{}",throwable);
                        //rabbitTemplate.convertAndSend("amq.topic", routingKey, message, correlationData);
                    }
                });

        // 到了交换机 但是没有到队列会触发 returnedMessage
        rabbitTemplate.convertAndSend("amq.topic", routingKey, message, correlationData);
    }
```



### 持久化

![image-20220210215121422](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210215121422.png)

![image-20220210215313866](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210215313866.png)



**默认都是持久化**



### 消费者消息确认

![image-20220210220609136](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210220609136.png)

#### 失败重试机制

![image-20220210222242414](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210222242414.png)



![image-20220210223125576](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210223125576.png)

![image-20220210223314051](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220210223314051.png)





### 死信交换机

![image-20220211102917296](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220211102917296.png)



### TTL

![image-20220211103525827](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220211103525827.png)



![image-20220211105637868](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220211105637868.png)

![image-20220211105715305](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220211105715305.png)

```java
// 监听延时消息
@Slf4j
@Component
public class DlMsgListen {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dl.queue", durable = "true"),
            exchange = @Exchange(name = "dl.direct"),
            key = "dl"
    ))
    public void dlMsg(String msg){
      log.info("接收到延时消息：【{}】",msg);
    }

}

@Configuration
public class TTLMsgConfig {

    @Bean
    public Queue ttlQueue(){
        return  QueueBuilder.durable("ttl.queue")
                .ttl(10000)
                // 超时消息的交换机
                .deadLetterExchange("dl.direct")
                // 超时消息的Key
                .deadLetterRoutingKey("dl")
                .build();
    }

    @Bean
    public DirectExchange ttlExchange(){
        return new DirectExchange("ttl.direct");
    }

    @Bean
    public Binding ttlBinDing(){
        return BindingBuilder.bind(ttlQueue()).to(ttlExchange()).with("ttl");
    }
}

```

### 延迟队列

![image-20220211112618001](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220211112618001.png)

![image-20220211112742976](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220211112742976.png)

![image-20220211112830250](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220211112830250.png)

### 消息堆积问题

![image-20220211124503311](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220211124503311.png)![image-20220211124844541](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220211124844541.png)



![image-20220211124922803](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220211124922803.png)

