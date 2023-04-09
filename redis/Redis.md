Redis

[toc]



# 1.命令



## 一、通用命令

![image-20220501113805056](Redis.assets\image-20220501113805056.png)

## 二、基本数据类型

![image-20220429220012131](C:\Users\wangjianlong\AppData\Roaming\Typora\typora-user-images\image-20220429220012131.png)



### 2.1 String

![image-20220430163627895](Redis.assets\image-20220430163627895.png)

![image-20220430163716250](Redis.assets\image-20220430163716250.png)

### 2.2 Key的结构

![image-20220501105538300](Redis.assets\image-20220501105538300.png)

![image-20220501105651520](Redis.assets\image-20220501105651520.png)



### 2.3 Hash

![image-20220501105846508](Redis.assets\image-20220501105846508.png)

![image-20220501105857113](Redis.assets\image-20220501105857113.png)

### 2.4 List



![image-20220501114324385](Redis.assets\image-20220501114324385.png)

![image-20220501114303342](Redis.assets\image-20220501114303342.png)



### 2.5 Set

![image-20220501114827474](Redis.assets\image-20220501114827474.png)

![image-20220501115445655](Redis.assets\image-20220501115445655.png)

![image-20220501115744477](Redis.assets\image-20220501115744477.png)

```bash
127.0.0.1:6379> SADD zs ls ww zl
(integer) 3
127.0.0.1:6379> SADD ls ww mz eg
(integer) 3
127.0.0.1:6379> SINTER zs ls
1) "ww"
127.0.0.1:6379> SDIFF zs li
1) "ls"
2) "zl"
3) "ww"
127.0.0.1:6379> SDIFF zs ls
1) "ls"
2) "zl"
127.0.0.1:6379> SUNION zs ls
1) "ls"
2) "zl"
3) "ww"
4) "eg"
5) "mz"
127.0.0.1:6379> SISMEMBER zs ls
(integer) 1
127.0.0.1:6379> SISMEMBER li zs
(integer) 0
127.0.0.1:6379> SREM zs ls
(integer) 1
127.0.0.1:6379>
```



### 2.6 SortedSet/Zset

![image-20220501115922171](Redis.assets\image-20220501115922171.png)

![image-20220501115933555](Redis.assets\image-20220501115933555.png)

![image-20220501120258632](Redis.assets\image-20220501120258632.png)

![image-20220501122114475](Redis.assets\image-20220501122114475.png)

```bash
127.0.0.1:6379> ZADD stu 85 Jack 89 Lucy 82 Rose 95 Tom 78 Jerry 92 Amy 76 Miles
(integer) 7
127.0.0.1:6379> ZREM stu Tom
(integer) 1
127.0.0.1:6379> ZSCORE stu Amy
"92"
127.0.0.1:6379> ZRANK stu Rose
(integer) 2
127.0.0.1:6379> ZCOUNT stu 0 80
(integer) 2
127.0.0.1:6379> ZINCRBY stu 2 Amy
127.0.0.1:6379> ZRANGE stu 0 2  REV withscores
1) "Amy"
2) "94"
3) "Lucy"
4) "89"
5) "Jack"
6) "85"
127.0.0.1:6379> ZRANGEBYSCORE stu 0 80 withscores
1) "Miles"
2) "76"
3) "Jerry"
4) "78"
```

## 三、Java客户端

![image-20220501125045748](Redis.assets\image-20220501125045748.png)

### 1. Jedis

#### 1.1 基本使用

![image-20220501125129355](Redis.assets\image-20220501125129355.png)



![image-20220501125230296](Redis.assets\image-20220501125230296.png)



#### 1.2 连接池

![image-20220501130208611](Redis.assets\image-20220501130208611.png)

```java
public final class JedisConnectionPool {
    private static volatile JedisPool jedisPool;
    public static Jedis getJedis(){
        if (jedisPool == null) {
            synchronized (JedisConnectionPool.class) {
                if (jedisPool == null) {
                    JedisPoolConfig config = new JedisPoolConfig();
                    config.setMaxTotal(8);
                    config.setMaxIdle(8);
                    config.setMinIdle(0);
                    config.setMaxWait(Duration.of(200, ChronoUnit.MILLIS));
                    jedisPool = new JedisPool(config,"192.168.10.102",6379,1000);
                    return jedisPool.getResource();
                }
            }
        }
        return jedisPool.getResource();
    }
}

// close方法 如果是连接池制造的Jedis对象 close方法则是归还
public void close() {
        if (this.dataSource != null) {
            JedisPoolAbstract pool = this.dataSource;
            this.dataSource = null;
            if (this.isBroken()) {
                pool.returnBrokenResource(this);
            } else {
                // 归还
                pool.returnResource(this);
            }
        } else {
            super.close();
        }

    }
```

### 2. Lettuce/Spring Data Redis

#### 2.1 初识



![image-20220501132500423](Redis.assets\image-20220501132500423.png)

![image-20220501132713347](Redis.assets\image-20220501132713347.png)

![image-20220501132809922](Redis.assets\image-20220501132809922.png)

![image-20220501132940306](Redis.assets\image-20220501132940306.png)



#### 2.2 序列化

![image-20220501133520609](Redis.assets\image-20220501133520609.png)

![image-20220501133650447](Redis.assets\image-20220501133650447.png)

```java
@Bean
public RedisTemplate<String,Object> redisTemplate(LettuceConnectionFactory factory){
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(factory);

    redisTemplate.setKeySerializer(RedisSerializer.string());
    redisTemplate.setHashKeySerializer(RedisSerializer.string());

    GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
    redisTemplate.setValueSerializer(serializer);
    redisTemplate.setHashValueSerializer(serializer);
    return redisTemplate;
}
```





```java
@Configuration
public class RedisConfigurea {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(LettuceConnectionFactory factory){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        // 设置链接工厂
        redisTemplate.setConnectionFactory(factory);
        // key的序列化
        StringRedisSerializer redisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(redisSerializer);

        // value序列化
        Jackson2JsonRedisSerializer<Object> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        //序列化时将类的数据类型存入json，以便反序列化的时候转换成正确的类型
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,ObjectMapper.DefaultTyping.NON_FINAL);

        // 解决jackson2无法反序列化LocalDateTime的问题
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());

        jsonRedisSerializer.setObjectMapper(objectMapper);

        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashKeySerializer(jsonRedisSerializer);
        return redisTemplate;
    }

}
```

#### 2.3 StringRedisTemplate

![image-20220501134710757](Redis.assets\image-20220501134710757.png)

![image-20220501134800675](Redis.assets\image-20220501134800675.png)



![image-20220501135245356](Redis.assets\image-20220501135245356.png)





## 四 、相应案例

![image-20220501190047444](Redis.assets\image-20220501190047444.png)

![image-20220501190106187](Redis.assets\image-20220501190106187.png)

![image-20220501190126078](Redis.assets\image-20220501190126078.png)



### 1. Session登录



![image-20220501190140023](Redis.assets\image-20220501190140023.png)

![image-20220501190150823](Redis.assets\image-20220501190150823.png)![image-20220501190155546](Redis.assets\image-20220501190155546.png)

![image-20220501190204627](Redis.assets\image-20220501190204627.png)

![image-20220501190213096](Redis.assets\image-20220501190213096.png)![image-20220501190222232](Redis.assets\image-20220501190222232.png)



### 2. Cache/缓存

#### 2.1 什么是缓存



![image-20220501190249575](Redis.assets\image-20220501190249575.png)

![image-20220501190418933](Redis.assets\image-20220501190418933.png)



#### 2.2 添加Redis缓存



![image-20220501190820112](Redis.assets\image-20220501190820112.png)



#### 2.3 缓存更新策略

![image-20220502105710428](Redis.assets\image-20220502105710428.png)

![image-20220502105946731](Redis.assets\image-20220502105946731.png)

![image-20220502110151302](Redis.assets\image-20220502110151302.png)

##### 2.3.1 先删除OR先更新？

![image-20220502110643603](Redis.assets\image-20220502110643603.png)

**优先考虑先库后缓存*

