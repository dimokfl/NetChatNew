package ru.geekbrain.mavenjavafxnetchat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class MainController<MASSAGE_MAX> implements Initializable {

    @FXML
    TextField msgField, usernameField;

    @FXML
    TextArea msgArea;

    @FXML
    HBox loginPanel, msgPanel;

    @FXML
    ListView<String> clientsList;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private String login;
    private File inputMsg;
    private FileReader reader;
    private FileWriter writer;
    private ReversedLinesFileReader reversedLinesFileReader;
    private List<String> list;
    private final int MASSAGE_PRINT_MAX = 100;



    public void setUsername(String username) {
        this.username = username;
        if (username != null) {
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
            msgPanel.setVisible(true);
            msgPanel.setManaged(true);
            clientsList.setVisible(true);
            clientsList.setManaged(true);
        } else {
            loginPanel.setVisible(true);
            loginPanel.setManaged(true);
            msgPanel.setVisible(false);
            msgPanel.setManaged(false);
            clientsList.setVisible(false);
            clientsList.setManaged(false);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUsername(null);
    }

    public void login() {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        if (usernameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Имя пользователя не может быть пустым", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
            out.writeUTF("/login " + usernameField.getText());
            login = usernameField.getText();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t = new Thread(() -> {
                try {
                    // Цикл авторизации
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/login_ok ")) {
                            setUsername(msg.split("\\s")[1]);
                            break;
                        }
                        if (msg.startsWith("/login_failed ")) {
                            String cause = msg.split("\\s", 2)[1];
                            msgArea.appendText(cause + "\n");
                        }
                    }

                    // Запуск логгирования истории чата
                    Platform.runLater(() -> {
                        System.out.println(Thread.currentThread().getName());
                    logHistoryChat();
                    });

                    // Цикл общения
                    while (true) {
                        String msg = in.readUTF();

                        if (msg.startsWith("/")) {
                            if (msg.startsWith("/clients_list ")) {
                                // /clients_list Bob Max Jack
                                String[] tokens = msg.split("\\s");

                                Platform.runLater(() -> {
                                    System.out.println(Thread.currentThread().getName());
                                    clientsList.getItems().clear();
                                    for (int i = 1; i < tokens.length; i++) {
                                        clientsList.getItems().add(tokens[i]);
                                    }
                                });

                            }
                            continue;
                        }

                        msgArea.appendText(msg + "\n");

                        if (writer != null) {
                            writer.write(msg + "\n");
                            writer.flush();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            });
            t.start();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно подключиться к серверу", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно отправить сообщение", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void logHistoryChat() {
        inputMsg = new File("history_" + login + ".txt");
        int massageStart = 0;
        try {
            if (!inputMsg.exists()) {
                inputMsg.createNewFile();
            }
            if (inputMsg.exists()) {
                writer = new FileWriter(inputMsg, true);
                reader = new FileReader(inputMsg);
                reversedLinesFileReader = new ReversedLinesFileReader(inputMsg, null);
                String readLine = reversedLinesFileReader.readLine();
                list = new ArrayList<>();
                while (readLine != null && massageStart <= MASSAGE_PRINT_MAX){
                    list.add(readLine);
                    readLine = reversedLinesFileReader.readLine();
                    massageStart++;
                }
                for (int i = list.size()-1; i >= 0; i--) {
                    msgArea.appendText(list.get(i) + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        setUsername(null);
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}