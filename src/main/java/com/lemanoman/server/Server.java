package com.lemanoman.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends GenericServer {

    private boolean close = false;
    private boolean isRunning = false;


    public Server() {

    }

    public void start() throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(PORT);
        System.out.println("THE SERVER STARTED ON " + PORT);
        ObjectMapper mapper = new ObjectMapper();
        isRunning = true;

        while (!close) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected");
            ServerTask task = new ServerTask(clientSocket,serverSocket);
            pool.submit(task);
        }
        serverSocket.close();

    }



    public boolean isClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
