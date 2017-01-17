package software.egger.message07;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReceiverClient07 {

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
