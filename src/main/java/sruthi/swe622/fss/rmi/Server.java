package sruthi.swe622.fss.rmi;

import java.rmi.RemoteException;

public class Server extends ServerImplementation{
    protected Server() throws RemoteException {
    }

    public void start(int portNumber)  {
        try{
            ServerImplementation obj = new ServerImplementation();
            int port = portNumber;
            obj.start(port);

        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
