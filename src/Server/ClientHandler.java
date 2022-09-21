package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket, String name) {
        try{
            this.socket = socket;
            this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername=name;
            clientHandlers.add(this);
           broadCastMessage(clientUsername+" connected!");
        }catch (IOException e ){
            closeEverything(socket,bufferedWriter,bufferedReader);
        }
    }


    @Override
    public void run() {
        String  messageFromClient;

        while(socket.isConnected()){
            try{
                messageFromClient=bufferedReader.readLine();
                //System.out.println("run msg "+messageFromClient);
                if(messageFromClient.split(":")[0].equals("FILE")){
                    downloadFile(messageFromClient.split(":")[1]);
                    //broadCastFile();
                }else{
                    broadCastMessage(messageFromClient);
                }

            }catch (IOException e ){
                System.out.println(e.getMessage());
                closeEverything(socket,bufferedWriter,bufferedReader);
                break;
            }
        }
    }


    public void broadCastMessage(String messageToSend){
        for(ClientHandler clientHandler : clientHandlers){
            try{
                if (!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch (IOException e){
                closeEverything(socket,bufferedWriter,bufferedReader);
            }
        }
    }

    public void broadCastFile(File file, String name){
        System.out.println(name+" image from");
        for(ClientHandler client : clientHandlers){
            try{
                if (!client.clientUsername.equals(name)){

                    FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
                    DataOutputStream dataOutputStream = new DataOutputStream(client.socket.getOutputStream());
                    PrintWriter printWriter = new PrintWriter(client.socket.getOutputStream());
                    printWriter.println("FILE:"+name);
                    printWriter.flush();

                    String fileName = file.getName();
                    byte[] fileNameBytes = fileName.getBytes();
                    byte[] fileContentBytes = new byte[(int)file.length()];
                    fileInputStream.read(fileContentBytes);

                    dataOutputStream.writeInt(fileNameBytes.length);
                    dataOutputStream.write(fileNameBytes);
                    dataOutputStream.writeInt(fileContentBytes.length);
                    dataOutputStream.write(fileContentBytes);
                    dataOutputStream.flush();
                    System.out.println(client.clientUsername+" to broadcast "+fileName);

                }
            }catch (IOException e){
                System.out.println("upload "+e.getMessage());
                closeEverything(socket,bufferedWriter,bufferedReader);
            }
        }
    }

    public void downloadFile(String name) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        int fileNameLength = dataInputStream.readInt();
        if(fileNameLength>0){
            System.out.println(fileNameLength);
            byte[] fileNameByte = new byte[fileNameLength];
            dataInputStream.readFully(fileNameByte,0,fileNameByte.length);
            String fileName = new String(fileNameByte);
            System.out.println(fileName);

            int fileContentLength = dataInputStream.readInt();
            if(fileContentLength>0){
                byte[] fileContentByte = new byte[fileContentLength];
                dataInputStream.readFully(fileContentByte,0,fileContentLength);

                File fileToDownload = new File(fileName);
                try{
                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                    fileOutputStream.write(fileContentByte);
                    fileOutputStream.close();
                    System.out.println("download");

                    broadCastFile(fileToDownload, name);
                }catch (IOException e){
                    System.out.println("download "+e.getMessage());
                    e.printStackTrace() ;
                }
            }
        }
    }


    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadCastMessage(clientUsername + " left");
    }


    public void closeEverything(Socket socket , BufferedWriter bufferedWriter, BufferedReader bufferedReader){
        removeClientHandler();
        try{
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if (bufferedWriter!=null){
                bufferedWriter.close();
            }
            if(socket!=null){
                socket.close();
            }
        }catch (IOException e ){
            e.printStackTrace();
        }
    }
}
