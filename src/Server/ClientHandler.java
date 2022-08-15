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
                broadCastMessage(messageFromClient);


                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                int fileNameLength = dataInputStream.readInt();
                if(fileNameLength>0){
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
                        }catch (IOException e){
                            e.printStackTrace() ;
                        }

                    }
                }

            }catch (IOException e ){
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
