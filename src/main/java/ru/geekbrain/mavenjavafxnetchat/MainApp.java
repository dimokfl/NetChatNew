package ru.geekbrain.mavenjavafxnetchat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;


public class MainApp extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("/fxml/sample.fxml"));
//        primaryStage.setTitle("My Chat");
//        primaryStage.setScene(new Scene(root, 600, 400));
//        primaryStage.show();

        String fxmlFile = "/fxml/sample.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent root = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));
        primaryStage.setTitle("JavaFX and Maven");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

    }
}


