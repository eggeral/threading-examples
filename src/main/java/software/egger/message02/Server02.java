package software.egger.message02;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("Duplicates")
public class Server02 {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try {

            ServerSocket serverSocket = new ServerSocket(5678);

            while (true) {
                System.out.println("Server waits for a client to connect");
                Socket connectionToClient = serverSocket.accept();
                System.out.println("Client connected");

                executorService.submit(() -> clientConnectionHandler(connectionToClient));

                System.out.println("Server is done");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void clientConnectionHandler(Socket connectionToClient) {

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionToClient.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(connectionToClient.getOutputStream()));
        ) {

            System.out.println("Got from client: " + reader.readLine());

            writer.println("Hello from server");
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                connectionToClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
