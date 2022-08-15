package Client;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFormController {
    public StackPane loginFormContext;
    public JFXTextField txtUsername;
    public static String userName;
    public static String host;
    public JFXTextField txtHost;


    public void loginOnAction(ActionEvent actionEvent) throws IOException {

        if(!txtUsername.getText().equals("")){
            userName = txtUsername.getText();
            if(!txtHost.getText().equals("")){
                Stage log = (Stage) loginFormContext.getScene().getWindow();
                log.close();
                host = txtHost.getText();
                Stage stage = new Stage();
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("ChatForm.fxml"))));
                stage.setResizable(false);
                stage.centerOnScreen();
                stage.show();
            }else{
                Stage log = (Stage) loginFormContext.getScene().getWindow();
                log.close();
                host = "localhost";
                Stage stage = new Stage();
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("ChatForm.fxml"))));
                stage.setResizable(false);
                stage.centerOnScreen();
                stage.show();
            }
        }else{
            txtUsername.setUnFocusColor(Paint.valueOf("#ff0000"));
        }
    }


    public void closeOnAction(MouseEvent mouseEvent) {
        System.exit(0);
    }
}
