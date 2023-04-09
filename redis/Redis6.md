# Redis6



# 一、NoSQL概述

> NoSQL(NoSQL = **Not Only SQL** )，意即“不仅仅是SQL”，泛指**非关系型的数据库**。 
>
> NoSQL 不依赖业务逻辑方式存储，而以简单的key-value模式存储。因此大大的增加了数据库的扩展能力。

redis

>Ø Redis是一个开源的key-value存储系统。
>
>Ø 和Memcached类似，它支持存储的value类型相对更多，包括string(字符串)、list(链表)、set(集合)、zset(sorted set --有序集合)和hash（哈希类型）。
>
>Ø 这些数据类型都支持push/pop、add/remove及取交集并集和差集及更丰富的操作，而且这些操作都是**原子性**的。
>
>Ø 在此基础上，Redis支持各种不同方式的排序。
>
>Ø 与memcached一样，为了保证效率，数据都是缓存在内存中。
>
>Ø 区别的是Redis会周期性的把更新的数据写入磁盘或者把修改操作写入追加的记录文件。
>
>Ø 并且在此基础上实现了master-slave(主从)同步。



# 二、安装与使用[基本命令]



* 官网下载相应的安装包后，在Linux系统下进行解压 在解压后的目录下执行 `make`和`make install`命令执行编译与安装  >[注意有gcc环境]
* 前台启动 直接执行  `redis-server`
* 后台启动 修改 `redis.conf`文件，将**1.1.1.1.** **daemonize no****改成**yes**   启动方式：`redis-server redis.conf`

相关的配置

> 查看默认安装目录：
>
> redis-benchmark:性能测试工具，可以在自己本子运行，看看自己本子性能如何
>
> redis-check-aof：修复有问题的AOF文件，rdb和aof后面讲
>
> redis-check-dump：修复有问题的dump.rdb文件
>
> redis-sentinel：Redis集群使用
>
> redis-server：Redis服务器启动命令
>
> redis-cli：客户端，操作入口

连接客户端查看是否正常

![image-20220103135258555](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103135258555.png)

命令

* redis-cli -p6379 多个端口查看启动
* redis-cli shutdown 单实例关闭
* 多实例关闭，指定端口关闭：redis-cli -p 6379 shutdown

库的命令

>默认16个数据库，类似数组下标从0开始，初始默认使用0号库
>
>使用命令 select  <dbid>来切换数据库。如: select 8 
>
>统一密码管理，所有库同样密码。
>
>dbsize查看当前数据库的key的数量
>
>flushdb清空当前库
>
>flushall通杀全部库

底层技术

> Redis是单线程+多路IO复用技术
>
> 多路复用是指使用一个线程来检查多个文件描述符（Socket）的就绪状态，比如调用select和poll函数，传入多个文件描述符，如果有一个文件描述符就绪，则返回，否则阻塞直到超时。得到就绪状态后进行真正的操作可以在同一个线程里执行，也可以启动线程执行（比如使用线程池）



# 三、常用五大数据类型



## 1、Redis键（key）

### 常用命令

* `keys *`查看当前数据库所有的key   `keys *1` 
* `exists key名`判断某个key是否存在
* `type key` 查看某个key的数据类型
* `del key` 删除key
* `unlink key` 根据value选择非阻性删除（异步操作）
* `expire key 秒数` 给key设置过期时间
* `ttl key`查看还有多少秒过期，-1表示永不过期，-2表示已过期 
* `select 库名`切换数据库
* `dbsize`查看当前数据库有多少key
* `flushdb` 清空当前库
* `flushall`通杀所有库
* `get key`查看value
* `set key value` 放置一个key并且赋值 如果是原来的值会覆盖
* `append key value` 给原来的值末尾追加value
* `strlen key `获取key的长度
* `setnx key value`当key不存在时才可以创建
* `incr/decr key`针对数字类型 让其value增一/减一
* `incrby/decrby  key 步长`设定加减的值
* `mset key value key value key value .....`同时设置多个key value
* `mget key key key..`同时获取多个value
* `msetnx key value。。。`多个设置其中一个已经存在就失败
* `setex key 过期时间 value`设置一个过期时间的键值对
* `getrange begin end` 截取内容 下标开始为0
* `setrange begin value`从起始下标覆盖
* `getset key value`获取当前key中的值同时覆盖原来的value值，key不存在就返回nil 同时创建
* 

## 2、String

二进制安全的意味着图片视频都可以存，或者序列化的对象 

