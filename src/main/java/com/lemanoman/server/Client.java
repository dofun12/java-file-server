package com.lemanoman.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.Socket;

public class Client extends GenericServer {

    private String host;
    private Integer port;

    public Client(String host) {
        this.host = host;
        this.port = PORT;
    }

    public Client(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public void getFile(String path, String dest) throws IOException {

        Socket sock = new Socket(host, port);

        sendString(sock, "download " + path);
        ObjectNode response = receiveJson(sock);
        if (response.hasNonNull("success") && response.get("success").asBoolean()) {
            receiveFile(sock, dest);
        }
        sock.close();
    }

    public ObjectNode status() throws IOException {

        ObjectNode response = null;
        Socket sock = null;
        try {
            sock = new Socket(host, port);
            sendString(sock, "status");
            response = receiveJson(sock);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sock != null) {
                sock.close();
            }
        }
        return response;

    }

    public ObjectNode listFile(String path) throws IOException {
        ObjectNode response = null;
        Socket sock = null;
        try {
            sock = new Socket(host, port);
            sendString(sock, "list " + path);
            response = receiveJson(sock);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sock != null) {
                sock.close();
            }
        }
        return response;

    }

    public static void main(String... args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Client client = new Client("127.0.0.1");
        client.status();
        //client.getFile("/home/kevim/teste-server/bigfile.deb","/home/kevim/teste-client/bigfile.deb");
        ObjectNode dirs = client.listFile("/mnt/usb/scavanger");
        if (dirs.get("success").asBoolean(false)) {
            ArrayNode array = mapper.convertValue(dirs.get("files"), ArrayNode.class);
            for (JsonNode node : array) {
                ObjectNode file = mapper.convertValue(node, ObjectNode.class);
                System.out.println(file.get("path"));

            }
        }

    }

}
