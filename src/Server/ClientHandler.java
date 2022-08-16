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
                System.out.println("run msg "+messageFromClient);
                if(messageFromClient.equalsIgnoreCase("file")){
                    broadCastMessage("file");
                    //broadCastFile();
                }else{
                    broadCastMessage(messageFromClient);
                }

            }catch (IOException e ){
                closeEverything(socket,bufferedWriter,bufferedReader);
                break;
            }
        }
    }


    public void broadCastMessage(String messageToSend){
        System.out.println("server send msg "+messageToSend);
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

    public void broadCastFile(){
        for(ClientHandler client : clientHandlers){
            try{
                if (!client.clientUsername.equals(clientUsername)){
                    client.bufferedWriter.write("file");
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                    DataOutputStream dataOutputStream = new DataOutputStream(client.socket.getOutputStream());
                    DataInputStream dataInputStream = new DataInputStream(client.socket.getInputStream());
                    int fileNameLength = dataInputStream.readInt();
                    if(fileNameLength>0){
                        byte[] fileNameByte = new byte[fileNameLength];
                        dataInputStream.readFully(fileNameByte,0,fileNameByte.length);

                        int fileLength = dataInputStream.readInt();
                        if(fileLength>0){
                            byte[] fileContentByte = new byte[fileLength];
                            dataInputStream.readFully(fileContentByte,0,fileLength);

                            dataOutputStream.writeInt(fileNameLength);
                            dataOutputStream.write(fileNameByte);
                            dataOutputStream.writeInt(fileLength);
                            dataOutputStream.write(fileContentByte);

                        }
                    }
                }
            }catch (IOException e){
                closeEverything(socket,bufferedWriter,bufferedReader);
            }
        }
    }

    public void downloadFile() throws IOException {
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
                }catch (IOException e){
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
