package Server;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFormController {
    public TextField txtServerMessage;
    public TextArea txtServerPane;
    public VBox chatContext;
    String message = "";
    Socket accept=null;
    FileChooser fileChooser = new FileChooser();



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
                        HBox hBox = new HBox();
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        hBox.setPadding(new Insets(5,5,5,10));
                        Text text = new Text(record);
                        TextFlow textFlow = new TextFlow(text);
                        textFlow.setStyle("-fx-background-color: #aaaeb1; -fx-background-radius: 20px");
                        textFlow.setPadding(new Insets(5,10,5,10));
                        hBox.getChildren().add(textFlow);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                chatContext.getChildren().add(hBox);
                            }
                        });
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

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5,5,5,10));
        Text text = new Text(myReply);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #0868a7; -fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5,10,5,10));
        text.setFill(Color.color(1,1,1));
        hBox.getChildren().add(textFlow);
        chatContext.getChildren().add(hBox);
        txtServerMessage.clear();
    }

    public void uploadImageOnAction(MouseEvent mouseEvent) {
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            System.out.println(file.getAbsolutePath());
            Image image = new Image(getClass().getResourceAsStream(file.getAbsolutePath()));
            ImageView imageView = new ImageView(image);
            chatContext.getChildren().add(imageView);
        }
    }

    public void closeOnAction(MouseEvent mouseEvent) {
        System.exit(0);
    }
}
