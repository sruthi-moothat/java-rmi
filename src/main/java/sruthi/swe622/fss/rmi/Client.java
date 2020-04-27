package sruthi.swe622.fss.rmi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public static FileServer getFileServer() throws Exception {
        String env = System.getenv("PA1_SERVER");
        String[] envArray = env.split(":");
        int portNumber = Integer.parseInt(envArray[1]);
        Registry registry = LocateRegistry.getRegistry((envArray[0]), portNumber);
        FileServer stub = (FileServer) registry.lookup("FileServer");
        return stub;
    }

    public static void uploadFile(String pathOnClient, String pathOnserver) throws Exception {
        FileServer stub = getFileServer();
        long serverFileLength = stub.fileSize(pathOnserver);
        RandomAccessFile inFile = new RandomAccessFile(pathOnClient, "r");
        long clientFileLength = inFile.length();
        if (serverFileLength == clientFileLength) {
            serverFileLength = 0;
        } else if (serverFileLength > 0) {
            System.out.println("Resuming upload");
        }
        inFile.seek(serverFileLength);
        int length;
        long percentage = 0;
        byte[] buffer = new byte[1024];
        while ((length = inFile.read(buffer)) > 0) {
            serverFileLength += length;
            stub.upload(buffer, pathOnserver, clientFileLength);
            float v = (float) serverFileLength / clientFileLength;
            percentage = (long) (v * 100);
            System.out.print("\rUploading... " + percentage + "%");

        }
        inFile.close();
        System.out.println();
        System.out.println("The file is successfully uploaded");
    }

    public static void downloadFile(String pathOnServer, String pathOnClient) throws Exception {
        FileServer stub = getFileServer();
        File file = new File(pathOnClient);
        long serverFileSize = stub.fileSize(pathOnServer);
        boolean append = false;
        long percentage;

        long clientFileLength = file.length();
        if(clientFileLength == serverFileSize || clientFileLength == 0) {
            clientFileLength = 0;
        }

        else if(clientFileLength >0) {
            System.out.println("Resuming download");
            append = true;
        }
        FileOutputStream out = new FileOutputStream(file,append);
        while (serverFileSize > clientFileLength) {
            byte[] data = stub.download(pathOnServer,clientFileLength);
            out.write(data);
            clientFileLength += data.length;
            float v = (float)clientFileLength / serverFileSize;
            percentage = (long) (v * 100);
            System.out.print("\rDownloading... " + percentage + "%");

        }

        out.flush();
        out.close();
        System.out.println();
        if(serverFileSize == 0) {
            System.out.println("The path does not exist");
        }
        else if(serverFileSize == clientFileLength) {
            System.out.println("The file is successfully downloaded");
        }

    }

    public static void listDirectory(String pathOnserver) throws Exception {
        FileServer stub = getFileServer();
        String[] list = stub.dir(pathOnserver);
        for (int i = 0; i < list.length; i++) {
            System.out.println(list[i]);
        }
    }

    public static void makeDirectory(String pathOnserver) throws Exception {
        FileServer stub = getFileServer();
        boolean newDir = stub.mkdir(pathOnserver);
        if (newDir) {
            System.out.println("New directory is created");
        } else {
            System.out.println("The directory already exists");
        }


    }

    public static void removeDirectory(String pathOnserver) throws Exception {
        FileServer stub = getFileServer();
        boolean remove = stub.rmdir(pathOnserver);
        if (remove == true) {
            System.out.println("The directory is deleted");
        } else {
            System.out.println("The directory is non empty, cannot be deleted or wrong path");

        }

    }

    public static void removeFile(String pathOnserver) throws Exception {
        FileServer stub = getFileServer();
        boolean remove = stub.rm(pathOnserver);
        if (remove == true) {
            System.out.println("The file is deleted");
        } else {
            System.out.println("The path is wrong");
        }
    }

    public static void shutdown() {
        try{
            FileServer stub = getFileServer();
            stub.shutdown();
        }catch(Exception e) {

        }

    }

}
