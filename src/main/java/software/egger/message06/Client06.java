package software.egger.message06;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client06 {

    public static void main(String[] args) {

        try (

                Socket connectionToServer = new Socket("", 5678);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(connectionToServer.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionToServer.getInputStream()));
                Scanner scanner = new Scanner(System.in)

        ) {

            System.out.println("Connection to server established");

            while (true) {
                String line = scanner.nextLine();
                writer.println(line);
                writer.flush();
                System.out.println("Got from server: " + reader.readLine());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
