package RMIServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

public interface IBook extends Remote{

    public boolean add(MyBook b) throws RemoteException;
    public MyBook queryByID(int bookID) throws RemoteException;
    public LinkedList<MyBook> queryByName(String name) throws RemoteException;
    public boolean delete(int bookID) throws RemoteException;
//接口中的方法必须抛出异常RemoteException
}

