package software.egger.message06;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("Duplicates")
public class SensorServer06 {


    public static void main(String[] args) {

        Sensor sensor = new Sensor();
        ExecutorService sensorExecutorService = Executors.newSingleThreadExecutor();
        sensorExecutorService.submit(sensor);

        //client connections get their own thread pool!
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try {

            ServerSocket serverSocket = new ServerSocket(6789);

            while (true) {
                System.out.println("Server waits for a client to connect");
                Socket connectionToClient = serverSocket.accept();
                System.out.println("Client connected");

                executorService.submit(() -> clientConnectionHandler(connectionToClient, sensor));

                System.out.println("Server is done");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void clientConnectionHandler(Socket connectionToClient, Sensor sensor) {

        try (

                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionToClient.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(connectionToClient.getOutputStream()))

        ) {

            String line;
            while (!(line = reader.readLine()).equals("EXIT")) {

                System.out.println(Instant.now() + ". Got " + line + " from client.");

                if (line.equals("GET")) {
                    double value = sensor.getValue();
                    System.out.println(Instant.now() + ". Value read from sensor");
                    writer.println("Value: " + value);
                    writer.flush();
                    System.out.println(Instant.now() + ". Value returned");
                }

            }

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
