package com.lemanoman.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.io.*;
import java.net.Socket;

public class GenericServer {
    final protected int PORT = 6050;

    public void sendString(Socket socket, String string) throws IOException {
        //Send the message to the server
        OutputStream os = socket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);
        string = string + "\n";
        bw.write(string);
        bw.flush();
        System.out.println("Message sent  : " + string);
    }

    public String receiveString(Socket socket) throws IOException {
        //Get the return message from the server
        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String message = br.readLine();
        System.out.println("Message received  : " + message);
        return message;
    }

    public void sendFile(Socket socket, String path) throws IOException {
        OutputStream os = socket.getOutputStream();

        System.out.println("Sending file  : " + path);

        File myFile = new File(path);


        FileInputStream fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        byte[] buffer = new byte[8192];
        int b;
        do {
            b = bis.read(buffer);
            os.write(buffer,0,b);
        } while (b != -1);
        fis.close();
        bis.close();
        os.flush();
        //os.close();
    }

    public void receiveFile(Socket socket, String dest) throws IOException {
        System.out.println("Receiving file at  : " + dest);

        InputStream in = socket.getInputStream();
        FileOutputStream fos = new FileOutputStream(dest);
        BufferedInputStream bis = new BufferedInputStream(in);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        byte[] buffer = new byte[8192];
        int b;
        do {
            b = bis.read(buffer);
            bos.write(buffer,0,b);
        } while (b != -1);
        fos.close();
        bis.close();
        //in.close();
    }

    public void sendJson(Socket socket, ObjectNode node) throws IOException {
        String json = "{}";
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = mapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        sendString(socket, json);
    }

    public ObjectNode receiveJson(Socket socket) throws IOException {
        String json = receiveString(socket);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, ObjectNode.class);
    }


}
