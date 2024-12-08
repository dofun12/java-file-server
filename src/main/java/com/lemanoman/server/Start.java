package com.lemanoman.server;

import org.apache.commons.cli.*;

import java.io.IOException;

public class Start implements OnCommandAction{
    private final static int PORT = 8080;
    public Start() {
        // TODO document why this constructor is empty
    }
    public static void main(String... args) throws IOException {
        new ArgParser(new Start(), args);

    }

    @Override
    public void serverStart() {
        Server server = new Server();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clientStart(String host, String filePath, String destPath) {
        try {
            Client client = new Client(host);
            client.getFile(filePath, destPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}