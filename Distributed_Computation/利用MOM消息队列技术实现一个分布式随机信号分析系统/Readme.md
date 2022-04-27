## 任务

![image-20220427150838415](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204271508554.png)

## 实验环境

​	JAVA 17 

​	Maven 3.8.5

​	Activemq 5.17.0

## 设计分析

​	本任务采用 ActiveMQ 作为 MOM 中间件，使用消息队列的方式，进行随机信号的生成和分析; 基于示例进行学习和完成。

#### 随机信号产生微服务

​	此微服务与Activemq中间件进行连接，并建立“`RandomData`”Topic，每隔100ms产生一个正态分布的随机数字，并将其发送进入消息队列。

```java
public void sendMessage() throws JMSException{
        // 每隔100ms产生一个正态分布的随机数字
        try{
               for(int i=0; i<500; i++){
                   // 7.创建消息
                   BytesMessage bytesMessage = session.createBytesMessage();
                   Random random = new Random();
                   double x= random.nextGaussian();   // X~N(0,1)
                   // (X-μ)/σ = X‘ => X = X'*σ + μ
                   x  = 1*x+10;  // X~N(10,1)
                   String s = x+"";
                   bytesMessage.writeBytes(s.getBytes());
                   // 发送消息
                   producer.send(bytesMessage);
                   System.out.println("Sent a message!");
                   Thread.sleep(100);
               }
           }  catch (InterruptedException e) {
               e.printStackTrace();
           }
    }
    public static void main(String[] args) throws JMSException{
        Publisher publisher = new Publisher("RandomData");
        publisher.sendMessage();
        publisher.close();
    }
}
```

​	随机数字的生成，采用 java random 模块的 `nextGaussian` 方法，先生成均值为 0，方差为 1 的随机数，再基于此改变均值和方差

#### 随机信号统计分析微服务

​	此微服务需要完成三个任务：计算过去N个数据的方差与均值；计算历史所有数据中的最值；将分析结果打包并通过Activemq发送；

```java
//通过一个动态数组来计算和存储前两项任务的结果
final Vector<Double> vector = new Vector<>();  
```

```java
// 获取过去50个数的均值和方差
int N=50;
if(vector.size()>=N) {
    double sum = 0.0;
    for (int i = vector.size() - 1; i >= vector.size() - N; i--) {
        sum += vector.get(i);
    }
    double mean = sum / N;
    double accumulate = 0.0;
    for (int i = vector.size() - 1; i >= vector.size() - N; i--) {
        accumulate += (vector.get(i) - mean) * (vector.get(i) - mean);
    }
    double std = accumulate / (N - 1);
```

```java
//判断最值
while ((len = message.readBytes(b)) > 0) {
    String str = new String(b, 0, len);
    double d = Double.parseDouble(str);
    if(d>max){  // 判断是否为当前最大值
        max =d;
    }
    if(d<min){  // 判断是否为当前最小值
        min =d;
    }
    count++;
    vector.addElement(d);  // 把随机数添加到vector中
```

​	此微服务与Activemq中间件连接并建立“`statistic`”Topic，来进行打包分析信息的服务。

```java
final Topic topic2 = session.createTopic("statistic");
final MessageProducer producer2 = session.createProducer(topic2);
```

```java
if(vector.size() % 50 == 0){
    // 创建消息
    BytesMessage bytesMessage = session.createBytesMessage();
    String s = (count - N + 1) + "-" + count +
            ": mean:" + mean + " var:" + std +
            "  |currentMax:" + max + " currentMin: " + min +"\n";
    bytesMessage.writeBytes(s.getBytes());
    producer2.send(bytesMessage);
    System.out.println("Sent a statistic message!");
}
```

​	此微服务未具备动态展示分析结果的功能。

​	分析数据将在第三个微服务中存储进一个txt文件供使用者查看。

#### 实时静态数据显示微服务

​	采用`JFreeChart` 完成信号折线图的绘制实现完成数据的可视化；

​	此微服务需要与前两项微服务建立的topic建立连接

```java
Topic topic1 = session.createTopic("RandomData");
Topic topic2 = session.createTopic("Statistic");
```

​	每50个数据更新一次折现图（实现了**伪动态**）

```
if(vector.size()%50==0 && vector.size()>0) {
        // 准备数据集
        DefaultCategoryDataset myDataset = new DefaultCategoryDataset();
        for(int i=vector.size()-50; i<=vector.size()-1;i++) {
        myDataset.addValue((double)(vector.get(i)),
        "randomNumber",
        i+"");
        }
        // 创建主题样式
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        // 设置标题字体
        standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 20));
        // 设置图例的字体
        standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 15));
        // 设置轴向的字体
        standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 15));
        // 应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);
        // 创建JFreeChart-object
        JFreeChart chart = ChartFactory.createLineChart(
        "Random Signal Generation System [number "+(count-49)+"-"+ count + "]",
        "No.",// 横坐标
        "RandomNumber",//纵坐标
        myDataset,//数据集
        PlotOrientation.VERTICAL,
        false, // 显示图例
        true, // 采用标准生成器
        false);// 是否生成超链接
        ChartFrame ChartFrame = new ChartFrame("折线图", chart);
        ChartFrame.pack();
        ChartFrame.setVisible(true);
        }
```

​	将第二个微服务的分析数据存储进一个txt文件中。

```java
public void onMessage(Message msg) {
    try {
        if(msg instanceof BytesMessage) {
            byte[] b = new byte[1024];
            int len;
            BytesMessage message = (BytesMessage) msg;
            while ((len = message.readBytes(b))>0) {
                String str = new String(b, 0, len);
                count++;
                vector.addElement(str);

                // 默认输出到控制台
                // 修改输出方向:输出到log文件中
                System.setOut(new PrintStream(new FileOutputStream("./statisticResult.txt", true)));
                System.out.println(str);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

## 结果展示

首先启动随机信号统计分析微服务与实时静态数据显示微服务，二者都在等待连接处于监听状态

![image-20220427161331621](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204271613665.png)

启动Publisher服务，每发送一次随机信号会提示一次。

![image-20220427161402806](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204271614869.png)

查看Activemq服务

![image-20220427161510572](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204271615617.png)

可以通过RandomData的信息看到一共发送了435个随机信号；

由于均值方差是按N=50计算，所以入Statistic次数为8；

动态折线图（每50个信号更新一次）：

![image-20220427161646848](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204271616916.png)

![image-20220427161749184](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204271617255.png)

分析数据情况：

![image-20220427162443382](https://cdn.jsdelivr.net/gh/mistletoe1222/img@main/imgs/202204271624431.png)

## 总结

1. 未实现动态折线图的目标，仅通过按照每50个信号绘制一幅折线图来实现伪随机
2. 在完成任务的过程中意识到自己对于JAVA语言的掌握十分薄弱，需要通过不断查询和询问同学来解决问题