一个value最多可以是**512mb**

底层是动态字符串

![image-20220103152733240](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103152733240.png)

如图中所示，内部为当前字符串实际分配的空间capacity一般要高于实际字符串长度len。当字符串长度小于1M时，扩容都是加倍现有的空间，如果超过1M，扩容时一次只会多扩1M的空间。需要注意的是字符串最大长度为512M。



## 3、列表 List

单键多值

Redis 列表是简单的字符串列表，按照插入顺序排序。你可以添加一个元素到列表的头部（左边）或者尾部（右边）。

它的底层实际是个双向链表，对两端的操作性能很高，通过索引下标的操作中间的节点性能会较差。

![image-20220103152937483](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103152937483.png)



### 常用命令

* `lpush/rpush key value value value...  `从列表左边/右边插入多个value
* `lrange key begin end`查看列表中的值 下表起始为0 如果end为-1，代表为末尾所有
* `lpush/rpush key 数值`从列表左边/右边吐出指定数值的value【吐出即从列表移除】
* `rpoplpush key1 key2` 从key1右边吐出 插入到key2左边一个
* `lindex key index`指定下标获取key的值 查看不是移除
* `llen key`获取列表的长度
* `linsert key before/after value  value2`在key的value的位置的前/后 插入value2
* `lrem key count value`从左边剔除掉count个指定的value
* `lset key index value`将key中指定下标替换为value



### 数据结构



List的数据结构为快速链表quickList。

首先在列表元素较少的情况下会使用一块连续的内存存储，这个结构是ziplist，也即是压缩列表。

它将所有的元素紧挨着一起存储，分配的是一块连续的内存。

当数据量比较多的时候才会改成quicklist。

因为普通的链表需要的附加指针空间太大，会比较浪费空间。比如这个列表里存的只是int类型的数据，结构上还需要两个额外的指针prev和next。

![image-20220103161418036](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103161418036.png)                               

Redis将链表和ziplist结合起来组成了quicklist。也就是将多个ziplist使用双向指针串起来使用。这样既满足了快速的插入删除性能，又不会出现太大的空间冗余。



## 4、列表set

Redis set对外提供的功能与list类似是一个列表的功能，特殊之处在于set是可以**自动排重**的，当你需要存储一个列表数据，又不希望出现重复数据时，set是一个很好的选择，并且set提供了判断某个成员是否在一个set集合内的重要接口，这个也是list所不能提供的。

Redis的Set是string类型的无序集合。它底层其实是一个value为null的hash表，所以添加，删除，查找的**复杂度都是****O(1)**。

一个算法，随着数据的增加，执行时间的长短，如果是O(1)，数据增加，查找数据的时间不变



### 常用命令

* `sadd key value value .....`添加set集合元素
* `smembers key`取出（查看）key所有的值
* `sismember key value` 判断key是否包含value  返回1【有】0【无】
* `scard key` 查询元素个数
* `srem key value value....`删除key中指定的值
* `spop key`随机吐出（移除）一个
* `spop key count `随机吐（移除）出count个
* `srandmember key n`随机查看key中n个值
* `smove key key1 value`把key1中的value移动到key2
* `sinter key key2`查看两个key相同的元素
* `sunion key key2`查看两个key合在一起的所有元素（相同的会只显示一个）
* `sdiff key key1`查看key中有而key1中没有的

### 数据结构

Set数据结构是dict字典，字典是用哈希表实现的。

Java中HashSet的内部实现使用的是HashMap，只不过所有的value都指向同一个对象。Redis的set结构也是一样，它的内部也使用hash结构，所有的value都指向同一个内部值。



## 5、Hash集合

key、field和value组成



![image-20220103164959691](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103164959691.png)

![image-20220103165134686](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103165134686.png)

![image-20220103165349270](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103165349270.png)

![image-20220103165740526](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103165740526.png)

![image-20220103165846734](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103165846734.png)

![image-20220103165923063](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103165923063.png)

![image-20220103170035424](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103170035424.png)

![image-20220103170149487](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103170149487.png)

![image-20220124213303783](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220124213303783.png)



### 数据结构

Hash类型对应的数据结构是两种：ziplist（压缩列表），hashtable（哈希表）。当field-value长度较短且个数较少时，使用ziplist，否则使用hashtable。



## 6、有序结合Zset

就是有序不重复的set集合 根据【score】排序value



### 常用命令

