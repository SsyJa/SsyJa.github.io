# JavaRMI应用

#### 工作原理

​	RMI能让一个Java程序去调用网络中另一台计算机的Java对象的方法，那么调用的效果就像是在本机上调用一样。通俗的讲：A机器上面有一个class，通过远程调用，B机器调用这个class 中的方法。

​	RMI，远程方法调用（Remote Method Invocation）是Enterprise JavaBeans的支柱，是建立分布式Java应用程序的方便途径。RMI是非常容易使用的，但是它非常的强大。

   RMI的基础是接口，RMI构架基于一个重要的原理：定义接口和定义接口的具体实现是分开的。下面我们通过具体的例子，建立一个简单的远程计算服务和使用它的客户程序

#### RMI包含部分

1. 远程服务的接口定义
2. 远程服务接口的具体实现
3. 桩（Stub）和框架（Skeleton）文件
4. 一个运行远程服务的服务器
5. 一个RMI命名服务，它允许客户端去发现这个远程服务
6. 类文件的提供者（一个HTTP或者FTP服务器）
7. 一个需要这个远程服务的客户端程序

#### RMI的用途

​	RMI的用途是为分布式Java应用程序之间的远程通信提供服务，提供分布式服务。

   目前主要应用时封装在各个J2EE项目框架中，例如Spring，EJB（Spring和EJB均封装了RMI技术）

   在Spring中实现RMI：

   ①在服务器端定义服务的接口，定义特定的类实现这些接口；

   ②在服务器端使用org.springframework.remoting.rmi.RmiServiceExporter类来注册服务；

   ③在客户端使用org.springframework.remoting.rmi.RmiProxyFactoryBean来实现远程服务的代理功能；

   ④在客户端定义访问与服务器端服务接口相同的类

#### 局限

仅适用于JVM之间的远程通信，不支持跨语言

#### RMI的参数和返回值

​	当调用远程对象上的方法时，客户机除了可以将原始类型的数据作为参数一外，还可以将对象作为参数来传递，与之相对应的是返回值，可以返回原始类型或对象，这些都是通过Java的对象序列化（serialization）技术来实现的。（换而言之：参数或者返回值如果是对象的话必须实现Serializable接口）

#### RMI的类和接口

![img](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204041812982.jpeg)

(一) **Remote接口：**是一个不定义方法的标记接口

   Public interface Remote{}

   在RMI中，远程接口声明了可以从远程Java虚拟机中调用的方法集。远程接口满足下列要求：

   1、远程接口必须直接或间接扩展Java.rmi.Remote接口，且必须声明为public，除非客户端于远程接口在同一包中

   2、在远程接口中的方法在声明时，除了要抛出与应用程序有关的一场之外，还必须包括RemoteException（或它的超类，IOExcepion或Exception）异常

   3、在远程方法声明中，作为参数或返回值声明的远程对象必须声明为远程接口，而非该接口的实现类。

(二) **RemoteObject抽象类**实现了Remote接口和序列化Serializable接口，它和它的子类提供RMI服务器函数。

(三) **LocateRegistry final()类**用于获得特定主机的引导远程对象注册服务器程序的引用（即创建stub），或者创建能在特定端口接收调用的远程对象注册服务程序。

**服务器端**：向其他客户机提供远程对象服务

   SomeService servcie=……；//远程对象服务

1. **Registry registry=LocateRegisty.getRegistry()；**                                    //Registry是个接口，他继承了Remote，此方法返回本地主机在默认注册表端口 1099 上对远程对象 `Registry` 的引用。
2. **getRegistry(int port)**                                                                                          返回本地主机在指定 port 上对远程对象 Registry 的引用;
3. **getRegistry(String host)**  返回指定 `host` 在默认注册表端口 1099 上对远程对象 `Registry` 的引用;
4. **getRegistry(String host, int port)**                                                                   返回指定的 `host` 和 `port` 上对远程对象 Registry 的引用
5. **registry.bind(“I serve”,service);**                                                                    // bind(String name,Remote obj) 绑定对此注册表中指定 name 的远程引用。name ： 与该远程引用相关的名称 obj ： 对远程对象（通常是一个 stub）的引用
6. **unbind（String name）**移除注册表中指定name的绑定。
7. **rebind（String name,Remote obj）**重新绑定，如果name已存在，但是Remote不一样则替换，如果Remote一样则丢弃现有的绑定
8. **lookup(String name)** 返回注册表中绑定到指定 name 的远程引用，返回Remote
9. **String[] list()**  返回在此注册表中绑定的名称的数组。该数组将包含一个此注册表中调用此方法时绑定的名称快照。

**客户机端**：向服务器提供相应的服务请求。

```
Registry registry=LocateRegisty.getRegistry()；
SomeService servcie=(SomeService)registry.lookup(“I serve”);
Servcie.requestService();
```

(四) **Naming类**和**Registry类**类似。

