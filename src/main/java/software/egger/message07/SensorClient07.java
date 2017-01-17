package software.egger.message07;

import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.concurrent.*;

public class SensorClient07 {

    public static void main(String[] args) {

        BlockingQueue<String> valuesQueue = new ArrayBlockingQueue<>(100);


        Runnable sender = () -> {
            try (

                    Socket connectionToServer = new Socket("localhost", 5678);
                    PrintWriter serverWriter = new PrintWriter(new OutputStreamWriter(connectionToServer.getOutputStream()));
                    BufferedReader serverReader = new BufferedReader(new InputStreamReader(connectionToServer.getInputStream()));

            ) {

                System.out.println("Connection to server established");
                while (true) {
                    String line = valuesQueue.take();
                    System.out.println("Sending " + line + " to server");
                    serverWriter.println(line);
                    serverWriter.flush();
                    String answer = serverReader.readLine();
                    System.out.println("Got from server: " + answer);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(sender);

        System.out.println("Connection to sensor established");

        Runnable poller = () -> {
            try (

                    Socket connectionToSensor = new Socket("localhost", 6789);
                    PrintWriter sensorWriter = new PrintWriter(new OutputStreamWriter(connectionToSensor.getOutputStream()));
                    BufferedReader sensorReader = new BufferedReader(new InputStreamReader(connectionToSensor.getInputStream()));

            ) {
                while (true) {
                    sensorWriter.println("GET");
                    sensorWriter.flush();
                    String line = sensorReader.readLine();
                    System.out.println(Instant.now() + ". Got from sensor: " + line);
                    valuesQueue.put(line);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        };

        ScheduledExecutorService pollerService = Executors.newScheduledThreadPool(2);
        pollerService.scheduleAtFixedRate(poller, 0, 500, TimeUnit.MILLISECONDS);


    }

}
