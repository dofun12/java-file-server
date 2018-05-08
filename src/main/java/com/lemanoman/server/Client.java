package com.lemanoman.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class Client extends GenericServer {

    private String host;
    private Integer port;
    private Socket clientSocket;

    public Client(String host) throws IOException{
        this.host = host;
        this.port = PORT;
        clientSocket = new Socket(host, port);
    }

    public Client(String host, Integer port) throws IOException {
        this.host = host;
        this.port = port;
        clientSocket = new Socket(host, port);
    }

    public void getFile(String path, String dest) throws IOException {
        sendString(clientSocket, "download " + path);
        ObjectNode response = receiveJson(clientSocket);
        if (response.hasNonNull("success") && response.get("success").asBoolean()) {
            receiveFile(clientSocket, dest);
        }

    }

    public ObjectNode status() throws IOException {
        ObjectNode response = null;
        sendString(clientSocket, "status");
        response = receiveJson(clientSocket);
        return response;
    }

    public ObjectNode listFile(String path) throws IOException {
        ObjectNode response = null;
        sendString(clientSocket, "list " + path);
        response = receiveJson(clientSocket);
        return response;
    }

    public void close() throws IOException {
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client("192.168.15.102");
        try {

            ObjectMapper mapper = new ObjectMapper();

            //client.status();
            //client.getFile("/home/kevim/teste-server/bigfile.deb","/home/kevim/teste-client/bigfile.deb");
            ObjectNode dirs = client.listFile("/mnt/usb/scavanger");
            if (dirs.get("success").asBoolean(false)) {
                ArrayNode array = mapper.convertValue(dirs.get("files"), ArrayNode.class);
                for (JsonNode node : array) {
                    ObjectNode file = mapper.convertValue(node, ObjectNode.class);
                    if(!file.get("directory").asBoolean()){
                        File dest = new File("C:\\Users\\Kevim Such\\Desktop\\output\\"+file.get("name").asText());
                        if(!dest.exists()){
                            client.getFile(file.get("path").asText(),dest.getAbsolutePath());
                        }

                    }


                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            client.close();
        }


    }

}
