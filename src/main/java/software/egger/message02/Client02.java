package software.egger.message02;

import java.io.*;
import java.net.Socket;

@SuppressWarnings("Duplicates")
public class Client02 {

    public static void main(String[] args) throws InterruptedException {

        try (

                Socket connectionToServer = new Socket("localhost", 5678);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(connectionToServer.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionToServer.getInputStream()));

        ) {

            System.out.println("Connection to server established");

            Thread.sleep(5000); // so we have time to start a second client.

            writer.println("Hello from client.");
            writer.flush();

            System.out.println("Got from server: " + reader.readLine());

            System.out.println("Client done");


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
