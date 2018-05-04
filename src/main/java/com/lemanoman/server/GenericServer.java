package com.lemanoman.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.net.Socket;

public class GenericServer {
    final protected int PORT=6500;
    public void sendString(Socket socket,String string) throws IOException {
        //Send the message to the server
        OutputStream os = socket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);
        string = string + "\n";
        bw.write(string);
        bw.flush();
        System.out.println("Message sent  : "+string);
    }

    public String receiveString(Socket socket) throws IOException {
        //Get the return message from the server
        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String message = br.readLine();
        System.out.println("Message received  : " +message);
        return  message;
    }

    public void sendFile(Socket socket,String path) throws IOException {
        System.out.println("Sending file  : " +path);

        File myFile = new File(path);
        byte[] mybytearray = new byte[(int) myFile.length()];

        FileInputStream fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        //bis.read(mybytearray, 0, mybytearray.length);

        DataInputStream dis = new DataInputStream(bis);
        dis.readFully(mybytearray, 0, mybytearray.length);

        OutputStream os = socket.getOutputStream();
        //Sending file data to the server
        os.write(mybytearray, 0, mybytearray.length);
        os.flush();

        //Closing socket
        os.close();
    }

    public void receiveFile(Socket socket,String dest) throws IOException{
        System.out.println("Receiving file at  : " +dest);

        InputStream in = socket.getInputStream();
        DataInputStream clientData = new DataInputStream(in);
        int bytesRead;


        OutputStream output = new FileOutputStream(dest);
        long size = clientData.readLong();
        byte[] buffer = new byte[1024];
        while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            output.write(buffer, 0, bytesRead);
            size -= bytesRead;
        }
        output.close();
    }

    public void sendJson(Socket socket,ObjectNode node) throws IOException{
        String json = "{}";
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = mapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        sendString(socket,json);
    }

    public ObjectNode receiveJson(Socket socket) throws IOException{
        String json = receiveString(socket);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json,ObjectNode.class);
    }


}
