package RMIServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;


//接口的实现
public class IBookImpl extends UnicastRemoteObject implements IBook {
    private static final long serialVersionUID = 1L;

    private static LinkedList<MyBook> BookDB = new LinkedList<MyBook>();

    protected IBookImpl() throws RemoteException {
    }


    public boolean add(MyBook b) throws RemoteException {

        return BookDB.add(b);
    }

    @Override
    public MyBook queryByID(int bookID) throws RemoteException {
        MyBook b = null;
        for(MyBook book: BookDB) {
            if(book.getID()==bookID) {
                b = book;
            }
        }
        return b;
    }

    public LinkedList<MyBook> queryByName(String name)  throws RemoteException {
        LinkedList<MyBook> B = new LinkedList<MyBook>();
        for(MyBook book: BookDB) {
            if(book.getName().equals(name)) {
                B.add(book);
            }
        }
        return B;
    }

    @Override
    public boolean delete(int bookID)  throws RemoteException {
        for(MyBook book: BookDB) {
            if(book.getID()==bookID) {
                return BookDB.remove(book);
            }
        }
        return false;
    }


}