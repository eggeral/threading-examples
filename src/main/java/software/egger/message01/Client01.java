package software.egger.message01;

import java.io.*;
import java.net.Socket;

public class Client01 {

    public static void main(String[] args) {

        try (

                Socket connectionToServer = new Socket("localhost", 5678);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(connectionToServer.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionToServer.getInputStream()));

        ) {

            System.out.println("Connection to server established");

            writer.println("Hello from client.");
            writer.flush();

            System.out.println("Got from server: " + reader.readLine());
            System.out.println("Client done");


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
