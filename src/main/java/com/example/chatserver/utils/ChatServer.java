package com.example.chatserver.utils;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer implements Runnable{
    private final List<PrintWriter> clientWriters = new ArrayList<>();
    private final int port;
    private final TextArea textArea;
    private ExecutorService threadPool;

    public ChatServer(int port, TextArea chatTextArea) {
        this.port = port;
        this.textArea = chatTextArea;
    }

    public void run() {
        threadPool = Executors.newCachedThreadPool();
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));

            while (serverSocketChannel.isOpen()) {
                SocketChannel clientSocket = serverSocketChannel.accept();
                textArea.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm:ss ")) + "\t got a connection!\n");
                PrintWriter writer = new PrintWriter(Channels.newWriter(clientSocket, StandardCharsets.UTF_8));
                clientWriters.add(writer);
                threadPool.submit(new ClientHandler(clientSocket, this, textArea));
            }
        } catch (Exception e) {
            textArea.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm:ss ")) + "\t" + e + "\n");
        }


    }

    public void tellEveryone(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
            writer.flush();
        }
        textArea.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm:ss ")) + "\t" + message + "\n");

    }

    public void cleanUp() {
        for (PrintWriter writer : clientWriters) {
            if (writer != null) {
                writer.close();
            }
        }
        if (threadPool != null) threadPool.shutdownNow();

    }
}
