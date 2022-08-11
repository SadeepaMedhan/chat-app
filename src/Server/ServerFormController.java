package Server;

import javafx.event.ActionEvent;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFormController {
    public TextField txtServerMessage;
    public TextArea txtServerPane;
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
                        //System.out.println(record);
                        Text text = new Text(record);
                        text.prefWidth(txtServerPane.getWidth());
                        text.setTextAlignment(TextAlignment.LEFT);
                        txtServerPane.appendText("client : "+text.getText()+"\n");
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
        String myReply = txtServerMessage.getText();

        PrintWriter printWriter = new PrintWriter(accept.getOutputStream());
        printWriter.println(myReply);
        printWriter.flush();
        Text text = new Text(myReply);
        text.prefWidth(txtServerPane.getWidth());
        text.setTextAlignment(TextAlignment.RIGHT);
        txtServerPane.appendText(text.getText()+"\n");
    }
}
