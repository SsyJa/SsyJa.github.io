import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Vector;



public class ConsumerStatistic {
    public static void main(String[] args) throws JMSException {
        Connection connection = null;
        try {
            // 1.创建连接工厂
            ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            // 2.创建连接
            connection = factory.createConnection();
            // 3.打开连接
            connection.start();
            // 4.创建会话
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // 5.设置目标地址
            Topic topic = session.createTopic("RandomData");
            // 6.创建消息消费者
            MessageConsumer messageConsumer = session.createConsumer(topic);
            System.out.println("waiting for connection");
            // 7.配置消息监听器
            messageConsumer.setMessageListener(new MessageListener() {
                final Vector<Double> vector = new Vector<>();  // 动态数组
                int count=0;  // 随机数总数
                double max=0;  // 随机数最大值
                double min=0;  // 随机数最小值

                // 将统计结果以消息形式发送出去
                // 创建目标地址
                final Topic topic2 = session.createTopic("Statistic");
                // 创建生产者
                final MessageProducer producer2 = session.createProducer(topic2);

                @Override
                public void onMessage(Message msg){
                    try {
                        if(msg instanceof BytesMessage) {
                            BytesMessage message = (BytesMessage) msg;
                            byte[] b = new byte[1024]; // 最多读1024bytes
                            int len;
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

                                    // 每50个数，将均值、方差、最值打包成一个新消息发送出去
                                    // 1-50 51-100 101-150......
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
                                } else System.out.println("……");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
          //下面这行代码必须有，否则还未接收到消息程序就会退出！
          System.out.println(System.in.read());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert connection != null;
            connection.close();
        }
    }
}