* `zadd key 1 value1 2 value2....`向Zset集合中添加元素
* `zrange key begin end ` **withscores**  查询指定下标的key集合,如果添加**WITHSCORES** 就是分数和value一起返回
* zrangebyscore key begin end **withscores**    取出这个范围的分数的值
* `zrevrangebyscore key max min withscores ` 倒着排序且显示分数
* `zincrby key 20 value`将value分数上升20
* `zrem key 20 `删除value分数为20的value
*  `zcount key min max`统计分数区间有多少个
* `zrank key value`返回value在集合中的排名 0 为起始下标

### 数据结构

SortedSet(zset)是Redis提供的一个非常特别的数据结构，一方面它等价于Java的数据结构Map<String, Double>，可以给每一个元素value赋予一个权重score，另一方面它又类似于TreeSet，内部的元素会按照权重score进行排序，可以得到每个元素的名次，还可以通过score的范围来获取元素的列表。

zset底层使用了两个数据结构

（1）hash，hash的作用就是关联元素value和权重score，保障元素value的唯一性，可以通过元素value找到相应的score值。

（2）跳跃表，跳跃表的目的在于给元素value排序，根据score的范围获取元素列表。



# 四、其他数据类型

## 1、bitmaps

计算两天都访问过的用户数量

![image-20220103212510184](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103212510184.png)



## 2、HyperLogLog

Redis HyperLogLog 是用来做基数统计的算法，HyperLogLog 的优点是，在输入元素的数量或者体积非常非常大时，计算基数所需的空间总是固定的、并且是很小的。

在 Redis 里面，每个 HyperLogLog 键只需要花费 12 KB 内存，就可以计算接近 2^64 个不同元素的基数。这和计算基数时，元素越多耗费内存就越多的集合形成鲜明对比。

但是，因为 HyperLogLog 只会根据输入元素来计算基数，而不会储存输入元素本身，所以 HyperLogLog 不能像集合那样，返回输入的各个元素。

 

什么是基数?

比如数据集 {1, 3, 5, 7, 5, 7, 8}， 那么这个数据集的基数集为 {1, 3, 5 ,7, 8}, 基数(不重复元素)为5。 基数估计就是在误差可接受的范围内，快速计算基数。



![image-20220103214136794](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103214136794.png)

## 3、 **Geospatial**

Redis 3.2 中增加了对GEO类型的支持。GEO，Geographic，地理信息的缩写。该类型，就是元素的2维坐标，在地图上就是经纬度。redis基于该类型，提供了经纬度设置，查询，范围查询，距离查询，经纬度Hash等常见操作。

![image-20220103214710219](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103214710219.png)

![image-20220103214832443](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103214832443.png)







# 五、Jedis[java]与事务



![image-20220124151642257](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220124151642257.png)

![image-20220124152510945](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220124152510945.png)





![image-20220124152631677](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220124152631677.png)

![image-20220124153021622](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220124153021622.png)









![image-20220103220706672](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220103220706672.png)

注解掉

![image-20220104124707897](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220104124707897.png)

**处于组队时如果一个出问题，全部失效 如果执行时一个出问题其他继续执行**



# 六、乐观锁

> **乐观锁(Optimistic Lock),** 顾名思义，就是很乐观，每次去拿数据的时候都认为别人不会修改，所以不会上锁，但是在更新的时候会判断一下在此期间别人有没有去更新这个数据，可以使用版本号等机制。**乐观锁适用于多读的应用类型，这样可以提高吞吐量**。Redis就是利用这种check-and-set机制实现事务的。

在执行multi之前，先执行watch key1 [key2],可以监视一个(或多个) key ，如果在事务**执行之前这个(****或这些) key** **被其他命令所改动，那么事务将被打断。**



![image-20220104125556489](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220104125556489.png)

`unwacth`放弃监控

