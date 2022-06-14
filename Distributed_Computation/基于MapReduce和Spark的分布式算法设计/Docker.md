# Docker

Docker--轻量级虚拟机；

Docker包含了APP、中间件、独立网络、独立文件系统、OSAPI；

### docker的特性：

隔离性：不同的Docker互不干扰

便携性：可以生成镜像文件，便于迁移；

轻量：镜像小、启动快；

# 启动和关闭实验环境

### 连接虚拟机

1. 启动实验环境：将目录切换到D:\Learn\dc-hadoop\hadoopspark, 然后运行命令：

```powershell
docker-compose up -d
```

![image-20220610100259121](https://cdn.jsdelivr.net/gh/mistletoe1222/img/imgs/202206101003226.png)

```powershell
docker ps
```

![image-20220610100314340](https://cdn.jsdelivr.net/gh/mistletoe1222/img/imgs/202206101003391.png)

2. 用如下命令通过ssh协议从本机（宿主机）远程登陆到hadoopspark_singlenode虚拟机
   内部：

```powershell
ssh -p 2222 root@localhost
```

​	如果有交互式提问一律回答“yes”。登录密码为“123456”。
​	从ssh中退出用“logout”或“exit”命令。

![image-20220610100435287](https://cdn.jsdelivr.net/gh/mistletoe1222/img/imgs/202206101004330.png)

3. 关闭实验环境：将当前目录切换到D:\Learn\dc-hadoop\hadoopspark，然后运行命令：

```powershell
docker-compose down
```

### HDFS

1. 启动实验环境
2. 用ssh登录到hadoopspark_singlenode虚拟机。以下命令都是在ssh登录后运行于hadoopspark_singlenode虚拟机中。
3. 启动HDFS分布式文件系统：运行如下命令启动HDF分布式文件系统

```powershell
start-dfs.sh
```

​	启动成功后，可以利用“jps”命令看到与HDFS系统相关的3个Java进程：分别是namenode进程、datanode进程和namenode的备份节点进程secondary namenode。

4. 特别提醒：ssh登录到hadoopspark_singlenode虚拟机后当前目录为（用pwd命令可以查看）：/root。hadoopspark_singlenode虚拟机的目录/root/share和宿主机的目录：“xxxx\hadoopspark\share”是绑定到一起的，两个目录的内容是完全相同的。因此，你可以利用这两个相互绑定的目录实现宿主机（即你的计算机）和Docker虚拟机之间的数据共享。

5. ssh登录到hadoopspark_singlenode虚拟机后的用户名为root，它默认使用的HDFS路径为根目录。

6. 停止HDFS分布式文件系统：运行如下命令停止HDFS分布式文件系统
   ```powershell
   stop-dfs.sh
   ```

## Spark

### RDD

​	RDD全称为Resilient Distributed Datasets，是Spark 最基本的数据抽象，它是只读的、分区存储的、分布式的数据集合。

​	在Spark平台的支持下，可以对RDD的内部元素进行并行粗粒度操作，操作的具体动作由应用层定义。

​	对比java，python中的数组，对某个元素进行修改等操作是细粒度操作，Spark中只能对RDD整体进行操作，称为粗粒度操作。

​	可以将RDD看作是一个分布式存储的”大数组“，应用程序层面只需关心如何由一个RDD转化为另一个RDD。

### GAD

一个具体的大数据处理任务可以表达为一系列RDD之间的转换。

一个分布式计算任务中涉及到的不同RDD之间存在依赖关系，RDD的每次转换都会生成一个新的依赖关系，这种RDD之间的依赖关系就像流水线一样。RDD(s) 及其之间的依赖关系组成了DAG(有向无环图)。

一个分布式计算任务可以表达为一个DAG；DAG中的节点表示**三类对象**：（1）输入文件；（2）输出文件；（3）RDD；DAG中有向边表示：**RDD转换算子**。

### RDD算子

创建算子

转换（Transform）算子

Action算子

## PS

关于map函数

![image-20220611232036505](https://cdn.jsdelivr.net/gh/mistletoe1222/img/imgs/202206112320557.png)

关于RDD算子

![image-20220612092458355](https://cdn.jsdelivr.net/gh/mistletoe1222/img/imgs/202206120925453.png)

参考

https://github.com/heibaiying/BigData-Notes