**客户端：**

   Naming.lookup(String url)

   url 格式如下"rmi://localhost/"+远程对象引用
**服务器端：**
   Registry registry=LocateRegistry.createRegistry(int port);
   Naming.rebind(“service”,service);

(五) **RMISecurityManager类**

   在RMI引用程序中，如果没有设置安全管理器，则只能从本地类路径加载stub和类，这可以确保应用程序不受由远程方法调用所下载的代码侵害

   在从远程主机下载代码之前必须执行以下代码来安装RMISecurityManager:

   System.setSecurityManager（new RMISecurityManager（））；

## 任务

利用RPC技术实现一个书籍信息管理系统，具体要求：
	1.客户端实现用户交互，服务器端实现书籍信息存储和管理。客户端与服务器端利用RPC机制进行协作。中间件任选。
	2.服务器端至少暴露如下RPC接口：
	bool add(Book b) 添加一个书籍对象。
	Book queryByID(intbookID) 查询指定ID号的书籍对象。
	Book ListqueryByName(String name) 按书名查询符合条件的书籍对象列		表，支持模糊查询。
	bool delete((intbookID) 删除指定ID号的书籍对象。

## 设计分析

​	在进行gRPC的学习和使用过程中遇到不少的问题与困难，故本次实验采用较为简单的JavaRMI实现。

​	根据基于Socket通信的相关知识，本次实验主要分为客户端与服务端两个部分；其中Client方面主要内容是：与Server的通信连接以及题目所述四个接口功能的详细调用实现；Server端需要完成：书籍数据结构的实现，四个接口的定义与暴露。

**文件目录**

|-- code
    |-- .idea
    |-- out
    |-- src
        |-- RMIClient
        |   |-- BookUtil.java
        |   |-- Client.java
        |-- RMIServer
            |-- IBook.java
            |-- IBookImpl.java
            |-- MyBook.java
            |-- Server.java

**设计简述**

**RMIServer**

- **Server**用于实现socket通信中server端的主要功能

```java
LocateRegistry.createRegistry(8086);
//此方法返回本地主机在默认注册表端口8086上对远程对象Registry的引用
IBook bookutil = new IBookImpl();
//创建端口引用
Naming.bind("rmi://localhost:8086/BOOKUtil", bookutil);
//将8086端口也就是服务器与端口引用绑定
```

- **MyBook**实现书籍数据结构

```java
public class MyBook implements Serializable {
    private static final long serialVersionUID = 1L;
//JAVA序列化的机制是通过判断类的serialVersionUID来验证的版本一致的
    private static int SUM;
    private int ID;
    private String name;

    public MyBook(String name) {
        this.name = name;
        ID = SUM++;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ID：" + ID + ", name：" + name ;
    }
```

- **IBook**用于暴露端口

```java
public interface IBook extends Remote{

    public boolean add(MyBook b) throws RemoteException;
    public MyBook queryByID(int bookID) throws RemoteException;
    public LinkedList<MyBook> queryByName(String name) throws RemoteException;
    public boolean delete(int bookID) throws RemoteException;

}
```

- **IBookImpl**来进行端口的功能实现

```java
public class IBookImpl extends UnicastRemoteObject implements IBook {
    private static final long serialVersionUID = 1L;
//JAVA序列化的机制是通过判断类的serialVersionUID来验证的版本一致的
    private static LinkedList<MyBook> BookDB = new LinkedList<MyBook>();
    protected IBookImpl() throws RemoteException {
        // TODO 自动生成的构造函数stub
    }
    
//进行接口的功能实现
    public boolean add(MyBook b) throws RemoteException {

        //主函数
    }
    @Override
    public MyBook queryByID(int bookID) throws RemoteException {
       //主函数
    }
    public LinkedList<MyBook> queryByName(String name)  throws RemoteException {
        //主函数
    }
    @Override
    public boolean delete(int bookID)  throws RemoteException {
        // 主函数
    }
}
```

**RMIClient**

- **Client**实现socket通信中的客户端

```java
public class Client {
    public static void main(String[] args) {
        IBook bookutil =(IBook) Naming.lookup("rmi://localhost:8086/BOOKUtil");
        ////Naming.lookup来远程获取8086接口的实例
        BookUtil.setBookutil(bookutil);
        //设置RPC接口
        switch{
                //实现书籍信息系统的视图
        }
}
```

- **BookUtil**实现用户端对Client暴露出的端口调用

## 结果展示

Server：

![image-20220404193817854](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204041938901.png)

Client：

![image-20220404194445949](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204041944993.png)![image-20220404194506837](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204041945872.png)

![image-20220404194531583](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204041945617.png)![image-20220404194610218](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204041946258.png)



## 参考

[java类中serialVersionUID的作用](https://blog.csdn.net/u014750606/article/details/80040130)

[java实现远程访问](https://www.cnblogs.com/shhaoran/archive/2013/02/12/2924445.html)