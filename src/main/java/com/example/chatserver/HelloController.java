package com.example.chatserver;

import com.example.chatserver.utils.ChatServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloController {
    ChatServer chatServer;
    ExecutorService executorService;
    @FXML
    public TextArea chatTextArea;

    @FXML
    public TextField portTextField;

    public void startServerButtonClicked() {
        if (chatServer != null) {
            try {
                chatServer.cleanUp();
                chatServer = null;
            } catch (Exception e) {
                chatTextArea.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm:ss ")) + "\t" + e + "\n");
                return;
            }
        }
        try {
            int serverPort = Integer.parseInt(portTextField.getText());
            chatServer = new ChatServer(serverPort, chatTextArea);
            executorService = Executors.newSingleThreadExecutor();
            executorService.execute(chatServer);
            chatTextArea.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm:ss ")) + "\t server started at port : " + serverPort + "\n");
        } catch (Exception e) {
            chatTextArea.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm:ss ")) + "\t" + e + "\n");
        }
    }

    public void shutDown() throws IOException {
        if (chatServer != null) chatServer.cleanUp();
        executorService.shutdownNow();
        System.out.println("exit!");
    }

}