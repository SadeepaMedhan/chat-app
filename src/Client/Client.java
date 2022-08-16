package Client;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;

import static Client.LoginFormController.userName;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket, String name) {
        try{
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.println(name);
            printWriter.flush();
        }catch (IOException e ){
            e.printStackTrace();
            System.out.println("error on client");
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void sendMessageToServer(String messageToServer){
        try {
            bufferedWriter.write(messageToServer);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("error sending message to the client");
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendFileToServer(File file, String name){
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.println("FILE:"+name);
            printWriter.flush();

            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String fileName = file.getName();
            byte[] fileNameBytes = fileName.getBytes();
            byte[] fileContentBytes = new byte[(int)file.length()];
            fileInputStream.read(fileContentBytes);

            dataOutputStream.writeInt(fileNameBytes.length);
            dataOutputStream.write(fileNameBytes);
            dataOutputStream.writeInt(fileContentBytes.length);
            dataOutputStream.write(fileContentBytes);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    public void receiveMessageFromServer(VBox vBox) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()) {
                    try {
                        String messageFromClient  = bufferedReader.readLine();
                        if(messageFromClient.split(":")[0].equals("FILE")){
                            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                            int fileNameLength = dataInputStream.readInt();
                            if(fileNameLength>0){
                                System.out.println(fileNameLength);
                                System.out.println(messageFromClient.split(":")[1]);
                                byte[] fileNameByte = new byte[fileNameLength];
                                dataInputStream.readFully(fileNameByte,0,fileNameByte.length);
                                String fileName = new String(fileNameByte);
                                String senderName = messageFromClient.split(":")[1];
                                System.out.println(senderName+" client receive "+ fileName);

                                int fileContentLength = dataInputStream.readInt();
                                if(fileContentLength>0){
                                    byte[] fileContentByte = new byte[fileContentLength];
                                    dataInputStream.readFully(fileContentByte,0,fileContentLength);
                                    File fileToDownload = new File(fileName);
                                    ChatFormController.addImage(fileToDownload,vBox,senderName);

//                                    File fileToDownload = new File(fileName);
//                                    try{
//                                        FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
//                                        fileOutputStream.write(fileContentByte);
//                                        fileOutputStream.close();
//                                        System.out.println("download");
//                                    }catch (IOException e){
//                                        e.printStackTrace() ;
//                                    }

                                }
                            }
                        }else{
                            ChatFormController.addLabel(messageFromClient,vBox);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                        System.out.println("error reading message form client");
                        closeEverything(socket,bufferedReader,bufferedWriter);
                        break;
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
