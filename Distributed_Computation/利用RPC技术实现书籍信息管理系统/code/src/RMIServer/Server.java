package RMIServer;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server {

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(8086);
            IBook bookutil = new IBookImpl();
            Naming.bind("rmi://localhost:8086/BookUtil", bookutil);
            System.out.println("Server On!");
        } catch (RemoteException | MalformedURLException | AlreadyBoundException e) {
            e.printStackTrace();
        }
    }

}