```java
package com.wjl.hello;

import java.util.List;
import java.util.UUID;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

public class HelloRedis {
	public static void main(String[] args) {
		
		for (int i = 0; i < 55; i++) {
			new Thread(()->{
				
				for (int j = 0; j < 3; j++) {
					miaoSha(UUID.randomUUID().toString(),"xiaomi");
				}
				
			}).start();
		}
		
	}


	@SuppressWarnings("resource")
	public static Boolean miaoSha(String uid,String proid ) {
		
		//判断用户与商品ID是否为空
		if(uid == null ||proid==null||uid==""||proid==""){
			return false;
		}
		//放置用户ID与商品库存的key
		String uidKey = "yonghu"+"key";
		String proidKey = "xiaomi" +"key";
		
		
		//Jedis jedis = new Jedis("114.55.255.154",6379);
		JedisPool jedispool = JedisUtil.getJedisPool();
		Jedis jedis = jedispool.getResource();
		//监视库存
		jedis.watch(proidKey);
		
		//获取库存是否开始秒杀？
		if(jedis.get(proidKey) == null) {
			System.out.println("秒杀未开始");
			jedis.close();
			return false;
		}
		
		//判断用户是否重复
		Boolean sismember = jedis.sismember(uidKey, uid);
		if(sismember) {
			System.out.println("不可重复获取");
			jedis.close();
			return false;
		}
		
		//判断商品数量是否小于1
		if(Integer.parseInt(jedis.get(proidKey))<=0) {
			System.out.println("秒杀结束");
			jedis.close();
			return false;
		}
		
		//获取事务
		Transaction multi = jedis.multi();
		multi.decr(proidKey);
		multi.sadd(uidKey, uid);
		
		List<Object> exec = multi.exec();
		
		if(exec==null || exec.size() == 0) {
			System.out.println("秒杀失败");
			jedis.close();
			return false;
		}
		//秒杀
		//jedis.sadd(uidKey, uid);
		//jedis.decr(proidKey);
		System.out.println(uid+"秒杀成功！！！");
		jedis.close();
		return true;
	}	
}


package com.wjl.hello;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public  class JedisUtil{
	private JedisUtil() {}
	private static volatile JedisPool jedisPool = null;
	
	public static JedisPool getJedisPool() {
		if(jedisPool==null) {
			synchronized (JedisUtil.class) {
				if(jedisPool==null) {
					
					JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
					jedisPoolConfig.setMaxIdle(32);
					jedisPoolConfig.setMaxTotal(200);
					jedisPoolConfig.setMaxWaitMillis(100*1000);
					jedisPoolConfig.setBlockWhenExhausted(true);
					jedisPoolConfig.setTestOnBorrow(true);
					
					jedisPool = new JedisPool(jedisPoolConfig,"114.55.255.154",6379,60000);
				}
			}
		}
		return jedisPool;
	}
}
```







# 配置文件 

#### 配置文件对大小写不敏感

```properties
# Redis configuration file example.
#
# Note that in order to read the configuration file, Redis must be
# started with the file path as first argument:
#
# ./redis-server /path/to/redis.conf

# Note on units: when memory size is needed, it is possible to specify
# it in the usual form of 1k 5GB 4M and so forth:
#
# 1k => 1000 bytes
# 1kb => 1024 bytes
# 1m => 1000000 bytes
# 1mb => 1024*1024 bytes
# 1g => 1000000000 bytes
# 1gb => 1024*1024*1024 bytes
#
# units are case insensitive so 1GB 1Gb 1gB are all the same.
```

#### 可以包含其他文件

```properties
# Include one or more other config files here.  This is useful if you
# have a standard template that goes to all Redis servers but also need
# to customize a few per-server settings.  Include files can include
# other files, so use this wisely.
#
# Note that option "include" won't be rewritten by command "CONFIG REWRITE"
# from admin or Redis Sentinel. Since Redis always uses the last processed
# line as value of a configuration directive, you'd better put includes
# at the beginning of this file to avoid overwriting config change at runtime.
#
# If instead you are interested in using includes to override configuration
# options, it is better to use include as the last line.
#
# include /path/to/local.conf
# include /path/to/other.conf
```

#### 网络

```properties
################################## NETWORK #####################################
# bind 127.0.0.1 -::1 意思是只能本机访问
bind 0.0.0.0
protected-mode yes # 保护开启
port 6333 # 更改端口

```

#### 通用配置

```properties
################################# GENERAL #####################################
daemonize yes # 是否开启守护进程

# Specify the server verbosity level.
# This can be one of:
# debug (a lot of information, useful for development/testing)
# verbose (many rarely useful info, but not a mess like the debug level)
# notice (moderately verbose, what you want in production probably)
# warning (only very important / critical messages are logged)
loglevel notice  # 日志级别
logfile "" # 日志的文件位置名
always-show-logo no # redis开启LOG
```



#### 快照

