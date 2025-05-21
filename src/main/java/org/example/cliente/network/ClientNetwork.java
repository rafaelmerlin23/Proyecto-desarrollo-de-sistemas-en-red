package org.example.cliente.network;


import org.example.shared.*;
import java.io.*;
import java.net.*;

public class ClientNetwork {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public static Response sendRequest(Request request) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            oos.writeObject(request);
            return (Response) ois.readObject();

        } catch (ConnectException e) {
            return new Response(false, "No se pudo conectar al servidor. Asegúrese de que el servidor esté en ejecución.");
        } catch (IOException | ClassNotFoundException e) {
            return new Response(false, "Error de comunicación: " + e.getMessage());
        }
    }
}