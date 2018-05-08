package com.lemanoman.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTask extends GenericServer implements Runnable {

    final private ObjectMapper mapper = new ObjectMapper();
    private Socket clientSocket;
    private ServerSocket serverSocket;

    public ServerTask(Socket clientSocket,ServerSocket serverSocker) {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocker;
    }


    @Override
    public void run() {
        try {
            boolean keepRunning = true;
            while (keepRunning) {
                String command = receiveString(clientSocket);


                if (command.startsWith("download")) {
                    String path = command.replaceAll("download ", "");
                    download(clientSocket, path);
                }
                if (command.startsWith("status")) {
                    ObjectNode response = mapper.createObjectNode();
                    response.put("host", serverSocket.getInetAddress().getHostAddress());
                    sendJson(clientSocket, response);
                }

                if (command.startsWith("list")) {
                    String path = command.replaceAll("list ", "");
                    listFiles(clientSocket, path);
                }
            }
            clientSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void listFiles(Socket clientSocket,String path) throws IOException {
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


        //response.put("host",serverSocket.getInetAddress().getHostAddress());
        sendJson(clientSocket, response);

    }

    public void download(Socket clientSocket,String path) throws IOException {


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
}