```properties
# 在规定的时间内发生多少次持久化
# save ""
#
# Unless specified otherwise, by default Redis will save the DB:
#   * After 3600 seconds (an hour) if at least 1 key changed
#   * After 300 seconds (5 minutes) if at least 100 keys changed
#   * After 60 seconds if at least 10000 keys changed
#
# You can set these explicitly by uncommenting the three following lines.
#
# save 3600 1
# save 300 100
# save 60 10000

# 谷歌翻译
除非另有说明，否则默认情况下 Redis 将保存 DB：
# * 3600 秒（一小时）后，如果至少 1 个键发生了变化
# * 300 秒（5 分钟）后，如果至少有 100 个键被更改
# * 如果至少有 10000 个键被更改，则在 60 秒后
#
# 您可以通过取消注释以下三行来明确设置这些。


# However if you have setup your proper monitoring of the Redis server
# and persistence, you may want to disable this feature so that Redis will
# continue to work as usual even if there are problems with disk,
# permissions, and so forth.
stop-writes-on-bgsave-error yes # 持久化出错是否继续工作

# Compress string objects using LZF when dump .rdb databases?
# By default compression is enabled as it's almost always a win.
# If you want to save some CPU in the saving child set it to 'no' but
# the dataset will likely be bigger if you have compressible values or keys.
rdbcompression yes # 是否压缩持久化文件（.rdb）

# RDB files created with checksum disabled have a checksum of zero that will
# tell the loading code to skip the check.
rdbchecksum yes  # 保存rdb文件是否校验
# Note that you must specify a directory here, not a file name.
dir ./   #rdb文件保存的目录

```



#### 主从复制

```properties
################################# REPLICATION #################################
```



#### 安全

```properties
################################## SECURITY ###################################
requirepass 1128 # 设置密码
```

#### 客户端限制

```properties
################################### CLIENTS ####################################
# maxclients 10000
# 设置最大客户端数量
# maxmemory <bytes> # redis配置最大内存容量
# maxmemory-policy noeviction # 内存满了的处理策略


# LRU 表示最近最少使用，LFU 意味着最少使用
# volatile-lru -> 利用 LRU 算法移除设置过过期时间的 key
# allkeys-lru -> 利用 LRU 算法移除任何 key（常用）
# volatile-lfu -> 利用 LFU 算法移除设置过过期时间的 key
# allkeys-lfu -> 利用 LFU 算法移除任何 key
# volatile-random -> 移除设置过过期时间的随机 key
# allkeys-random -> 移除随机 key
# volatile-ttl -> 移除即将过期的 key(minor TTL)
# noeviction -> 不移除任何 key，只是返回一个写错误（默认）
```

#### AOF

```properties
############################## APPEND ONLY MODE ###############################
appendonly no # 默认不开启aof 默认使用rdb
appendfilename "appendonly.aof" # 持久化文件名

# appendfsync always # 每次修改都会同步
appendfsync everysec # 每秒执行一下
# appendfsync no # 不执行 由系统自己执行
```



# 发布订阅

![image-20220124232743053](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220124232743053.png)

![image-20220124232758941](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220124232758941.png)



# 七、持久化



## 1、RDB

![image-20220124221346657](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220124221346657.png)

````bash
# The filename where to dump the DB
dbfilename dump.rdb  # 
````

自己修改一下

```bash
# save 3600 1
# save 300 100
# save 60 10000
save 60 5 # 当60秒内有五次事务操作就保存
```

测试： 删除掉dump.rdb文件 连接客户端之后执行五次修改操作 直接`shutdown`制造异常

当确认redis进程被杀死 之后重启 还能获得之前的数据就算成功

rdb触发规则

1. save规则满足
2. 执行flushall
3. 退出redis

就会在redis目录生成dump.rdb文件

redis会自动检测目录中的此文件

![image-20220124223513628](C:\Users\还是人物经历\AppData\Roaming\Typora\typora-user-images\image-20220124223513628.png)



![image-20220125142218590](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220125142218590.png)



## 2、AOF

```bash
appendonly no # 默认不开启
```

开启后默认每秒记录

当`.aof文件损坏`时

![image-20220124225458283](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220124225458283.png)

`redis-check-aof --fix appendonly.aof `





# 集群

`info replication`在客户端查看当前角色

![image-20220124234512944](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220124234512944.png)

![image-20220124234955728](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20220124234955728.png)

## 哨兵

```bash
sentinel monitor myredis 127.0.0.1 6333 1
       # 监控     名字    被监控的服务器  票数
redis-sentinel sentienl.conf 

sentinel auth-pass <master-name> <password>
设置连接master和slave时的密码，注意的是sentinel不能分别为master和slave设置不同的密码，因此master和slave的密码应该设置相同。
```

replicaof 127.0.0.1 6222
