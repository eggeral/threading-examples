package software.egger.message05;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings("ALL")
public class SenderClient05 {

    public static void main(String[] args) {

        LinkedBlockingQueue<String> requests = new LinkedBlockingQueue<>();

        Runnable sender = () -> {
            try (
                    Socket connectionToServer = new Socket("localhost", 5678);
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(connectionToServer.getOutputStream()));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connectionToServer.getInputStream()));

            ) {

                int requestNumber = 0;

                while (true) {

                    String request = requests.take();
                    System.out.println("Sending your request");
                    writer.println(requestNumber + ": " + request);
                    writer.flush();
                    System.out.println("Request sent. Waiting for response.");
                    System.out.println("Got from server: " + reader.readLine());
                    requestNumber++;
                    System.out.println(requests.size() + " more items in the queue.");

                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        };

        ExecutorService senderExecutor = Executors.newSingleThreadExecutor();
        senderExecutor.submit(sender);

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connection to server established");

            while (true) {
                System.out.println("Please enter your request: ");
                String line = scanner.nextLine();
                System.out.println("Putting request into queue");
                requests.put(line);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
