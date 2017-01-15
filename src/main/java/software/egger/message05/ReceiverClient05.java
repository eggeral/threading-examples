package software.egger.message05;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ReceiverClient05 {

    public static void main(String[] args) {


        try (
                Socket connectionToServer = new Socket("localhost", 4567);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionToServer.getInputStream()));

        ) {

            while (true) {

                System.out.println(reader.readLine());

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
