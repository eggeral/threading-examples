package software.egger.message04;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("Duplicates")
public class Server04 {

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

            String line;
            while (!(line = reader.readLine()).equals("Exit")) {

                System.out.println("Got from client: " + line);
                System.out.println("Doing some work which takes long");
                Thread.sleep(5000);
                writer.println("Response from server: " + line.split(":")[0]);
                writer.flush();

            }

        } catch (IOException | InterruptedException e) {
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
