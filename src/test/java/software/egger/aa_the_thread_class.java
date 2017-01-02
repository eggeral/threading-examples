package software.egger;

import org.junit.Test;

import java.math.BigInteger;

public class aa_the_thread_class {

    private void slowLoop(int times) {
        BigInteger max = new BigInteger("99999");
        BigInteger idx = BigInteger.ZERO;
        for (int count = 0; count < times; count++) {
            while (!idx.equals(max)) {
                idx = idx.add(BigInteger.ONE);
            }
        }
    }

    @Test
    public void constructingRunnableAndThread() {
        // Thread and Runnable

        // 1. way to create a Runnable
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("R1 run");
            }
        };
        r1.run(); // This does not start a Thread!

        // 2. Java 8 way to create a Runnable
        Runnable r2 = () -> System.out.println("R2 run");

        // A Thread takes a Runnable to execute it.
        Thread t1 = new Thread(r2);
        t1.run();

        // We can also extend Thread to create a Thread
        class MyThread extends Thread {
            @Override
            public void run() {
                System.out.println("MyThread run");
            }
        }

        Thread t2 = new MyThread();
        t2.run();

    }


    @Test
    public void threadProperties() {
        // Setting the name of a Thread is useful for debugging
        Runnable r1 = () -> {
            System.out.println(Thread.currentThread().getName() + " start");
            slowLoop(10);
            System.out.println(Thread.currentThread().getName() + " end");
        };
        Thread t1 = new Thread(r1, "my thread 1");
        System.out.println(t1.getName());
        System.out.println("======");

        // A Thread can be asked for its alive state
        System.out.println("t1.isAlive(): " + t1.isAlive());
        t1.start(); // Starting the thread
        System.out.println("t1.isAlive(): " + t1.isAlive());
        slowLoop(20);
        System.out.println("t1.isAlive(): " + t1.isAlive());
        System.out.println("======");

        // A Thread has a state
        t1 = new Thread(r1, "my thread 2");
        System.out.println("t1.getState(): " + t1.getState());
        t1.start(); // Starting the thread
        System.out.println("t1.getState(): " + t1.getState());
        slowLoop(20);
        System.out.println("t1.getState(): " + t1.getState());
        // t1.start(); // A thread can not be started twice
        // See State enum for other Thread states.
        System.out.println("======");

        // Threads can set a priority
        t1 = new Thread(r1, "my thread 3");
        t1.setPriority(Thread.MAX_PRIORITY); // Attention! Thread priority handling depends on OS specific thread scheduler.
        t1.setPriority(Thread.MIN_PRIORITY);
        t1.setPriority(Thread.NORM_PRIORITY);
        System.out.println(t1.getPriority());

    }
}