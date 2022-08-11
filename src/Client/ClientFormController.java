package Client;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientFormController {
    public TextField txtClientMessage;
    Socket socket = null;
    String message = "";

    public void initialize() throws IOException {
        new Thread(()->{
            try {
                socket = new Socket("localhost",5000);

            while (true){
                if(!message.equals("exit")){
                    InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String record = bufferedReader.readLine();
                    message = record;
                    System.out.println(record);
                }else{
                    return;
                }
            }} catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void sendOnAction(ActionEvent actionEvent) throws IOException {
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        printWriter.println(txtClientMessage.getText());
        printWriter.flush();
    }
}
