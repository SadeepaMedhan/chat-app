package Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
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
import static Client.LoginFormController.host;

public class ChatFormController {
    public TextField txtClientMessage;
    public Label lblUserName;
    public VBox chatListContext;
    private Client client;

    public void initialize() throws IOException {
        lblUserName.setText(userName);
        txtClientMessage.requestFocus();
        client = new Client(new Socket(host,5000),userName);
        client.receiveMessageFromServer(chatListContext);

//        FileOutputStream fileOutputStream = new FileOutputStream("src/User.ser");
//        ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
//        User user = new User(userName,host,"5000");
//        oos.writeObject(user);
    }

    public void sendOnAction(ActionEvent actionEvent) {
        String messageToSend = txtClientMessage.getText();
        if (!messageToSend.isEmpty()){
            HBox hBox =  new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT );
            hBox.setPadding(new Insets(5,10,5,10));
            Text text = new Text(messageToSend);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: #7E308E;"+"-fx-background-radius: 10px");
            textFlow.setPadding(new Insets(5,10,5,10));
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
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {

            HBox hBox =  new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT );
            hBox.setPadding(new Insets(5,10,5,10));
            Image image = new Image("file:"+file.getAbsolutePath());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            hBox.getChildren().add(imageView);
            chatListContext.getChildren().add(hBox);
            client.sendFileToServer(file, userName);
            System.out.println("send "+file.getName());
        }
    }

    public static void  addLabel(String messageFromServer,VBox vBox){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,10,5,10));

        Text text = new Text(messageFromServer);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(198,194,194);"+"-fx-background-radius: 10px");
        textFlow.setPadding(new Insets(5,10,5,10));
        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(hBox);
            }
        });
    }

    public static void  addImage(File file,VBox vBox, String name){
        HBox hBox = new HBox();
        VBox vBox1 = new VBox();
        vBox1.setStyle("-fx-background-color: rgb(198,194,194);"+"-fx-background-radius: 5px");
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,10,5,10));
        Text text = new Text(name);
        vBox1.getChildren().add(text);
        Image image = new Image("file:"+file.getAbsolutePath());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        vBox1.setPadding(new Insets(5,10,5,10));
        vBox1.getChildren().add(imageView);
        hBox.getChildren().add(vBox1);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(hBox);
            }
        });
    }
}

