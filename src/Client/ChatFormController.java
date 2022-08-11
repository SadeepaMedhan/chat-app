package Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Collection;

import static Client.LoginFormController.userName;

public class ChatFormController {
    public TextField txtClientMessage;
    public Label lblUserName;
    public VBox chatListContext;
    Socket socket = null;
    String reply = "";
    FileChooser fileChooser = new FileChooser();

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
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    Text text = new Text(record);
                    TextFlow textFlow = new TextFlow(text);
                    hBox.getChildren().add(textFlow);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            chatListContext.getChildren().add(hBox);
                        }
                    });
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

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        Text text = new Text(reply);
        TextFlow textFlow = new TextFlow(text);
        hBox.getChildren().add(textFlow);
        chatListContext.getChildren().add(hBox);
        txtClientMessage.clear();
    }

    public void closeOnAction(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void uploadImageOnAction(MouseEvent mouseEvent) {
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            System.out.println(file.getAbsolutePath());
            Image image = new Image(getClass().getResourceAsStream(file.getAbsolutePath()));
            ImageView imageView = new ImageView(image);
            chatListContext.getChildren().add(imageView);
        }
    }
}
