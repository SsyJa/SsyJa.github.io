import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.util.Random;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;


public class Publisher {
    private final Connection connection;
    private final MessageProducer producer;
    private final Session session;
    public Publisher(String topicName) throws JMSException {
        // 1.创建连接工厂
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    	// 2.创建连接
        connection = factory.createConnection();
        // 3.打开连接
        connection.start();
        // 4.创建会话
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        // 5.创建目标地址
        Topic topic = session.createTopic(topicName);
        // 6.创建生产者
        producer = session.createProducer(topic);
    }
    public void close() throws JMSException {
        if (connection != null) {
            connection.close();
        }
    }
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
                   // 8.发送消息
                   producer.send(bytesMessage);
                   System.out.println("Sent a message!");
                   Thread.sleep(100);
               }
           }  catch (InterruptedException e) {
               e.printStackTrace();
           }
    }
    // 主函数
    public static void main(String[] args) throws JMSException{
        Publisher publisher = new Publisher("RandomData");
        publisher.sendMessage();
        publisher.close();
    }
}
