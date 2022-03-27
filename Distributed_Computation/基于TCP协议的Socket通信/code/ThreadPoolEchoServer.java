import java.io.*;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolEchoServer {
    public static void main(String[] args) throws Exception {
        ServerSocket listenSocket = new ServerSocket(8086);  //监听socket
        Socket socket = null;
        System.out.println("listenSocket on");

        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 500, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(3));

        int count = 0;

        while (true) {
            socket = listenSocket.accept();
            System.out.println("The number of the clients is "+ ++count);
            MyTask myTask = new MyTask(socket);
            executor.execute(myTask);
            System.out.println("The number of threads in the ThreadPool:"+executor.getPoolSize());
            System.out.println("The number of tasks in the Queue:"+executor.getQueue().size());
            System.out.println("The number of tasks completed:"+executor.getCompletedTaskCount());
        }
    }
}
