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


    public void loginOnAction(ActionEvent actionEvent) throws IOException {

        if(!txtUsername.getText().equals("")){
            userName = txtUsername.getText();
            Stage stage = (Stage) loginFormContext.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("ChatForm.fxml"))));
            //stage.setResizable(false);

            stage.centerOnScreen();
            stage.show();
        }else{
            txtUsername.setUnFocusColor(Paint.valueOf("#ff0000"));
        }
    }


    public void closeOnAction(MouseEvent mouseEvent) {
        System.exit(0);
    }
}