[参考链接](https://blog.csdn.net/weixin_43361136/article/details/118940208)



#### 2.4 缓存穿透

![image-20220502124026216](Redis.assets\image-20220502124026216.png)

![image-20220502124142354](Redis.assets\image-20220502124142354.png)





#### 2.5 缓存雪崩

![image-20220502131533234](Redis.assets\image-20220502131533234.png)

#### 2.6  缓存击穿

![image-20220502131705822](Redis.assets\image-20220502131705822.png)

![image-20220502132917192](Redis.assets\image-20220502132917192.png)

![image-20220502132959525](Redis.assets\image-20220502132959525.png)

![image-20220502133325306](Redis.assets\image-20220502133325306.png)

##### 代码

```java
@Override
public Result queryById(Long id) throws JsonProcessingException {

    String key = CacheConst.SHOP_FIELD + id;
    String shopCache = stringRedisTemplate.opsForValue().get(key);
    if (StrUtil.isNotBlank(shopCache)) {
        Shop shop = JSONUtil.toBean(shopCache, Shop.class);
        log.debug("走缓存 {}", shop);
        return Result.ok(shop);
    }

    String lockKey = CacheConst.CACHE_LOCK_SHOP+ id;
    System.out.println(lockKey);
    int count = 10;
    try {
        // 获取锁失败接着查询缓存看看其他进程有没有更新
        while ( !this.tryLock(lockKey)) {
            if (count == 0) {break;}
            shopCache =  stringRedisTemplate.opsForValue().get(key);
            log.info("获取锁失败  {}",shopCache);
            if (StrUtil.isNotBlank(shopCache)) {
                Shop shop = JSONUtil.toBean(shopCache, Shop.class);
                log.debug("走缓存 {}", shop);
                return Result.ok(shop);
            }
            TimeUnit.MILLISECONDS.sleep(500L);
            count--;
        }
        log.info("获取锁成功");
        Shop shop = getById(id);
        if (shop == null) {
            Shop obj = new Shop();
            obj.setId(id);
            stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(obj),CacheConst.NULL_TTL,CacheConst.NULL_UNIT);
            return Result.fail("店铺不存在");
        }

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop));
        stringRedisTemplate.expire(key, CacheConst.CACHE_TTL, CacheConst.CACHE_TTL_UNIT);
        return Result.ok(shop);
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    } finally {
        this.unlock(lockKey);
    }
}

private boolean tryLock(String lockKey){
    boolean lock = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
    return lock;
}

private boolean unlock(String lockKey){
    boolean unlock = stringRedisTemplate.delete(lockKey);
    return unlock;
}
```



![image-20220502154710677](Redis.assets\image-20220502154710677.png)

```java
String key = CacheConst.SHOP_FIELD + id;
String shopCache = stringRedisTemplate.opsForValue().get(key);
//todo 未命中直接返回
if (StrUtil.isBlank(shopCache)) {
    log.debug("未命中==> {}", shopCache);
    return Result.fail("店铺不存在");
}

RedisData shopData = JSONUtil.toBean(shopCache, RedisData.class);
JSONObject data = (JSONObject) shopData.getData();
Shop shop = JSONUtil.toBean(data, Shop.class);
// todo 命中判断是否过期
if (shopData.getExpire() .isAfter(LocalDateTime.now())) {
    // todo 未过期
    log.debug("未过期 {}", shopData);
    return Result.ok(shop);
}
// todo 过期
String lockKey = CacheConst.CACHE_LOCK_SHOP + id;

if (this.tryLock(lockKey)) {
    // todo 开启独立线程更新缓存刷新过期时间
    System.err.println("刷新");
    config.refreshRedis(lockKey,1000,getById(id),s->{
        this.unlock(lockKey);
        System.err.println("解锁");
    });

}
System.err.println("过期返回");
//todo 未获取锁 返回过期
return Result.ok(data);
```



#### 2.7 缓存封装工具

```java
@Slf4j
@Component
public class CacheClient {
    private final StringRedisTemplate redisTemplate;

    public CacheClient(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void set(String key, Object value, long expireTime, TimeUnit unit) {
        String json = JSONUtil.toJsonStr(value);
        redisTemplate.opsForValue().set(key, json, expireTime, unit);
    }

    // 逻辑过期时间
    public void setWithLogicalExpire(String key, Object value, long expireTime, TimeUnit unit) {
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(expireTime)));
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 防止穿透
     */
    public <R, ID> R getWithPassThrough(String keyPrefix, ID id, Class<R> type, Function<ID, R> callback,
                                        long cacheTime, TimeUnit unit
    ) {
        String key = keyPrefix + id;
        String json = redisTemplate.opsForValue().get(key);
        // todo redis获取缓存
        if (StrUtil.isNotBlank(json)) {
            R bean = JSONUtil.toBean(json, type);
            return bean;
        }
        // todo 判断是否为空值 "" null
        // 理解为序列化不了的 “”
        if (json != null) return null;

        // todo 查询数据库
        R r = callback.apply(id);
        // todo 防止击穿
        if (r == null) {
            redisTemplate.opsForValue().set(key, "", cacheTime, unit);
            return null;
        }
        // todo 存入redis
        this.set(key, r, cacheTime, unit);
        return r;
    }

    /**
     * 防止击穿逻辑锁
     */
    public <R, ID> R getWithLogicalExpire(String keyPrefix, ID id, Class<R> type, String lockKeyPrefix, Function<ID, R> dbcallback) {
        String key = keyPrefix + id;
        String typeCache = redisTemplate.opsForValue().get(key);
        //todo 未命中直接返回
        if (StrUtil.isBlank(typeCache)) {
            log.debug("未命中==> {}", typeCache);
            return null;
        }

        RedisData redisData = JSONUtil.toBean(typeCache, RedisData.class);
        JSONObject data = (JSONObject) redisData.getData();

        R r = JSONUtil.toBean(data, type);
        // todo 命中判断是否过期
        if (redisData.getExpireTime().isAfter(LocalDateTime.now())) {
            // todo 未过期
            log.debug("未过期 {}", redisData);
            return r;
        }
        // todo 过期
        String lockKey = lockKeyPrefix + id;
        // todo 尝试获取锁
        if (this.tryLock(lockKey)) {
            // todo 开启独立线程更新缓存刷新过期时间
            System.err.println("刷新");
            config.refreshRedis(lockKey, 10, dbcallback.apply(id), s -> {
                this.unlock(lockKey);
                System.err.println("解锁");
            });

        }
        System.err.println("过期返回");
        return r;
    }

    /**
     * 缓存击穿互斥锁
     */
    public <R, ID> R getMutuallyExclusive(String keyPrefix, ID id, Class<R> type, String lockKeyPrefix,
                                          Function<ID, R> function,
                                          long cacheTime, TimeUnit cacheUnit
    ) {
        String key = keyPrefix + id;
        String cacheJson = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(cacheJson)) {
            R r = JSONUtil.toBean(cacheJson, type);
            log.debug("走缓存 {}", r);
            return r;
        }

        if (cacheJson != null) return null;

        String lockKey = lockKeyPrefix + id;
        int count = 10;
        try {
            // 获取锁失败接着查询缓存看看其他进程有没有更新
            while (!this.tryLock(lockKey)) {
                if (count == 0) {
                    return null;
                }
                cacheJson = redisTemplate.opsForValue().get(key);
                log.info("获取锁失败  {}", cacheJson);
                if (StrUtil.isNotBlank(cacheJson)) {
                    R r = JSONUtil.toBean(cacheJson, type);
                    log.debug("走缓存 {}", r);
                    return r;
                }
                TimeUnit.MILLISECONDS.sleep(500);
                count--;
            }

            log.info("获取锁成功");
            R r = function.apply(id);
            if (r == null) {
                redisTemplate.opsForValue().set(key, "", cacheTime, cacheUnit);
                return null;
            }
            redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(r), cacheTime, cacheUnit);
            return r;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            log.debug("释放锁");
            this.unlock(lockKey);
        }
    }


    private boolean tryLock(String lockKey) {
        boolean lock = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
        return lock;
    }

    private boolean unlock(String lockKey) {
        boolean unlock = redisTemplate.delete(lockKey);
        return unlock;
    }

    @Autowired
    private MybatisConfig config;
}
```



#### 

###  3 应对秒杀



![image-20220503152728513](Redis.assets\image-20220503152728513.png)



#### 3.1 全局唯一ID

![image-20220503153716608](Redis.assets\image-20220503153716608.png)



![image-20220503153943306](Redis.assets\image-20220503153943306.png)

![image-20220503154127896](Redis.assets\image-20220503154127896.png)

```java
public class RedisIdWorker {
    private static final long START_TIME = LocalDateTime.of(2022, 1, 1, 0, 0, 0).toEpochSecond(ZoneOffset.UTC);
    private static final int COUNT_BITS = 32;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public long nextId(String keyPrefix){
        // 时间戳
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        now = now - START_TIME;
        // 生成序列号
        String key = "icr:" + keyPrefix + ":" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        long count = stringRedisTemplate.opsForValue().increment(key);
        // 拼接并返回
        return now << COUNT_BITS | count;
    }

    public static void main(String[] args) {
        int i = 0 << 32 | 100;
        System.out.println(i);
    }
}
```

![image-20220503164306982](Redis.assets\image-20220503164306982.png)

#### 3.2 秒杀 超卖

![image-20220503182724497](Redis.assets\image-20220503182724497.png)

![image-20220503182920758](https://picgo-images0401.oss-cn-beijing.aliyuncs.com/picgo/image-20220503182920758.png)![image-20220503183923144](Redis.assets\image-20220503183923144.png)

```java
 seckillVoucher.setStock((seckillVoucher.getStock() - 1));
        boolean update = seckillVoucherService.update()
                .setSql("stock = stock -1")
                .eq("voucher_id",voucherId)
                .gt("stock",0)
                .update()
                ;
        if (!update) {
            return Result.fail("优惠卷已售空");
        }
```

```java
@Transactional
    public Result createUserOrderVoucher(Long voucherId, SeckillVoucher seckillVoucher, LocalDateTime now) {
        Long id = UserHolder.getUser().getId();
        // 对id一样的字符串常量池加锁 不影响 this 这把钥匙的使用权
        // 因为使用的是 防止同一个人下单两次
        synchronized (id.toString().intern()) {
            int count = query().eq("user_id", id).eq("voucher_id", voucherId).count();
            if (count > 0) {
                return Result.fail("您只能下一单");
            }

            seckillVoucher.setStock((seckillVoucher.getStock() - 1));
            boolean update = seckillVoucherService.update()
                    .setSql("stock = stock -1")
                    .eq("voucher_id", voucherId)
                    .gt("stock", 0)
                    .update();
            if (!update) {
                return Result.fail("优惠卷已售空");
            }

            VoucherOrder voucherOrder = new VoucherOrder();
            voucherOrder.setId(idWorker.nextId("order"));
            voucherOrder.setUserId(id);
            voucherOrder.setVoucherId(voucherId);
            voucherOrder.setStatus(1);
            voucherOrder.setUpdateTime(now);
            voucherOrder.setCreateTime(now);
            voucherOrder.setPayType(1);
            save(voucherOrder);
            return Result.ok("下单成功！ 请尽快支付！");
        }
    }
```

##### 微服务情况

![image-20220504161726465](Redis.assets\image-20220504161726465.png)



#### 3.3 分布式锁

[redis实现分布式锁相关链接](https://blog.csdn.net/zxd1435513775/article/details/122194202 )

![image-20220504162223941](Redis.assets\image-20220504162223941.png)

![image-20220504162910969](Redis.assets\image-20220504162910969.png)

![image-20220504163245446](Redis.assets\image-20220504163245446.png)

![image-20220504165217480](Redis.assets\image-20220504165217480.png)

```java
SimpleRedisLock redisLock = new SimpleRedisLock("order:"+id, stringRedisTemplate); // 自定义的锁对象实现
boolean isLock = redisLock.tryLock(10, TimeUnit.SECONDS);
if (!isLock) {
    // TODO 注意 这里的锁对象是不同 进程 同一个 用户 的id 不同用户不会产生争抢锁现象 只会判断 where stock > 0
    return Result.fail("不允许重复下单");
}
try {
    int count = query().eq("user_id", id).eq("voucher_id", voucherId).count();
    if (count > 0) {
        return Result.fail("您只能下一单");
    }

	//    ......
    return Result.ok("下单成功！ 请尽快支付！");
    //}
} finally {
    redisLock.unlock();
}
```

##### 有时效性key的失效导致key误删问题

![image-20220504182804786](Redis.assets\image-20220504182804786.png)

```markdown
## 图解：
- 线程1 获取到锁 但是由于业务执行时间过长导致锁Key失效 直接会使抢夺锁的线程2 获取到锁并且生成key
* 于此同时线程1执行业务完成 直接删除线程2 对应的key 导致线程3 在线程2 执行业务时同时获取到了key 
**导致了一系列数据不一致问题**
```

![image-20220504183456398](Redis.assets\image-20220504183456398.png)

> 根据存入的value(Thread.id) 判断释放的线程id是否一致

```java
public class SimpleRedisLock implements ILock {
    private final static String LOCK_PREFIX = "lock:";
    // 每个服务的jvm初始化放在字符串常量池的 中的UUID 作为唯一标识前缀
    private static final String ID_PREFIX = UUID.randomUUID().toString(true);
    private final String name;
    private final StringRedisTemplate stringRedisTemplate;


    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public boolean tryLock(long timeoutSec,TimeUnit unit) {
        String threadId = ID_PREFIX + Thread.currentThread();
        Boolean ownLock = stringRedisTemplate.opsForValue().setIfAbsent(LOCK_PREFIX + name, threadId,timeoutSec,unit);
        return Boolean.TRUE.equals(ownLock);
    }

    @Override
    public void unlock() {
        String threadId = ID_PREFIX + Thread.currentThread();
        String id = stringRedisTemplate.opsForValue().get(LOCK_PREFIX + name);
        // 判断将要执行删除锁的服务线程是否为加锁线程 解决锁误删问题
        if (! threadId.equals(id)) {
            throw new BusinessLock("释放锁异常");
        }
        stringRedisTemplate.delete(LOCK_PREFIX + name);
    }

    class BusinessLock extends RuntimeException {
        private String msg;

        public BusinessLock(String msg) {
            super(msg);
        }

        public BusinessLock(Throwable cause, String msg) {
            super(msg,cause);
        }
    }
}
```

##### 阻塞释放问题

![image-20220504191519782](Redis.assets\image-20220504191519782.png)

```markdown
# 这里是因为线程1 `释放阻塞` 由于前面对比的版本/id是 value 线程1释放是已经判断过的 不会对比value直接删除 `key` 而
# 此时 `key` 中存放的是线程2 的锁value 导致线程3乘虚而入

# 这里 key 一致是要保证分布式多个服务之间互斥 value是防止误删
```

**所以要保证获取锁和释放锁是`原子性操作`**

```java
if (! threadId.equals(id)) {
    throw new BusinessLock("释放锁异常");
}
stringRedisTemplate.delete(LOCK_PREFIX + name);
// 这一段执行 `equals`  `delete` 是组合操作 很难保证原子性
```



##### lua 保证原子性

![image-20220504193007203](Redis.assets\image-20220504193007203.png)

![image-20220504194045886](Redis.assets\image-20220504194045886.png)



```sh
127.0.0.1:6379> EVAL "return redis.call('mset',KEYS[1],ARGV[1],KEYS[2],ARGV[2])" 2 k1 k2 v1 v2

127.0.0.1:6379> EVAL "return redis.call('hset',KEYS[1],AVGS[1],AVGS[2])" h1 name zs
```

```lua
local id = redis.call('GET',KEYS[1])

if(id == ARGV[1]) then
  return redis.call('DEL',KEYS[1])
end

return 0
```

![image-20220504195336329](Redis.assets\image-20220504195336329.png)

![image-20220504195507464](Redis.assets\image-20220504195507464.png)



![image-20220504202508211](Redis.assets\image-20220504202508211.png)

```java
public class SimpleRedisLock implements ILock {

    private final static String LOCK_PREFIX = "lock:";
    // 每个服务的jvm初始化放在字符串常量池的 中的UUID 作为唯一标识前缀
    private static final String ID_PREFIX = UUID.randomUUID().toString(true);
    private final String name;
    private final StringRedisTemplate stringRedisTemplate;
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    static {
        // 加载脚本
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    @Override
    public boolean tryLock(long timeoutSec, TimeUnit unit) {
        String threadId = ID_PREFIX + Thread.currentThread();
        Boolean ownLock = stringRedisTemplate.opsForValue().setIfAbsent(LOCK_PREFIX + name, threadId, timeoutSec, unit);
        return Boolean.TRUE.equals(ownLock);
    }

    @Override
    public void unlock() {
        Long result = stringRedisTemplate
                .execute(
                        UNLOCK_SCRIPT,
                        Collections.singletonList(LOCK_PREFIX + name),
                        ID_PREFIX + Thread.currentThread().getId());
        // todo 缩减为一行代码 优化误删问题 但是归根还是超时问题
        if (result == 0) {
            throw new BusinessLock("释放锁异常");
        }
    }

    /*@Override
    public void unlock() {
        String threadId = ID_PREFIX + Thread.currentThread();
        String id = stringRedisTemplate.opsForValue().get(LOCK_PREFIX + name);
        // 判断将要执行删除锁的服务线程是否为加锁线程 解决锁误删问题
        if (! threadId.equals(id)) {
            throw new BusinessLock("释放锁异常");
        }
        stringRedisTemplate.delete(LOCK_PREFIX + name);
    }*/

    class BusinessLock extends RuntimeException {
        private String msg;

        public BusinessLock(String msg) {
            super(msg);
        }

        public BusinessLock(Throwable cause, String msg) {
            super(msg, cause);
        }
    }
}
```

 相关业务层

```java
@Transactional
public Result createUserOrderVoucher(Long voucherId, SeckillVoucher seckillVoucher, LocalDateTime now) {
    Long id = UserHolder.getUser().getId();
    // 对id一样的字符串常量池加锁 不影响 this 这把钥匙的使用权
    // 因为使用的是 防止同一个人下单两次
    // synchronized (id.toString().intern()) {
    // 只锁该对象的下单重复
    // 多用户的秒杀有 where stock > 0 的原子锁保证
    SimpleRedisLock redisLock = new SimpleRedisLock("order:"+id, stringRedisTemplate);
    boolean isLock = redisLock.tryLock(10, TimeUnit.SECONDS);
    if (!isLock) {
        // TODO 注意 这里的锁对象是不同 进程 同一个 用户 的id 不同用户不会产生争抢锁现象 只会判断 where stock > 0
        return Result.fail("不允许重复下单");
    }
    try {
        int count = query().eq("user_id", id).eq("voucher_id", voucherId).count();
        if (count > 0) {
            return Result.fail("您只能下一单");
        }

        seckillVoucher.setStock((seckillVoucher.getStock() - 1));
        boolean update = seckillVoucherService.update()
            .setSql("stock = stock -1")
            .eq("voucher_id", voucherId)
            .gt("stock", 0)
            .update();
        if (!update) {
            return Result.fail("优惠卷已售空");
        }

        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(idWorker.nextId("order"));
        voucherOrder.setUserId(id);
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setStatus(1);
        voucherOrder.setUpdateTime(now);
        voucherOrder.setCreateTime(now);
        voucherOrder.setPayType(1);
        save(voucherOrder);
        return Result.ok("下单成功！ 请尽快支付！");
        //}
    } finally {
        redisLock.unlock();
    }
}
```



##### Redisson 分布式锁优化

![image-20220504204047241](Redis.assets\image-20220504204047241.png)

![image-20220504204219211](Redis.assets\image-20220504204219211.png)

```http
https://redisson.org/
https://github.com/redisson/redisson
## github 中文
https://github.com/redisson/redisson/wiki/%E7%9B%AE%E5%BD%95
```

###### 依赖于配置

![image-20220504205042959](Redis.assets\image-20220504205042959.png)

```xml
<dependency>
   <groupId>org.redisson</groupId>
   <artifactId>redisson</artifactId>
   <version>3.17.1</version>
</dependency>  
```

![image-20220504205527554](Redis.assets\image-20220504205527554.png)



###### 测试

![image-20220504210650538](Redis.assets\image-20220504210650538.png)

###### 可重入

![image-20220505113503104](Redis.assets\image-20220505113503104.png)

![image-20220505115708179](Redis.assets\image-20220505115708179.png)



![image-20220505113951234](Redis.assets\image-20220505113951234.png)

![image-20220505132238018](Redis.assets\image-20220505132238018.png)

![image-20220505132401366](Redis.assets\image-20220505132401366.png)

###### 主从一致性

![image-20220505193108347](Redis.assets\image-20220505193108347.png)



##### 异步秒杀优化

![image-20220505201856766](Redis.assets\image-20220505201856766.png)

```markdown
- 思路
1. 将库存信息存放至redis中  [size : 100] 用户下单时将库存减一 [decr : 1] 保证库存安全
2. 将购买过思维用户id存入 redis set 集合中 j
```

```lua
-- 如果set集合中不存在就添加 返回一 
if(redis.call('sismember',KEYS[1],ARGV[1]) == 0) then redis.call('sadd',KEYS[1],ARGV[1]) return 1 end return 0
-- 注意 then 后面的冒号在redis EVAL 中不需要 但是时LUA读起来更加规范

-- 判断value的值是否大于0 就减一
local size = redis.call('get',KEYS[1]) if(tonumber(size) > 0) then redis.call('decr',KEYS[1]) return 1 end return 0 
```

![image-20220505205047915](Redis.assets\image-20220505205047915.png)



###### 代码1

```lua
 -- 1.参数列表
 -- 1.1.优惠券id
 local voucherId = ARGV[1]
 -- 1.2.用户id
 local userId = ARGV[2]
 -- 2.数据key
 -- 2.1.库存key
 local stockKey = 'seckill:stock:' .. voucherId
 -- 2.2.订单key
 local orderKey = 'seckill:order:' .. voucherId

 -- 3.脚本业务
 -- 3.1.判断库存是否充足 get stockKey
 if(tonumber(redis.call('get', stockKey)) <= 0) then
     -- 3.2.库存不足，返回1
     return 1
 end
 -- 3.2.判断用户是否下单 SISMEMBER orderKey userId
 if(redis.call('sismember', orderKey, userId) == 1) then
     -- 3.3.存在，说明是重复下单，返回2
     return 2
 end
 -- 3.4.扣库存 incrby stockKey -1
 redis.call('incrby', stockKey, -1)
 -- 3.5.下单（保存用户）sadd orderKey userId
 redis.call('sadd', orderKey, userId)

return 0
```



对应的java业务层

```java
 private final IVoucherService voucherService;

    private final ISeckillVoucherService seckillVoucherService;

    private final RedisIdWorker idWorker;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedissonClient redissonClient;

    private final static DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript();
        SECKILL_SCRIPT.setResultType(Long.class);
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
    }

    private BlockingQueue<VoucherOrder> taskQueue = new LinkedBlockingQueue<>(1024 * 1024);
    private static final ExecutorService SCKILL_SERVICE = Executors.newSingleThreadExecutor();

    // 当前类初始化完毕执行
    @PostConstruct
    private void init() {
        // 提交线程任务
        SCKILL_SERVICE.submit(new VoucherOrderHandler());
    }


    @Override
    public Result seckillVoucher(Long voucherId) {
        Long userId = UserHolder.getUser().getId();

        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(),
                userId.toString()
        );
        int resultValue = result.intValue();

        if (resultValue == 1) {
            return Result.fail("已售空");
        }
        if (resultValue == 2) {
            return Result.fail("您已购买过此卷");
        }
        if (resultValue != 0) {
            return Result.fail("服务器错误");
        }

        // 将订单加入阻塞队列 异步执行
        long order = idWorker.nextId("order");

        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(order);
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setStatus(1);
        voucherOrder.setUpdateTime(LocalDateTime.now());
        voucherOrder.setCreateTime(LocalDateTime.now());
        voucherOrder.setPayType(1);
	    // 将任务加入任务队列
        taskQueue.add(voucherOrder);

        return Result.ok("订单号\t" + order);
    }

    private class VoucherOrderHandler implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    // take 会阻塞
                    VoucherOrder voucherOrder = taskQueue.take();
                    // 创建订单
                    createVoucherOrder(voucherOrder);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log.error("error", e);
                }
            }
        }

        private void createVoucherOrder(VoucherOrder voucherOrder) {
            Long userId = voucherOrder.getUserId();
            Long voucherId = voucherOrder.getVoucherId();
            RLock redisLock = redissonClient.getLock("lock:order" + userId);
            // 注意 这里锁的对象还是用户id解决一人一单问题

            boolean isLock = redisLock.tryLock();
            if (!isLock) {
                // TODO 注意 这里的锁对象是不同 进程 同一个 用户 的id 不同用户不会产生争抢锁现象 只会判断 where stock > 0
                log.error("不允许重复下单");
                return;
            }
            try {
                int count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
                if (count > 0) {
                    log.error("不允许重复下单");
                    return;
                }

                boolean update = seckillVoucherService.update()
                        .setSql("stock = stock -1")
                        .eq("voucher_id", voucherId)
                        .gt("stock", 0)
                        .update();

                if (!update) {
                    log.error("库存不足");
                    return;
                }
                // 保存订单
                save(voucherOrder);
            } finally {
                redisLock.unlock();
            }
        }
    }
```

![image-20220506135734507](Redis.assets\image-20220506135734507.png)





##### 消息队列优化

![image-20220506143536689](Redis.assets\image-20220506143536689.png)

![image-20220506143649593](Redis.assets\image-20220506143649593.png)



###### 1 基于List

![image-20220506144543466](Redis.assets\image-20220506144543466.png)

![image-20220506145313642](Redis.assets\image-20220506145313642.png)

###### 2 PubSub

![image-20220506150453852](Redis.assets\image-20220506150453852.png)

![image-20220506150514775](Redis.assets\image-20220506150514775.png)



###### 3 Stream

![image-20220506151102991](Redis.assets\image-20220506151102991.png)

![image-20220506151517591](Redis.assets\image-20220506151517591.png)

![image-20220506151702602](Redis.assets\image-20220506151702602.png)

![image-20220506151730122](Redis.assets\image-20220506151730122.png)

![image-20220506151950976](Redis.assets\image-20220506151950976.png)

![image-20220506152219993](Redis.assets\image-20220506152219993.png)

![image-20220506152930757](Redis.assets\image-20220506152930757.png)

![image-20220506154520831](Redis.assets\image-20220506154520831.png)

```sh
# 创建一个队列
XADD s1 * k1 v1 k2 v2
# 在这个队列读取消息 从0 开始 4 条
XREAD COUNT 4 BLOCK 2000 STREAMS s1 0
XREAD COUNT 4 BLOCK 0 STREAMS s1 $

# 根据队列创建一个消费者组
XGROUP CREATE s1 wjl 0 
# 读取未被消费过的消息
XREADGROUP GROUP wjl c1 COUNT 10 BLOCK 0 STREAMS s1 >
# 从0开始 读取消费过但是未确认的消息
XREADGROUP GROUP wjl c1 COUNT 10 BLOCK 0 STREAMS s1 0
# 确认消息
XACK  s1 wjl 消息id ..

# s1 队列名 wjl组名  IDEL 空闲时间毫秒 - + id所有范围 10 count c1 指定消费者 每个消费者都有XPENDING
127.0.0.1:6379> XPENDING s1 wjl IDLE 1000  - + 10  c1
```

![image-20220506163939388](Redis.assets\image-20220506163939388.png)

![image-20220506163950281](Redis.assets\image-20220506163950281.png)



##### 代码实现

```sh
-- 手动创建一个队列和消费组
127.0.0.1:6379> XGROUP CREATE stream.orders g1 0  MKSTREAM
OK
XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 0 STREAMS stream.orders >
```



```lua
 -- 1.参数列表
 -- 1.1.优惠券id
 local voucherId = ARGV[1]
 -- 1.2.用户id
 local userId = ARGV[2]
 -- 1.3 订单id
 local orderId = ARGV[3]

 -- 2.数据key
 -- 2.1.库存key
 local stockKey = 'seckill:stock:' .. voucherId
 -- 2.2.订单key
 local orderKey = 'seckill:order:' .. voucherId

 -- 3.脚本业务
 -- 3.1.判断库存是否充足 get stockKey
 if(tonumber(redis.call('get', stockKey)) <= 0) then
     -- 3.2.库存不足，返回1
     return 1
 end
 -- 3.2.判断用户是否下单 SISMEMBER orderKey userId
 if(redis.call('sismember', orderKey, userId) == 1) then
     -- 3.3.存在，说明是重复下单，返回2
     return 2
 end
 -- 3.4.扣库存 incrby stockKey -1
 redis.call('incrby', stockKey, -1)
 -- 3.5.下单（保存用户）sadd orderKey userId
 redis.call('sadd', orderKey, userId)

-- 发送消息到队列中 [stream.orders]
redis.call('xadd','stream.orders','*','userId',userId,'id',voucherId,'orderId',orderId)
return 0

-- redis中声明一个队列和消费组
```



```java
private final IVoucherService voucherService;

    private final ISeckillVoucherService seckillVoucherService;

    private final RedisIdWorker idWorker;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedissonClient redissonClient;

    private final static DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript();
        SECKILL_SCRIPT.setResultType(Long.class);
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
    }

    private static final ExecutorService SCKILL_SERVICE = Executors.newSingleThreadExecutor();

    // 当前类初始化完毕执行
    @PostConstruct
    private void init() {
        // 提交线程任务
        SCKILL_SERVICE.submit(new VoucherOrderHandler());
    }
    private class VoucherOrderHandler implements Runnable {
        @Override
        public void run() {
            String streamQueue = "stream.orders";
            while (true) {
                try {
                    // take 会阻塞
                    // XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS stream.orders >
                    List<MapRecord<String, Object, Object>> mapRecords = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            //  ReadOffset.lastConsumed() == '>'
                            StreamOffset.create(streamQueue, ReadOffset.lastConsumed())
                    );
                    if (mapRecords == null || mapRecords.isEmpty()) {
                        continue;
                    }
                    MapRecord<String, Object, Object> entries = mapRecords.get(0);
                    Map<Object, Object> entriesValue = entries.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(entriesValue, new VoucherOrder(), true);

                    // 消息确认
                    stringRedisTemplate.opsForStream().acknowledge(streamQueue,"g1",entries.getId());
                    // 创建订单
                    createVoucherOrder(voucherOrder);
                } catch (Exception e) {
                    log.error("error", e);
                    handlePendingList();
                }
            }
        }

        private void handlePendingList() {
            String streamQueue = "stream.orders";
            while (true) {
                try {
                    // take 会阻塞
                    // XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS stream.orders 0
                    List<MapRecord<String, Object, Object>> mapRecords = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1),// 处理出异常集合不需要阻塞
                            //  ReadOffset.lastConsumed() == '>'
                            StreamOffset.create(streamQueue, ReadOffset.from("0"))
                    );
                    if (mapRecords == null || mapRecords.isEmpty()) {
                        // 如果为null 说明pending-list没有异常消息 结束循环
                        break;
                    }
                    MapRecord<String, Object, Object> entries = mapRecords.get(0);
                    Map<Object, Object> entriesValue = entries.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(entriesValue, new VoucherOrder(), true);

                    // 消息确认
                    stringRedisTemplate.opsForStream().acknowledge(streamQueue,"g1",entries.getId());
                    // 创建订单
                    createVoucherOrder(voucherOrder);
                } catch (Exception e) {
                    log.error("处理pending-list订单异常", e);
                }
            }
        }

        private void createVoucherOrder(VoucherOrder voucherOrder) {
            Long userId = voucherOrder.getUserId();
            Long voucherId = voucherOrder.getVoucherId();
            RLock redisLock = redissonClient.getLock("lock:order" + userId);
            // 注意 这里锁的对象还是用户id解决一人一单问题

            boolean isLock = redisLock.tryLock();
            if (!isLock) {
                // TODO 注意 这里的锁对象是不同 进程 同一个 用户 的id 不同用户不会产生争抢锁现象 只会判断 where stock > 0
                log.error("不允许重复下单");
                return;
            }
            try {
                int count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
                if (count > 0) {
                    log.error("不允许重复下单");
                    return;
                }

                boolean update = seckillVoucherService.update()
                        .setSql("stock = stock -1")
                        .eq("voucher_id", voucherId)
                        .gt("stock", 0)
                        .update();

                if (!update) {
                    log.error("库存不足");
                    return;
                }
                // 保存订单
                save(voucherOrder);
            } finally {
                redisLock.unlock();
            }
        }
    }

    @Override
    public Result seckillVoucher(Long voucherId) {
        Long userId = UserHolder.getUser().getId();
        // 订单id
        long orderId = idWorker.nextId("order");
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(),
                userId.toString(),
                orderId
        );
        int resultValue = result.intValue();

        if (resultValue == 1) {
            return Result.fail("已售空");
        }
        if (resultValue == 2) {
            return Result.fail("您已购买过此卷");
        }
        if (resultValue != 0) {
            return Result.fail("服务器错误");
        }

        return Result.ok("订单号\t" + orderId);
    }
```



##### 4. Feed流

![image-20220507215237641](Redis.assets\image-20220507215237641.png)

![image-20220507215437956](Redis.assets\image-20220507215437956.png)

![image-20220507215643462](Redis.assets\image-20220507215643462.png)

![image-20220507215812810](Redis.assets\image-20220507215812810.png)

![image-20220507220007540](Redis.assets\image-20220507220007540.png)

![image-20220507220110621](Redis.assets\image-20220507220110621.png)

![image-20220508120220656](Redis.assets\image-20220508120220656.png)

![image-20220508120404676](Redis.assets\image-20220508120404676.png)

```sh
127.0.0.1:6379> ZREVRANGEBYSCORE z1 1000 0 WITHSCORES LIMIT 0 3
根据分数查询z1 最大当前时间 最小时间 显示分数 从0开始查3条
127.0.0.1:6379> ZREVRANGEBYSCORE z1 5 0 WITHSCORES LIMIT 1 3
上次查询最小的是 5 所以最大从5 开始最小值不管 然后 LIMIE 后面找下一个 不包括5 所以为1 查询3条
```

> 根据Zset查询 因为第一次查询没有上一次的最小值 所以为当前时间戳 然后`LIMIT`开始给0
>
> 滚动查询 每一次查询上一次查询的最小值(分数)开始 防止Feed新消息导致消息混乱

但是当查询到的元素有`score`一样的情况 任然会混乱 所以`LIMIT`跳过的应当为`上一次查询的最小值重复的个数`

![image-20220508125416792](Redis.assets\image-20220508125416792.png)

![image-20220508125516147](Redis.assets\image-20220508125516147.png)



###### Controller

```java
@GetMapping("of/follow")
public Result getFeedBlog(
    @RequestParam("lastId") Long max, // 上一次查询的最小值的时间戳
    @RequestParam(value = "offset", defaultValue = "0") Integer offset// 偏移量 第一次没有 默认0
) {
    return followService.queryFeedById(max, offset);
}
```

###### Service

```java
public Result queryFeedById(Long max, Integer offset) {
    // 获取当前用户id
    Long userId = UserHolder.getUser().getId();
    if (userId == null) {
        return Result.fail("请登录！");
    }
    String key = BlogConst.FAN_INBOX + userId;
    // 查询收件箱
    Set<ZSetOperations.TypedTuple<String>> blogIdByScores = stringRedisTemplate.opsForZSet()
        .reverseRangeByScoreWithScores(key, 0, max, offset, 3);
    if (blogIdByScores == null || blogIdByScores.isEmpty()) {
        return Result.ok();
    }
    // 解析数据
    List<Long> ids = new ArrayList<>(blogIdByScores.size());
    long minTime = 0; // 这个Set底层是TreeSet
    int os = 1; // 初始化为1
    for (ZSetOperations.TypedTuple<String> blogIdByScore : blogIdByScores) {
        String idStr = blogIdByScore.getValue();
        long time = blogIdByScore.getScore().longValue(); // 5 4 4 2 2 不置为1的话offset为 4 就错了
        if (time == minTime) {
            os++;
        } else {
            minTime = time;
            os = 1;
        }
        ids.add(Long.valueOf(idStr));
    }
    // 根据id查询blog
    List<Blog> blogs = query()
        .in("id",ids)
        .last("ORDER BY FIELD( id"+StrUtil.join(",",ids)+")")
        .list();
    blogs.forEach(blog -> {
        queryBlogUser(blog); // 查询有关用户
        isBlogLiked(blog); // 查询是否被点赞
    });
    // 封装返回
    ScoreResult<Blog> result = new ScoreResult<>(blogs,minTime,offset);
    return Result.ok(result);
}
```



### 4 附近商铺

#### 4.1 GEO数据类型

![image-20220508142346072](Redis.assets\image-20220508142346072.png)

![image-20220508142700583](Redis.assets\image-20220508142700583.png)

```sh
$ 1 
GEOADD g1 116.378248 39.865275 bjn 116.42803 39.903738 bj 116.322287 39.893729 bjx
$ 2
127.0.0.1:6379> GEODIST g1 bjx bj
"9091.5648"
127.0.0.1:6379> GEODIST g1 bjx bj KM
"9.0916"
$ 3 
127.0.0.1:6379> GEOSEARCH g1 FROMLONLAT 116.397904 39.909005 BYRADIUS 10 km WITHDIST
1) 1) "bj"
   2) "2.6361"
2) 1) "bjn"
   2) "5.1452"
3) 1) "bjx"
   2) "6.6723"
   
GEOSEARCH key [FROMMEMBER member] [FROMLONLAT longitude latitude] [BYRADIUS radius M|KM|FT|MI] [BYBOX width height M|KM|FT|MI] [ASC|DESC] [COUNT count [ANY]] [WITHCOORD] [WITHDIST] [WITHHASH]
  summary: Query a sorted set representing a geospatial index to fetch members inside an area of a box or a circle.
  since: 6.2.0
```

#### 4.2 java

```java
@Test
void shopImportGeo() {
    Map<Long, List<Shop>> listMap = shopService.list().stream().collect(Collectors.groupingBy(Shop::getTypeId));
    for (Map.Entry<Long, List<Shop>> entry : listMap.entrySet()) {
        Long typeId = entry.getKey();
        List<Shop> shopList = entry.getValue();
        List<RedisGeoCommands.GeoLocation<String>> loctions = shopList
                .stream()
                .map(shop ->
                        new RedisGeoCommands.GeoLocation<>(shop.getId().toString(), new Point(shop.getX(), shop.getY())))
                .collect(Collectors.toList());

        redisTemplate.opsForGeo().add(CacheConst.SHOP_GEO_PREFIX + typeId, loctions);
    }
}
```

![image-20220508160505134](Redis.assets\image-20220508160505134.png)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-redis</artifactId>
        </exclusion>
        <exclusion>
            <groupId>io.lettuce</groupId>
            <artifactId>lettuce-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis</artifactId>
    <version>2.6.2</version>
</dependency>
<dependency>
    <groupId>io.lettuce</groupId>
    <artifactId>lettuce-core</artifactId>
    <version>6.1.8.RELEASE</version>
</dependency>
```

```java
@Override
public Result queryShopByType(Integer typeId, Integer current, Double x, Double y) {
    // 1.判断是否需要根据坐标查询
    if (x == null || y == null) {
        // 不需要坐标查询，按数据库查询
        Page<Shop> page = query()
                .eq("type_id", typeId)
                .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
        // 返回数据
        return Result.ok(page.getRecords());
    }

    // 2.计算分页参数
    int from = (current - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
    int end = current * SystemConstants.DEFAULT_PAGE_SIZE;

    // 3.查询redis、按照距离排序、分页。结果：shopId、distance
    String key = SHOP_GEO_KEY + typeId;
    GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo() // GEOSEARCH key BYLONLAT x y BYRADIUS 10 WITHDISTANCE
            .search(
                    key,
                    GeoReference.fromCoordinate(x, y),
                    new Distance(5000),
                   
// RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs() e
        RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end)
            );
    // 4.解析出id
    if (results == null) {
        return Result.ok(Collections.emptyList());
    }
    List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();
    if (list.size() <= from) {
        // 没有下一页了，结束
        return Result.ok(Collections.emptyList());
    }
    // 4.1.截取 from ~ end的部分
    List<Long> ids = new ArrayList<>(list.size());
    Map<String, Distance> distanceMap = new HashMap<>(list.size());
    list.stream().skip(from).forEach(result -> {
        // 4.2.获取店铺id
        String shopIdStr = result.getContent().getName();
        ids.add(Long.valueOf(shopIdStr));
        // 4.3.获取距离
        Distance distance = result.getDistance();
        distanceMap.put(shopIdStr, distance);
    });
    // 5.根据id查询Shop
    String idStr = StrUtil.join(",", ids);
    List<Shop> shops = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
    for (Shop shop : shops) {
        shop.setDistance(distanceMap.get(shop.getId().toString()).getValue());
    }
    // 6.返回
    return Result.ok(shops);
}
```



### 5 BitMap

![image-20220508192021153](Redis.assets\image-20220508192021153.png)

![image-20220508202430521](Redis.assets\image-20220508202430521.png)

```sh
11110000
BITFIELD b1 GET u2 0
# BITFIELD key GET u(无符号)2(获取几位) 0(从0开始)
# 获取到 11 转换10进制就是3
```

#### 5.1 Java

![image-20220508204756585](Redis.assets\image-20220508204756585.png)

#### 5.2 统计签到

![image-20220508213258107](Redis.assets\image-20220508213258107.png)

> 从当前时间判断之前的时间里是否为0 为0表示断签

运算符 https://cloud.tencent.com/developer/article/1338265

```java
Long userId = user.getId();
LocalDate now = LocalDate.now();
String key = CacheConst.getCurrentUserSign(now, userId);
// 返回签到记录 为十进制数字
List<Long> result = redisTemplate.opsForValue().bitField(
        key,
        BitFieldSubCommands
                .create()
                .get(BitFieldSubCommands.BitFieldType.unsigned(now.getDayOfMonth()))
                .valueAt(0)
);
if (result == null || result.isEmpty()) {
    return Result.ok(0);
}
// 循环遍历
Long num = result.get(0);
int count = 0;
if (num == null || num == 0) {
    return Result.ok(0);
}
// 与1做与运算 得到数字最后一个Bit位
while (true) {
    if ((num & 1) == 0) {
        // 为0 直接结束
        break;
    }else {
        // 不为0 计数加一
        count++;
    }
    // 数字右移动
    num >>>= 1;
}
// 判断bit位是否为0 不为0 加1
return Result.ok(count);
```

& 按位与运算 将两个数字的二进制位做对比 都为1 才是一

110111   `55`

111000   `56`

=

110000   `48`		55&56=48

\n

1100011  `99`

  10100  `20`  [不足左边补0]

=

0000000   `0`        99&20=0



| 按位或 有一边为1就为1

1010     `10`

 001	 `1`

=

1011     `11`



5  101

9 1000

=

13 1101

### 6 HyperLogLog

![image-20220509123352606](Redis.assets\image-20220509123352606.png)

![image-20220509123830491](Redis.assets\image-20220509123830491.png)

![image-20220509124652592](Redis.assets\image-20220509124652592.png)





## 五、集群搭建

> 硬件原因 展示略过







## 六、Redis最佳实践



```sh
OBJECT ENCODING key # 查看key编码
MEMORY USAGE key # 查看占用长度
SCAN start(起始下标) # 扫描key
127.0.0.1:6379> CONFIG GET hash-max-ziplist-entries
1) "hash-max-ziplist-entries"
2) "512"


```



### 1. Redis键值设计

![image-20220509125333997](Redis.assets\image-20220509125333997.png)

![image-20220509125823263](Redis.assets\image-20220509125823263.png)

![image-20220509130409921](Redis.assets\image-20220509130409921.png)

![image-20220509130816123](Redis.assets\image-20220509130816123.png)

![image-20220509131606410](Redis.assets\image-20220509131606410.png)

![image-20220509131927307](Redis.assets\image-20220509131927307.png)

![image-20220509132120905](Redis.assets\image-20220509132120905.png)

![image-20220509132628510](Redis.assets\image-20220509132628510.png)





### 2. 批处理优化

#### 1.1 Pipeline

![image-20220509133335471](Redis.assets\image-20220509133335471.png)

![image-20220509133844739](Redis.assets\image-20220509133844739.png)

![image-20220509134050366](Redis.assets\image-20220509134050366.png)

![image-20220509134136971](Redis.assets\image-20220509134136971.png)



#### 1.2 集群环境下的批处理

![image-20220509134701126](Redis.assets\image-20220509134701126.png)



> spring集成的是`异步Slot`



### 3. 服务端优化

#### 1. 持久化

![image-20220509141306608](Redis.assets\image-20220509141306608.png)

![image-20220509141414308](Redis.assets\image-20220509141414308.png)

![image-20220509141538533](Redis.assets\image-20220509141538533.png)

#### 2. 慢查询

![image-20220509141849190](Redis.assets\image-20220509141849190.png)

> 这里是配置内存 永久化配置在配置文件

![image-20220509142112737](Redis.assets\image-20220509142112737.png)

![image-20220509142124861](Redis.assets\image-20220509142124861.png)

#### 3. 命令及安全配置

![image-20220509163825660](Redis.assets\image-20220509163825660.png)

#### 4. 内存配置

![image-20220509164931114](Redis.assets\image-20220509164931114.png)

![image-20220509165056885](Redis.assets\image-20220509165056885.png)

#### 5. 缓冲区配置

![image-20220509170247645](Redis.assets\image-20220509170247645.png)

![image-20220509171717031](Redis.assets\image-20220509171717031.png)

#### 6. 集群可用性

![image-20220509171858767](Redis.assets\image-20220509171858767.png)

![image-20220509172501679](Redis.assets\image-20220509172501679.png)

![image-20220509172747649](Redis.assets\image-20220509172747649.png)



## 七、原理解析



### 1. 数据结构



#### 1.1 动态字符串SDS

![image-20220510184536525](Redis.assets\image-20220510184536525.png)

![image-20220510185104763](Redis.assets\image-20220510185104763.png)

> 为什么预分配？
>
> ​	Linux分成了用户空间和内存空间的，应用程序无法直接操作硬件，需要和Linux内核进行交互，用户态切换到内核态，申请内存
>
> > 学习 Linux 时，经常可以看到两个词：User space（用户空间）和 Kernel space（内核空间）。
> >
> > 简单说，Kernel space 是 Linux 内核的运行空间，User space 是用户程序的运行空间。为了安全，它们是隔离的，即使用户的程序崩溃了，内核也不受影响。
> >
> > 注：虚拟内存被操作系统划分成两块：内核空间和用户空间，内核空间是内核代码运行的地方，用户空间是用户程序代码运行的地方。当进程运行在内核空间时就处于内核态，当进程运行在用户空间时就处于用户态。
> >
> > > 相关🔗 https://www.linuxprobe.com/linux-kernel-user-space.html

![image-20220510190129289](Redis.assets\image-20220510190129289.png)










#### 1.2 InSet

![image-20220510190653304](Redis.assets\image-20220510190653304.png)

![image-20220510190843192](Redis.assets\image-20220510190843192.png)

![image-20220510192606881](Redis.assets\image-20220510192606881.png)

> 问题： 上图的 5 10 20 都可以用一个字节表示 为什么要采用占两个字节的(INTSET_ENC_INT16)?\
>
> > 为了方便inset基于数组角标去寻找， C语言基于指针(8字节大小的整数，映射到内存空间【类似内存地址】)查找

![image-20220510191406888](Redis.assets\image-20220510191406888.png)

`起始地址 + （元素大小 * 角标）`

![image-20220511155218024](Redis.assets\image-20220511155218024.png)




#### 1.3 Dict

![image-20220511190544762](Redis.assets\image-20220511190544762.png)

![image-20220511190853498](Redis.assets\image-20220511190853498.png)

![image-20220511191122449](Redis.assets\image-20220511191122449.png)

![image-20220511192131561](Redis.assets\image-20220511192131561.png)

```c++
using namespace std;
#define Start 1
int main()
{
    for (int i = Start; i <=9 ; ++i) {
        for (int j = 1; j <= i; ++j) {
            cout << "|" << j << "*" << i << "=" << i*j << "|  ";
        }
        cout << endl;
    }
    return 0;
}
```

![image-20220511194032818](Redis.assets\image-20220511194032818.png)

![image-20220511201414583](Redis.assets\image-20220511201414583.png)



![image-20220511210106290](Redis.assets\image-20220511210106290.png)

![image-20220511210316929](Redis.assets\image-20220511210316929.png)

![image-20220511210334648](Redis.assets\image-20220511210334648.png)



#### 1.4 ZipList

![image-20220512123229574](Redis.assets\image-20220512123229574.png)

![image-20220512123448330](Redis.assets\image-20220512123448330.png)

![image-20220512123932625](Redis.assets\image-20220512123932625.png)

![image-20220512124830113](Redis.assets\image-20220512124830113.png)
![image-20220512130138451](Redis.assets\image-20220512130138451.png)

![image-20220512130401751](Redis.assets\image-20220512130401751.png)



#### 1、SDS 动态字符串

##### 优点解析

- 二进制安全

C语言的字符串[0个字节的无符号指针指向的char数组]是根据 `\0`结尾  内容无法容纳`\0`而SDS 是根据 len 长度判断字符串的 而扩容时不包括 `\0` 且C的字符串每次更改都是新的常量池



#### 2、InSet 

**有序/内容倒序扩容**           				   为了扩容(或者说扩容编码长度)防止先放比较小的数据 **伸展**(右边补0)导致后面准备放其他数据的字节位被占满，也就是先将大得数据放置到准备的位置 而扩容前将要添加的数据在扩容(改变编码 )之后添加

注意 判断的数据大小是所占用的比特位 也就是所负数所占用的字节是可以比正数大的，正数插入元素角标位置不变，负数向后移动一位



类似于TreeSet<Number>



根据len 可以根据公式 `startIndex + (index * typesize)`很快找到前后元素

适用于数据量少 	        多了便会慢       数组形式[连续内存空间]

#### 3、Dict

  初始化数组最小是4   size为2^n            sizemask为2^n-![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652348563105-407b1b96-65c3-47e0-bf49-fccf2ffb6025.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652348566954-0b8a6fbc-2e89-4a7f-9562-5a1f093f1709.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652348572464-0c39500b-6a6a-40d2-9bd3-ba846af3be42.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652348577533-71cf5618-9de4-4388-8e29-d421dedfd0d8.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652348581695-e81693d4-5fc7-43ff-b739-f41b9769bc6b.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652348585856-da8b83b6-ebe8-4e21-b3fd-3a32686aebe9.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652348590893-1e5f48b1-e272-46aa-8dac-abf25dac1f89.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652348595849-cc08c6fe-572e-44bb-b25f-9aa3aacb1cfd.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652348598956-dbf5528f-db34-4608-b938-d1f7c5488363.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652348602453-c57202ce-4085-40dd-a859-207512d5b394.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652348617163-9bb6c386-bfbe-4f71-ba83-0e17d1861c72.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652348620055-8bb3f74e-4345-4808-ab40-f72b870db4f2.jpeg)



#### 4. ZipList



Dict不是连续性的 每个entity都是独立的 使用了大量指针 查询快但是内存占用大，可能造成大量的内存碎片



![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652406140842-a2839052-cee7-4ee0-8dbb-179e7aeca26f.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652406142527-68a63b6e-943e-4f44-85d7-595cef07de57.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652406144378-4682352a-7f21-44bd-9cc1-fc474a5c9b6d.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652406146909-ccded0f4-50bc-40be-ba91-296984e7781d.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652406149246-b17c69c2-e815-4249-acf8-bac934122ad2.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652406151561-365f1ac5-efd0-4bc6-849a-5c11c257fe3e.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652406153451-1e801e52-0c4c-4b1b-9086-669c2785b5c7.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652406155582-745b72cf-1d55-4027-9cd4-380126afc376.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652406157527-ab3fe2ba-5671-45c1-82ee-1e22f4c1358d.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652406168466-58d5a9d6-692e-4286-880a-cbea98b21675.jpeg)



类似于链表，节点找中间的节点性能慢，但是舍弃了指针，占用内存空间更小，节点数会做限制，总节点长度 和节点数是小端字节序表示，低位在前



##### 连续锁更新问题

假如说现在有几个连续且节点长度都是250～253的节点，他们记录前一个节点都是用一个字节表示，因为没超过254，这时有一个超过254字节插入到队首，那么之前的头节点表示上一个节点长度的所使用的字节数就要从1变成5 导致本身超过超过254字节数，导致后面几个节点超过阈值随之更新，申请内存空间切换态消耗很大

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652407226560-75de1ce3-aa59-48b2-aef0-ae0c780b15d2.jpeg)

![img](https://cdn.nlark.com/yuque/0/2022/jpeg/23215202/1652407229225-e45a6324-3c25-4212-8baa-deb8b33d6f00.jpeg)

#### 1.5 QuickList

![image-20220513102457942](Redis.assets\image-20220513102457942.png)

![image-20220513102816552](Redis.assets\image-20220513102816552.png)

![image-20220513102956092](Redis.assets\image-20220513102956092.png)

![image-20220513103302838](Redis.assets\image-20220513103302838.png)

![image-20220513103433782](Redis.assets\image-20220513103433782.png)

![image-20220513103556341](Redis.assets\image-20220513103556341.png)

#### 1.6 SkipList

查询效率非常高 可以允许32级指针 理论上说也就是 2^32 个元素

![image-20220513104631498](Redis.assets\image-20220513104631498.png)

![image-20220513105159098](Redis.assets\image-20220513105159098.png)

![image-20220513105330141](Redis.assets\image-20220513105330141.png)

![image-20220513105449786](Redis.assets\image-20220513105449786.png)



#### 1.7 RedisObject

![image-20220513121027275](Redis.assets\image-20220513121027275.png)

![image-20220513121837076](Redis.assets\image-20220513121837076.png)

![image-20220513122500043](Redis.assets\image-20220513122500043.png)

#### 1.8 五种数据结构

##### 1、String

![image-20220513123551082](Redis.assets\image-20220513123551082.png)



如果小于44字节

![image-20220513123851824](Redis.assets\image-20220513123851824.png)



小于44字节所采用的编码是 SDS_TYPE_8  RedisObject和SDS 加在一起刚好64字节  redis底层内存分配的算法 `jemalloc`(https://blog.csdn.net/MOU_IT/article/details/118460045)

会以2^n 内存分配 而64正好是一个内存分片的大小不会产生碎片



> 相关文章 https://blog.csdn.net/weixin_41231928/article/details/120589257
>
> - https://baijiahao.baidu.com/s?id=1703450897541081806&wfr=spider&for=pc

“一个字节有8位,每一位两种状态1或者0计算机储存数据是以二进制的方式,有一位为符号位,所以最大数为01111111转化为十进制数为127。若无符号,最大数为11111111转化为十进制为255。”



![image-20220513125506307](Redis.assets\image-20220513125506307.png)

![image-20220513125519107](Redis.assets\image-20220513125519107.png)

![image-20220513130737790](Redis.assets\image-20220513130737790.png)

![image-20220513130923360](Redis.assets\image-20220513130923360.png)



##### 2、 List

![image-20220513134106381](Redis.assets\image-20220513134106381.png)

![image-20220513134312453](Redis.assets\image-20220513134312453.png)



##### 3、 Set

![image-20220513141143043](Redis.assets\image-20220513141143043.png)

![image-20220513141316067](Redis.assets\image-20220513141316067.png)

![image-20220513142020132](Redis.assets\image-20220513142020132.png)



##### 4、 ZSet

![image-20220513143513689](Redis.assets\image-20220513143513689.png)





![image-20220513144515259](Redis.assets\image-20220513144515259.png)



![image-20220513145834293](Redis.assets\image-20220513145834293.png)

![image-20220513150644329](Redis.assets\image-20220513150644329.png)



![image-20220513150844977](Redis.assets\image-20220513150844977.png)



##### 5、 Hash

![image-20220513151856439](Redis.assets\image-20220513151856439.png)

![image-20220513152114934](Redis.assets\image-20220513152114934.png)

![image-20220513152158875](Redis.assets\image-20220513152158875.png)

![image-20220513153456110](Redis.assets\image-20220513153456110.png)

![image-20220513153511090](Redis.assets\image-20220513153511090.png)

![image-20220513153821467](Redis.assets\image-20220513153821467.png)、、







### 2. 网络模型



##### 1、用户空间和内核空间

![image-20220513154323432](Redis.assets\image-20220513154323432.png)

![image-20220513154645759](Redis.assets\image-20220513154645759.png)

![image-20220513155639758](Redis.assets\image-20220513155639758.png)



##### 2、阻塞IO

![image-20220513160820740](Redis.assets\image-20220513160820740.png)



##### 3、 非阻塞IO

![image-20220513162324690](Redis.assets\image-20220513162324690.png)



##### 4、 IO多路复用

> 相关链接 
>
> - https://zhuanlan.zhihu.com/p/126278747
> - https://blog.csdn.net/shawntime/article/details/115089947
> - https://zhuanlan.zhihu.com/p/272891398

![image-20220513162705604](Redis.assets\image-20220513162705604.png)

![image-20220513162901051](Redis.assets\image-20220513162901051.png)

###### select

![image-20220513164006566](Redis.assets\image-20220513164006566.png)

> https://blog.csdn.net/youyou1543724847/article/details/83445226



###### poll

![image-20220513165024133](Redis.assets\image-20220513165024133.png)



###### epoll

![image-20220513170642623](Redis.assets\image-20220513170642623.png)



![image-20220513191334322](Redis.assets\image-20220513191334322.png)





##### 5、 多路复用通知机制

![image-20220513205633035](Redis.assets\image-20220513205633035.png)



##### 6、IO多路复用web服务流程

![image-20220513210831132](Redis.assets\image-20220513210831132.png)



##### 7、 信号驱动IO 与 异步IO

![image-20220513211122386](Redis.assets\image-20220513211122386.png)

![image-20220513211253679](Redis.assets\image-20220513211253679.png)

![image-20220513211356108](Redis.assets\image-20220513211356108.png)



##### 7、Redis网络模型

![image-20220513211644336](Redis.assets\image-20220513211644336.png)

![image-20220513211946039](Redis.assets\image-20220513211946039.png)



![image-20220513212622022](Redis.assets\image-20220513212622022.png)

![image-20220514104058971](Redis.assets\image-20220514104058971.png)

![image-20220514104752494](Redis.assets\image-20220514104752494.png)

![image-20220514104953463](Redis.assets\image-20220514104953463.png)

![image-20220514105324362](Redis.assets\image-20220514105324362.png)

![image-20220514110208970](Redis.assets\image-20220514110208970.png)

![image-20220514110303562](Redis.assets\image-20220514110303562.png)



![image-20220514111118333](Redis.assets/image-20220514111118333.png)



#### 3. Redis通信协议



##### 1、 RESP协议

![image-20220514123908278](Redis.assets/image-20220514123908278.png)



![image-20220514125320052](Redis.assets/image-20220514125320052.png)

##### 2、 模拟客户端

```java
SocketChannel channel = SocketChannel.open();
ByteBuffer allocate = ByteBuffer.allocate(96);
channel.connect(new InetSocketAddress("localhost",6379));
allocate.put("*3\r\n$3\r\nset\r\n$4\r\nname\r\n$6\r\n张三\r\n".getBytes(StandardCharsets.UTF_8));
allocate.flip();
channel.write(allocate);

allocate.clear();

channel.read(allocate);
allocate.flip();
int limit = allocate.limit();
byte[] bytes = new byte[limit];
for (int i = 0; i < limit; i++) {
    bytes[i] = allocate.get();
}

System.out.println(new String(bytes)); // 输出 [+OK]
```





#### 4、Redis内存策略

##### 1、内存过期

![image-20220514190702800](Redis.assets/image-20220514190702800.png)

![image-20220514191720012](Redis.assets/image-20220514191720012.png)

![image-20220514193115125](Redis.assets/image-20220514193115125.png)



![image-20220514195815617](Redis.assets/image-20220514195815617.png)

![image-20220514200326328](Redis.assets/image-20220514200326328.png)

![image-20220514200455756](Redis.assets/image-20220514200455756.png)



##### 2、内存淘汰

![image-20220514202012332](Redis.assets/image-20220514202012332.png)

![image-20220514210416303](Redis.assets/image-20220514210416303.png)

![image-20220514210455487](Redis.assets/image-20220514210455487.png)







# 配色与插件

![image-20220504220732446](Redis.assets\image-20220504220732446.png)

![image-20220504220753719](Redis.assets\image-20220504220753719.png)



![image-20220505120957835](Redis.assets\image-20220505120957835.png)

![image-20220511161131766](Redis.assets\image-20220511161131766.png)

![image-20220511165407969](Redis.assets\image-20220511165407969.png)

![image-20220511165618113](Redis.assets\image-20220511165618113.png)

