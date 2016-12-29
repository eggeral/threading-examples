package software.egger;

import static software.egger.util.Utils.sleep;

public class ac_daemon_threads {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("main start");

        Thread userThread = new Thread(() -> {
            System.out.println("Thread start");
            sleep(3000L);
            System.out.println("Thread end");
        });
        userThread.setDaemon(true);
        userThread.start();

        System.out.println("main end");
    }
}