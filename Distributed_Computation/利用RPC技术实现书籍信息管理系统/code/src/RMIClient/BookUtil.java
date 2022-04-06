package RMIClient;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Scanner;

import RMIServer.MyBook;
import RMIServer.IBook;

public class BookUtil{

    private static IBook bookutil;
    private static Scanner input = new Scanner(System.in);

    //bool add(Book b) 添加一个书籍对象。
    public static void add() {
        System.out.println("请输入添加书籍名称：");
        String name = input.nextLine();
        MyBook book = new MyBook(name);
        boolean b = false;
        try {
            b =  bookutil.add(book);
        } catch (RemoteException e) {
            e.printStackTrace();
        }finally {
            if(!b) {
                System.out.println("add abortively");
            }
            else {
                System.out.println("add successfully");
            }
        }

    }

    //Book queryByID(intbookID) 查询指定ID号的书籍对象。
    public static void queryByID() {
        System.out.println("请输入欲查询的ID");
        int id = input.nextInt();
        MyBook book = null;
        try {
            book = bookutil.queryByID(id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(book==null) {
            System.out.println("查无此书");
        }
        else {
            System.out.println("此书籍详细信息：");
            System.out.println(book.toString());
        }
    }
    //Book ListqueryByName(String name) 按书名查询符合条件的书籍对象列表，不支持模糊查询。
    public static void queryByName() {
        System.out.println("请输入欲查询书籍的名称：");
        String name = input.nextLine();
        LinkedList<MyBook> booklist = new	LinkedList<MyBook>();
        try {
            booklist = bookutil.queryByName(name);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(booklist.size()==0) {
            System.out.println("查无此书");
        }else {
            System.out.println("此书籍详细信息：");
            for(MyBook book:booklist) {
                System.out.println(book.toString());
            }
        }

    }
    //bool delete((intbookID) 删除指定ID号的书籍对象。
    public static void delete() {
        System.out.println("请输入待删除书籍的ID");
        int id = input.nextInt();
        boolean b = false;
        try {
            b = bookutil.delete(id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }finally {
            if(b==false) {
                System.out.println("删除失败");
            }else {
                System.out.println("删除成功");
            }
        }
    }

    public static void setBookutil(IBook bookutil) {
        BookUtil.bookutil = bookutil;
    }
}