package Server;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFormController {
    public TextField txtServerMessage;
    String message = "";
    Socket accept=null;
    public void initialize(){
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(5000);
                System.out.println("server start..");
                accept = serverSocket.accept();
                System.out.println("client connected!");

                while (true){
                    if(!message.equals("exit")){
                        InputStreamReader inputStreamReader = new InputStreamReader(accept.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String record = bufferedReader.readLine();
                        message = record;
                        System.out.println(record);
                    }else{
                        return;
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    public void sendOnAction(ActionEvent actionEvent) throws IOException {
        PrintWriter printWriter = new PrintWriter(accept.getOutputStream());
        printWriter.println(txtServerMessage.getText());
        printWriter.flush();
    }
}
