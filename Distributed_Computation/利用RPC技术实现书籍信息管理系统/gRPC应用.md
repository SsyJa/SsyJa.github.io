# gRPC应用

​	**gRPC** 由 google 开发，是一款语言中立、平台中立、开源的远程过程调用(RPC)系统。

​	在 gRPC 里*客户端*应用可以像调用本地对象一样直接调用另一台不同的机器上*服务端*应用的方法，使得您能够更容易地创建分布式应用和服务。与许多 RPC 系统类似，gRPC 也是基于以下理念：定义一个*服务*，指定其能够被远程调用的方法（包含参数和返回类型）。在服务端实现这个接口，并运行一个 gRPC 服务器来处理客户端调用。在客户端拥有一个*存根*能够像服务端一样的方法。

![img](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204041256530.png)

#### 使用 protocol buffers

​	gRPC 默认使用 *protocol buffers*，这是 Google 开源的一套成熟的结构数据序列化机制（当然也可以使用其他数据格式如 JSON）。正如你将在下方例子里所看到的，你用 *proto files* 创建 gRPC 服务，用 protocol buffers 消息类型来定义方法参数和返回类型。

#### 定义服务

​	一个 RPC 服务通过参数和返回类型来指定可以远程调用的方法。gRPC 通过 [protocol buffers](http://doc.oschina.net/https：//developers.google.com/protocol-buffers/docs/overview) 来实现。

​	我们使用 protocol buffers 接口定义语言来定义服务方法，用 protocol buffer 来定义参数和返回类型。客户端和服务端均使用服务定义生成的接口代码。

​	这里有我们服务定义的例子，在 [helloworld.proto](http://doc.oschina.net/https：//github.com/grpc/grpc-java/tree/master/examples/src/main/proto) 里用 protocol buffers IDL 定义的。`Greeter` 服务有一个方法 `SayHello` ，可以让服务端从远程客户端接收一个包含用户名的 `HelloRequest` 消息后，在一个 `HelloReply` 里发送回一个 `Greeter`。

```protobuf
syntax = "proto3";

option java_package = "io.grpc.examples";

package helloworld;

// The greeter service definition.
service Greeter {
  // Sends a greeting
  rpc SayHello (HelloRequest) returns (HelloReply) {}
}

// The request message containing the user's name.
message HelloRequest {
  string name = 1;
}

// The response message containing the greetings
message HelloReply {
  string message = 1;
}
```

#### 生成 gRPC 代码

​	一旦定义好服务，我们可以使用 protocol buffer 编译器 `protoc` 来生成创建应用所需的特定客户端和服务端的代码 - 你可以生成任意 gRPC 支持的语言的代码，当然 PHP 和 Objective-C 仅支持创建客户端代码。生成的代码同时包括客户端的存根和服务端要实现的抽象接口，均包含 `Greeter` 所定义的方法。

**Java**

  这个例子的构建系统也是 Java gRPC 本身构建的一部分 , 这个例子事先生成的代码在 src/generated/main下。 以下类包含所有我们需要创建这个例子所有的代码：

​	HelloRequest.java， HelloResponse.java和其他文件包含所有 protocol buffer 用来填充、序列化和提取 `HelloRequest` 和 `HelloReply` 消息类型的代码。

GreeterGrpc.java， 包含 (还有其他有用的代码)：

  `Greeter` 服务端需要实现的接口

```java
    public static interface Greeter {
    public void sayHello(Helloworld.HelloRequest request,
        StreamObserver&lt;Helloworld.HelloReply>responseObserver);
        }
```

客户端用来与 `Greeter` 服务端进行对话的 `存根` 类。就像你所看到的，异步存根也实现了 `Greeter` 接口。

```java
  public static class GreeterStub extends      AbstractStub&lt;GreeterStub>
    implements Greeter {
      ...
    }
```

#### 写一个服务器

##### 服务实现

**Java**

  GreeterImpl.java 准确地实现了 `Greeter` 服务所需要的行为。  正如你所见，`GreeterImpl` 类通过实现 `sayHello` 方法，实现了从 [IDL](http://doc.oschina.net/https：//github.com/grpc/grpc-java/tree/master/examples/src/main/proto) 生成的`GreeterGrpc.Greeter` 接口 。

```java
@Override
public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
responseObserver.onNext(reply);
responseObserver.onCompleted();
}
```

  `sayHello` 有两个参数：

- `HelloRequest`，请求。

- `StreamObserver<HelloReply>`： 应答观察者，一个特殊的接口，服务器用应答来调用它。

  为了返回给客户端应答并且完成调用：

1. 用我们的激动人心的消息构建并填充一个在我们接口定义的 `HelloReply` 应答对象。
2. 将 `HelloReply` 返回给客户端，然后表明我们已经完成了对 RPC 的处理。

##### 服务端实现

需要提供一个 gRPC 服务的另一个主要功能是让这个服务实在在网络上可用。

**Java**

  [HelloWorldServer.java](http://doc.oschina.net/https：//github.com/grpc/grpc-java/blob/master/examples/src/main/java/io/grpc/examples/helloworld/HelloWorldServer.java) 提供了以下代码作为 Java 的例子。

```java
/* The port on which the server should run */
private int port = 50051;
private Server server;
private void start() throws Exception {
server = ServerBuilder.forPort(port)
    .addService(GreeterGrpc.bindService(new GreeterImpl()))
    .build()
    .start();
logger.info("Server started, listening on " + port);
Runtime.getRuntime().addShutdownHook(new Thread() {
  @Override
  public void run() {
    // Use stderr here since the logger may has been reset by its JVM shutdown hook.
    System.err.println("*** shutting down gRPC server since JVM is shutting down");
    HelloWorldServer.this.stop();
    System.err.println("*** server shut down");
  }
});
}
```

在这里我们创建了合理的 gRPC 服务器，将我们实现的 `Greeter` 服务绑定到一个端口。然后我们启动服务器：服务器现在已准备好从 `Greeter` 服务客户端接收请求。

#### 写一个客户端

客户端的 gRPC 非常简单。在这一步，我们将用生成的代码写一个简单的客户程序来访问我们在上一节里创建的 `Greeter` 服务器。

##### 连接服务

首先我们看一下我们如何连接 `Greeter` 服务器。我们需要创建一个 gRPC 频道，指定我们要连接的主机名和服务器端口。然后我们用这个频道创建存根实例。

**Java**

```java
private final ManagedChannel channel;
private final GreeterGrpc.GreeterBlockingStub blockingStub;
public HelloWorldClient(String host, int port) {
channel = ManagedChannelBuilder.forAddress(host, port)
    .usePlaintext(true)
    .build();
blockingStub = GreeterGrpc.newBlockingStub(channel);
}
```

  在这个例子里，我们创建了一个阻塞的存根。这意味着 RPC 调用要等待服务器应答，将会返回一个应答或抛出一个异常。 gRPC Java 还可以有其他种类的存根，可以向服务器发出非阻塞的调用，这种情况下应答是异步返回的。

##### 调用 RPC

现在我们可以联系服务并获得一个 greeting ：

1. 我们创建并填充一个 `HelloRequest` 发送给服务。
2. 我们用请求调用存根的 `SayHello()`，如果 RPC 成功，会得到一个填充的 `HelloReply` ，从其中我们可以获得 greeting。

**Java**

```java
HelloRequest req = HelloRequest.newBuilder().setName(name).build();
HelloReply reply = blockingStub.sayHello(req);
```





## 任务

利用RPC技术实现一个书籍信息管理系统，具体要求：

​	1.客户端实现用户交互，服务器端实现书籍信息存储和管理。客户端与服务器端利用RPC机制进行协作。中间件任选。

​	2.服务器端至少暴露如下RPC接口：

`bool add(Book b)` 添加一个书籍对象。
`Book queryByID(intbookID)` 查询指定ID号的书籍对象。
`BookListqueryByName(String name)` 按书名查询符合条件的书籍对象列表，支持模糊查询。
`bool delete((intbookID)` 删除指定ID号的书籍对象。

