package Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

import static Client.LoginFormController.userName;

public class ChatFormController {
    public TextField txtClientMessage;
    public Label lblUserName;
    public VBox chatListContext;
    FileChooser fileChooser = new FileChooser();
    private Client client;

    public void initialize(){
        lblUserName.setText(userName);
        txtClientMessage.requestFocus();

        try {
            client = new Client(new Socket("localhost",5000));
        }catch (IOException e ){
            e.printStackTrace();
        }
        client.receiveMessageFromServer(chatListContext);
    }

    public void sendOnAction(ActionEvent actionEvent) throws IOException {
        String messageToSend = txtClientMessage.getText();
        if (!messageToSend.isEmpty()){
            HBox hBox =  new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT );
            hBox.setPadding(new Insets(5,10,5,10));
            Text text = new Text(messageToSend);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: rgb(3,71,210);"+"-fx-background-radius: 10px");
            textFlow.setPadding(new Insets(5,5,5,10));
            text.setFill(Color.color(1,1,1));
            hBox.getChildren().add(textFlow);
            chatListContext.getChildren().add(hBox);
            client.sendMessageToServer(userName+" : "+messageToSend);
            txtClientMessage.clear();
        }
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
    public static void  addLabel(String messageFromServer,VBox vBox){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,10,5,10));

        Text text = new Text(messageFromServer);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(145,145,158);"+"-fx-background-radius: 10px");
        textFlow.setPadding(new Insets(5,5,5,10));
        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(hBox);
            }
        });
    }
}
