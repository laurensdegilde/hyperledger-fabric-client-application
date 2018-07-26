package main;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hyperledger.fabric.sdk.exception.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Build.fxml"));
        primaryStage.setTitle("Alpha client application");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
