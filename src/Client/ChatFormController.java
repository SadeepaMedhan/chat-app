package Client;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import static Client.LoginFormController.userName;

public class ChatFormController {
    public TextField txtClientMessage;
    public TextArea txtClientPane;
    public Label lblUserName;
    Socket socket = null;
    String reply = "";

    public void initialize() throws IOException {
        lblUserName.setText(userName);
        txtClientMessage.requestFocus();

        new Thread(()->{
            try {
                socket = new Socket("localhost",5000);

            while (true){
                if(!reply.equals("exit")){
                    InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String record = bufferedReader.readLine();
                    reply = record;
                    //System.out.println(record);
                    txtClientPane.appendText("server : "+record+"\n");
                }else{
                    return;
                }
            }} catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void sendOnAction(ActionEvent actionEvent) throws IOException {
        String reply = txtClientMessage.getText();
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        printWriter.println(reply);
        printWriter.flush();
        txtClientPane.appendText("me : "+reply+"\n");
    }

    public void closeOnAction(MouseEvent mouseEvent) {
        System.exit(0);
    }
}
