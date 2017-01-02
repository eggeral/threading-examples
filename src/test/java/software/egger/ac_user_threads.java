package software.egger;

import java.math.BigInteger;

public class ac_user_threads {

    private static void slowLoop(int times) {
        BigInteger max = new BigInteger("99999");
        BigInteger idx = BigInteger.ZERO;
        for (int count = 0; count < times; count++) {
            while (!idx.equals(max)) {
                idx = idx.add(BigInteger.ONE);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("main start");

        Thread userThread = new Thread(() -> {
            System.out.println("Thread start");
            slowLoop(3000);
            System.out.println("Thread end");
        });
        userThread.start();

        System.out.println("main end");
    }
}