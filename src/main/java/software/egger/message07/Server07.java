package software.egger.message07;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings("Duplicates")
public class Server07 {

    private static volatile boolean stop = false;


    private static List<LinkedBlockingQueue<String>> receiverBuffers = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        Runnable sourcesHandler = () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(5678);

                while (!stop) { // this is actually useless unless we set soTimeout in order for the server socket to get out of accept once and a while.
                    System.out.println("Server waits for a sources to connect");
                    Socket connectionToClient = serverSocket.accept();
                    System.out.println("Source connected");

                    executorService.submit(() -> sourcesHandler(connectionToClient));

                    System.out.println("Server is done");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        };

        Runnable sinksHandler = () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(4567);

                while (!stop) {
                    System.out.println("Server waits for a sinks to connect");
                    Socket connectionToClient = serverSocket.accept();
                    System.out.println("Sink connected");

                    LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
                    receiverBuffers.add(queue);
                    executorService.submit(() -> sinkHandler(connectionToClient, queue));

                    System.out.println("Server is done");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        };


        ExecutorService sourcesHandlerExecutor = Executors.newSingleThreadExecutor();
        sourcesHandlerExecutor.submit(sourcesHandler);

        ExecutorService sinkHandlerExecutor = Executors.newSingleThreadExecutor();
        sinkHandlerExecutor.submit(sinksHandler);

    }

    private static void sourcesHandler(Socket connectionToClient) {

        try (

                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionToClient.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(connectionToClient.getOutputStream()));

        ) {

            String line;
            while (!(line = reader.readLine()).equals("Exit")) {

                System.out.println("Got from client: " + line);
                for (LinkedBlockingQueue<String> receiverBuffer : receiverBuffers) {
                    receiverBuffer.put(line);
                }
                writer.println("ACK");
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


    private static void sinkHandler(Socket connectionToClient, LinkedBlockingQueue<String> queue) {

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(connectionToClient.getOutputStream()))) {

            while (true) {
                String value = queue.take();
                writer.println(value);
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
