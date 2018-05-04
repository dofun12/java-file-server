package com.lemanoman.server;

import java.io.IOException;

public class Start {
    public static void main(String... args) throws IOException {
        String operation;
        if (args != null) {
            operation = args[0];
        } else {
            operation = "test";
        }

        Server server = new Server();
        switch (operation) {
            case "server":
                try {
                    server.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "test":
                Thread runnable = new Thread() {
                    @Override
                    public void run() {
                        try {
                            server.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                runnable.start();
                while (!server.isRunning()) ;
                try {
                    Client client = new Client("127.0.0.1");
                    client.getFile("/home/kevim/teste-server/bigfile.deb","/home/kevim/teste-client/bigfile.deb");
                    server.setClose(true);
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "client":
                Client client = new Client(args[1]);
                client.getFile(args[2],args[3]);
                server.setClose(true);
                System.exit(0);
                break;
            default:
                break;
        }
    }
}
