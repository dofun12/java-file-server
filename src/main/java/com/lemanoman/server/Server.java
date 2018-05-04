package com.lemanoman.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends GenericServer {

    private boolean close = false;
    private boolean isRunning = false;

    public Server() {

    }

    public void start() throws IOException {
        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(PORT);
        System.out.println("THE SERVER STARTED ON " + PORT);
        ObjectMapper mapper = new ObjectMapper();
        isRunning = true;

        while (!close) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected");

            String command = receiveString(clientSocket);
            if (command.startsWith("download")) {
                String path = command.replaceAll("download ","");

                ObjectNode response = mapper.createObjectNode();
                File file = new File(path);
                if (file.exists()) {
                    response.put("name", file.getName());
                    response.put("size", file.length());
                    response.put("lastmodified", file.lastModified());
                    response.put("success", true);

                    sendJson(clientSocket, response);
                    sendFile(clientSocket, path);
                } else {
                    response.put("message", "File not founded: "+path);
                    response.put("success", false);
                    sendJson(clientSocket, response);
                }
            }
            if (command.startsWith("status")) {
                ObjectNode response = mapper.createObjectNode();
                response.put("host",serverSocket.getInetAddress().getHostAddress());
                sendJson(clientSocket, response);
            }
            
            if (command.startsWith("list")) {
                String path = command.replaceAll("list ","");
                ObjectNode response = mapper.createObjectNode();
                
                File file = new File(path);
                if(file.exists() && file.isDirectory()){
                    ArrayNode array = mapper.createArrayNode();
                    for(File f:file.listFiles()){
                        ObjectNode fileNode = mapper.createObjectNode();
                        fileNode.put("path", f.getAbsolutePath());
                        fileNode.put("length",f.length());
                        fileNode.put("lastModified",f.lastModified());
                        fileNode.put("name", f.getName());
                        fileNode.put("directory",f.isDirectory());
                        array.add(fileNode);
                    }
                    response.set("files", array);
                    response.put("success",true);
                }else if(file.exists() && file.isFile()){
                    File f = file;
                    ArrayNode array = mapper.createArrayNode();
                    ObjectNode fileNode = mapper.createObjectNode();
                    fileNode.put("path", f.getAbsolutePath());
                    fileNode.put("length",f.length());
                    fileNode.put("lastModified",f.lastModified());
                    fileNode.put("name", f.getName());
                    fileNode.put("directory",f.isDirectory());
                    array.add(fileNode);
                    response.set("files", array);
                    response.put("success",true);
                }else{
                    response.put("success",false);
                    response.put("message","File not founded: "+path);
                }
                
                
                response.put("host",serverSocket.getInetAddress().getHostAddress());
                sendJson(clientSocket, response);
            }

            clientSocket.close();
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
