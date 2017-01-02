package software.egger;

import java.math.BigInteger;

public class ad_daemon_threads {

    private static void slowLoop(int times) {
        BigInteger max = new BigInteger("99999");
        BigInteger idx = BigInteger.ZERO;
        for (int count = 0; count < times; count++) {
            while (!idx.equals(max)) {
                idx = idx.add(BigInteger.ONE);
            }
        }
    }


    public static void main(String[] args) throws InterruptedException {
        System.out.println("main start");

        Thread userThread = new Thread(() -> {
            System.out.println("Thread start");
            slowLoop(3000);
            System.out.println("Thread end");
        });
        userThread.setDaemon(true);
        userThread.start();

        System.out.println("main end");
    }
}