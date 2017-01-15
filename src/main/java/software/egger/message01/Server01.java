package software.egger.message01;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server01 {

    public static void main(String[] args) {

        try {

            ServerSocket serverSocket = new ServerSocket(5678);
            System.out.println("Server waits for a client to connect");
            try (
                    Socket connectionToClient = serverSocket.accept();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connectionToClient.getInputStream()));
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(connectionToClient.getOutputStream()));
            ) {
                System.out.println("Client connected");
                System.out.println("Got from client: " + reader.readLine());

                writer.println("Hello from server");
                writer.flush();
            }
            System.out.println("Server is done");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
