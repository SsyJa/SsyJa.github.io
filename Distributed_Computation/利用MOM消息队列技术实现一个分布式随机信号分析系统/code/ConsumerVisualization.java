import org.apache.activemq.ActiveMQConnectionFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.jms.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Vector;

public class ConsumerVisualization {

    public static void main(String[] args) throws JMSException {
        Connection connection = null;
        try {
            // 1.创建连接工厂
            ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            // 2.创建连接
            connection = factory.createConnection();
            // 3.启动连接
            connection.start();
            // 4.创建会话
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // 5.1 设置目标地址1
            Topic topic1 = session.createTopic("RandomData");
            // 5.2 设置目标地址2
            Topic topic2 = session.createTopic("Statistic");
            // 6.1 创建消息消费者1
            MessageConsumer messageConsumer1 = session.createConsumer(topic1);
            System.out.println("waiting for connection");
            // 7.1配置消息监听器1 订阅的主题是”RandomData“即就是原始数据
            messageConsumer1.setMessageListener(new MessageListener() {
                int count=0;
                final Vector<Double> vector = new Vector<>();  // 动态数组
                @Override
                public void onMessage(Message msg) {
                    try {
                        if(msg instanceof BytesMessage) {
                            byte[] b = new byte[1024];
                            int len;
                            BytesMessage message = (BytesMessage) msg;
                            while ((len = message.readBytes(b))>0) {
                                String str = new String(b, 0, len);
                                double d = Double.parseDouble(str);
                                count++;
                                vector.addElement(d);

                                // 每50个数据更新一次折线图
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
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            // 6.2 创建消息消费者2
            MessageConsumer messageConsumer2 = session.createConsumer(topic2);
            // 7.2.配置消息监听器2 订阅的主题是”Statistic“即就是实时统计分析的结果
            System.out.println("waiting for connection");
            messageConsumer2.setMessageListener(new MessageListener() {
                int count=0;
                final Vector<String> vector = new Vector<>();  // 动态数组
                @Override
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
            });
            // 防止还没有接受到消息就退出程序!
            System.out.println(System.in.read());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert connection != null;
            connection.close();
        }
    }
}

// 每50个数据更新一次折现图


