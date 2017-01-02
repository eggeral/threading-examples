package software.egger;

import org.junit.Test;

public class al_deadlock {

    @Test
    public void simpleDeadLock() throws InterruptedException {

        Object lock1 = new Object();
        Object lock2 = new Object();

        Thread t1 = new Thread(() -> {
            synchronized (lock1) {
                System.out.println("t1 got lock1");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t1 tries to get lock2");
                synchronized (lock2) {
                    System.out.println("t1 did something");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lock2) {
                System.out.println("t2 got lock2");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t2 tries to get lock1");
                synchronized (lock1) {
                    System.out.println("t2 did something");
                }
            }
        });

        t1.start();
        t2.start();

        t1.join(1000);
        System.out.println("Waited 1sec for t1 to complete");
        t2.join(1000);
        System.out.println("Waited 1sec for t2 to complete");

        System.out.println("t1 state: " + t1.getState());
        System.out.println("t2 state: " + t2.getState());

        System.out.println("== Try to unblock ==");
        System.out.println("Interrupt t1");
        t1.interrupt(); // this does of course nothing!
        System.out.println("t1 state: " + t1.getState());
        System.out.println("t2 state: " + t2.getState());

        System.out.println("Stop t1");
        t1.stop();
        System.out.println("t1 state: " + t1.getState());
        System.out.println("t2 state: " + t2.getState());

        System.out.println("== We are doomed! There seems to be no way to recover from this. ==");
    }


}


