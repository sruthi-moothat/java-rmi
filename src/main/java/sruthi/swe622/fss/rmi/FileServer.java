package sruthi.swe622.fss.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileServer extends Remote {
    void upload(byte[] mybyte, String pathOnServer, long clientFileLength) throws RemoteException;
    byte[] download(String pathOnServer,long clientFileLength) throws RemoteException;
    boolean mkdir(String pathOnServer) throws RemoteException;
    String[] dir(String pathOnServer) throws RemoteException;
    boolean rmdir(String pathOnServer) throws RemoteException;
    boolean rm(String pathOnServer) throws RemoteException;
    long fileSize(String pathOnServer) throws RemoteException;
    void shutdown() throws Exception;
}
