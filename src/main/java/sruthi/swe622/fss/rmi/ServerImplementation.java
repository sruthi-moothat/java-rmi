package sruthi.swe622.fss.rmi;

import java.io.*;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerImplementation extends UnicastRemoteObject implements FileServer, Serializable {
    Registry reg;
    protected ServerImplementation() throws RemoteException {
    }
    public void start(int portNumber) {
        try {
            reg = LocateRegistry.createRegistry(portNumber);
            reg.bind("FileServer",this);
            System.out.println("Server is ready");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    String absolutePath = Paths.get("").toAbsolutePath().toString();
    public void upload(byte[] data, String pathOnServer,long clientFileLength) throws RemoteException {
        boolean append = false;
        try {
            File file = new File(absolutePath + pathOnServer);

            if(file.exists() && file.length() < clientFileLength) {
                append = true;
            }
            FileOutputStream out = new FileOutputStream(file,append);
            out.write(data);

            out.flush();
            out.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    public byte[] download(String pathOnServer,long clientFileLength) throws RemoteException {
        byte[] data = new byte[1024];

        try {
            RandomAccessFile inFile = new RandomAccessFile(absolutePath + "/" + pathOnServer, "r");
            inFile.seek(clientFileLength);
            inFile.read(data,0,data.length);
            inFile.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return data;

    }

    public boolean mkdir(String pathOnServer) throws RemoteException {
        File newDir = new File(absolutePath + "/" + pathOnServer);

        try {
            if(newDir.exists()) {
                return false;

            }
            else {
                newDir.mkdir();
                return true;
            }

        }catch(Exception e) {
            e.printStackTrace();
            return false;

        }

    }

    public String[] dir(String pathOnServer) throws RemoteException {
        String[] list = null;
        try {
            File file = new File(absolutePath + "/" + pathOnServer);
            list = file.list();

        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;

    }

    public boolean rmdir(String pathOnServer) throws RemoteException {
        File dir = null;
        boolean delete = false;
        try {
            dir = new File(absolutePath + "/" + pathOnServer);
            if(dir.exists()) {
                if(dir.list().length == 0){
                    dir.delete();
                    delete = true;
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return delete;

    }

    public boolean rm(String pathOnServer) throws RemoteException {
        File file = null;
        boolean delete = false;
        try {
            file = new File(absolutePath + "/" + pathOnServer);
            file.delete();
            delete = true;

        }catch(Exception e) {
            e.printStackTrace();
            System.out.println("The path does not exists");
        }
        return delete;
    }

    public void shutdown() throws Exception {
        try{
            reg.unbind("FileServer");
            unexportObject(this, true);
            unexportObject(reg, true);
            System.out.println("The Server is shutdown");
            System.exit(1);
        } catch (Exception e) {
        }
    }

    public long fileSize(String pathOnServer){
        File file = new File(absolutePath + "/" + pathOnServer);
        if(file.exists()) {
            return file.length();
        } else
            return 0;
    }
}
