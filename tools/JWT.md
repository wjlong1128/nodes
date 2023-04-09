# **JWT**



# 一、简介

[官网链接](jwt.io)

![image-20220117133236313](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220117133236313.png)

## JWT能做什么？

> 1. 一旦用户登录，每个后续请求将包含JWT，从而允许该用户访问令牌的路由、服务与资源。单点登录是当今广泛使用的一项功能，因为他的开销很小，且可以在不同的域中轻松使用
> 2.  JOSN WEB Token是在各方面之间安全传输信息的方法之一。因为可以对JWT进行签名，所以可以确保发件人是所描述的`那个人`。由于签名是使用 **标头**和**有效负载**计算的，可以确保验证信息是否为被篡改的。

## 为什么选择JWT？

* 基于Http协议，以往的java web验证用户是否登录是基于会话技术中的`Session`与`Cookie`技术做到；
* 相应而来的缺点：认证的用户过多，服务器的开销就会很大，在基于分布式的环境下，很难做到负载均衡来进行认证，随之而来的扩展能力降低
* 前后端分离的情况下，痛苦就对了

> JWT认证的情况下可以相应的解决一些问题
>
> 客户端					  服务端
>
> 用户认证					通过生成JWT
>
> 本地保存JWT 
>
> 请求系统API  			拦截器拦截并且认证JWT
>
> 展示数据或者错误信息  

## JWT的结构

令牌组成

```txt
token  string ====> header.payload.Signature
- 标头
- 有效载荷
- 签名
```

标头信息

```json
{
    "alg":"HS256",
    "typ":"JWT"
}
// 经过Base64 编码之后就是 xxxxxx
```

有效负载

````json
// 其中包含声明 通常是有关实体和其他数据的声明
{
    "sub":"123456",
    "name":"admin",
    "admin":true""
}
// 也是通过Base64编码；官方提示：不要放用户密码等敏感信息
````

签名

> 就是前面两部分相加 再加上自定义的随机盐生成签名 与 后期登录的主体进行对比
>
> ！！！ 不能泄露

最后所展示的数据结构

```txt
jkgfsdjkfghskdjfgdsjkfds
ljlksdfjsldkfjdsklfs
h4k23g45j23kfg45jh23g
```





# 二、Hello World!

	## 相应依赖

```xml
<dependency>
     <groupId>com.auth0</groupId>
     <artifactId>java-jwt</artifactId>
     <version>3.18.2</version>
</dependency>
```

## Hello World

```java
@Test
    void contextLoads() {
        Map<String,Object> map = new HashMap<>();

        // java的日历类
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND,200);

        String token = JWT.create()
                .withHeader(map) //header
                .withClaim("id", 123) // payload 不敏感的信息都可以放
                .withClaim("name", "zhangsan")
                .withExpiresAt(instance.getTime()) // 指定过期时间
                .sign(Algorithm.HMAC256("salt"));//签名 //用算法类加入秘钥
        System.out.println(token);
        //eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiemhhbmdzYW4iLCJpZCI6MTIzLCJleHAiOjE2NDI0MDE4NTR9.j_ERDf-4Z6XyhPu3ksEBjN5ZoLZv8ydFhOjeYnqiwoM
    }

    @Test
    void test(){
        // 验证对象
        Verification salt = JWT.require(Algorithm.HMAC256("salt"));
        JWTVerifier build = salt.build();
        DecodedJWT verify = build.verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiemhhbmdzYW4iLCJpZCI6MTIzLCJleHAiOjE2NDI0MDE4NTR9.j_ERDf-4Z6XyhPu3ksEBjN5ZoLZv8ydFhOjeYnqiwoM");
        // 如果通过 可以根据key get负载信息
        // 放什么类型 拿什么类型
        System.out.println(verify.getClaim("id").asString());
        System.out.println(verify.getClaim("name").asString());
    }
```



# 三、整合SpringBoot



## 封装工具类

```java
public class JwtUtil {

    private static String SING = "WJLwjlSing";


    /**
     *  根据传过来不敏感的信息Map生成对应的token
     * @param map
     * @return
     */
    public static String getToken(Map<String,String> map ){

        //默认七天过期
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE,7);

        Map<String,Object> headermaps = new HashMap<>();
        JWTCreator.Builder builder = JWT.create().withHeader(headermaps);

        map.forEach(builder::withClaim);

        return builder.withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(SING));
    }

    /**
     *  根据传过来的token验证是否合法
     * @param token
     */
    public static void verity(String token){
       JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
    }

    /**
     *  根据传过来的Token返回Token信息
     * @param token
     * @return
     */
    public static DecodedJWT getDecodedJWT(String token){
        return JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
    }
}
```



## DEMO

流程：

> 前台传过来数据====> 数据库验证===>yes就返回token



配合的表

![image-20220117160428549](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220117160428549.png)

```java
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String,Object> map = new HashMap<>();
        // 获取请求头中的token
        String token = request.getHeader("token");
        try {
            JwtUtil.getDecodedJWT(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("mgs",e.getMessage()); // 实际异常要写清楚
        }
        map.put("static",false);
        response.getWriter().println(new ObjectMapper().writeValueAsString(map));
        response.setContentType("application/json;charset=UTF-8");
        return false;
    }
} 
```

