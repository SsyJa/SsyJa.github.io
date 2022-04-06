package RMIClient;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import RMIServer.IBook;



public class Client {

    public static void main(String[] args) {

        try {
            IBook bookutil =(IBook) Naming.lookup("rmi://localhost:8086/BookUtil");
            //Naming.lookup来远程获取8086接口的实例
            BookUtil.setBookutil(bookutil);
            System.out.println("Client On!");
            Scanner input = new Scanner(System.in);
            int choose  ;
            while(true) {
                System.out.println("MenuList:");
                System.out.println("1: Add Book"); //书籍ID从0开始
                System.out.println("2: Query By ID");
                System.out.println("3: istQuery By Name");  //未实现模糊查询
                System.out.println("4: Delete By ID"); //删除后此ID闲置
                System.out.println("请输入你所要进行的操作对应序号");
                choose = input.nextInt();
                switch (choose) {
                    case 1:
                        BookUtil.add();
                        break;
                    case 2:
                        BookUtil.queryByID();
                        break;
                    case 3:
                        BookUtil.queryByName();
                        break;
                    case 4:
                        BookUtil.delete();
                        break;
                    default:
                        System.out.println("输入无效");
                        break;
                }

            }
        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}