package ru.geekbrain.mavenjavafxnetchat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.time.format.DateTimeFormatter;


public class MainApp extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void start(Stage primaryStage) throws Exception {

        String fxmlFile = "/fxml/sample.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent root = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));
        primaryStage.setTitle("JavaFX and Maven");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest((EventHandler<WindowEvent>) event -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();


    }
}

