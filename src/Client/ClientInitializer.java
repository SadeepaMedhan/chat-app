package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static javafx.scene.paint.Color.TRANSPARENT;

public class ClientInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("LoginForm.fxml")));
        scene.setFill(TRANSPARENT);
        scene.getStylesheets().add("Client/styles/style.css");
        primaryStage.setScene(scene);
        //primaryStage.setFullScreen(true);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }
}
