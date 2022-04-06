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

![image-20220404194445949](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204062140123.png)![image-20220404194506837](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204062140116.png)

![image-20220404194531583](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204062140120.png)![image-20220404194610218](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204062140921.png)



## 参考

[java类中serialVersionUID的作用](https://blog.csdn.net/u014750606/article/details/80040130)

[java实现远程访问](https://www.cnblogs.com/shhaoran/archive/2013/02/12/2924445.html)