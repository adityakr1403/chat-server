package com.example.chatserver.utils;

import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler implements Runnable {
    ChatServer server;
    BufferedReader reader;
    SocketChannel socketChannel;
    TextArea textArea;

    public ClientHandler(SocketChannel clientSocket, ChatServer server, TextArea chatTextArea) {
        socketChannel = clientSocket;
        reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
        this.server = server;
        this.textArea = chatTextArea;
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                server.tellEveryone(message);
            }
        } catch (Exception e) {
            textArea.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm:ss ")) + "\t" + e + "\n");

        }
    }
}
