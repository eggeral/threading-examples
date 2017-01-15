package software.egger.message06;

import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.Scanner;

public class Client06 {

    public static void main(String[] args) {

        try (

                Socket connectionToServer = new Socket("localhost", 6789);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(connectionToServer.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionToServer.getInputStream()));

        ) {

            System.out.println("Connection to server established");

            while (true) {
                writer.println("GET");
                writer.flush();
                System.out.println(Instant.now() + ". Got from server: " + reader.readLine());
                Thread.sleep(500);
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

}
