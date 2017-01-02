package software.egger;

import org.junit.Test;

public class am_wait_notify {

    @Test
    public void simpleWait() throws InterruptedException {

        Object lock = new Object();

        Thread t = new Thread(() -> {
            synchronized (lock) { // wait has to be called in synchronized otherwise we get java.lang.IllegalMonitorStateException
                System.out.println("t waits");
                try {
                    lock.wait(); // the also releases the lock! Think of what happens to the sum example if we use wait() there!
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t awake again");
            }
        });

        t.start();
        Thread.sleep(100); // Give t some time to enter wait()
        synchronized (lock) { // notify has to be called in synchronized otherwise we get java.lang.IllegalMonitorStateException
            System.out.println("calling notify");
            lock.notify();
            System.out.println("notify called");
        }
        t.join();

    }

    @Test
    public void waitAlsoExitsOnInterrupt() throws InterruptedException {

        Object lock = new Object();

        Thread t = new Thread(() -> {
            synchronized (lock) {
                System.out.println("t waits");
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    System.out.println("wait interrupted");
                    System.out.println("isInterrupted(): " + Thread.currentThread().isInterrupted());
                    System.out.println("interrupted(): " + Thread.interrupted());
                }
                System.out.println("t awake again");
            }
        });

        t.start();
        Thread.sleep(100); // Give t some time to enter wait()
        t.interrupt();
        t.join();

    }


    @SuppressWarnings("Duplicates")
    @Test
    public void notifyReleasesOneThreads() throws InterruptedException {

        Object lock = new Object();

        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("t1 waits");
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    System.out.println("t1 wait interrupted");
                }
                System.out.println("t1 awake again");
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("t2 waits");
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    System.out.println("t2 wait interrupted");
                }
                System.out.println("t2 awake again");
            }
        });

        t1.start();
        t2.start();
        Thread.sleep(100); // Give t1, t2 some time to enter wait()

        synchronized (lock) {
            System.out.println("calling notify (1)");
            lock.notify();
            System.out.println("notify (1) called");
            System.out.println("calling notify (2)");
            lock.notify();
            System.out.println("notify (2) called");
        }

        t1.join();
        t2.join();

    }


    @SuppressWarnings("Duplicates")
    @Test
    public void notifyAllReleasesAllThreads() throws InterruptedException {

        Object lock = new Object();

        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("t1 waits");
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    System.out.println("t1 wait interrupted");
                }
                System.out.println("t1 awake again");
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("t2 waits");
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    System.out.println("t2 wait interrupted");
                }
                System.out.println("t2 awake again");
            }
        });

        t1.start();
        t2.start();
        Thread.sleep(100); // Give t1, t2 some time to enter wait()

        synchronized (lock) {
            System.out.println("calling notifyAll");
            lock.notifyAll();
            System.out.println("notifyAll called");
        }

        t1.join();
        t2.join();

    }


    @Test
    public void waitCanHaveTimeOuts() throws InterruptedException {

        Object lock = new Object();

        Thread t = new Thread(() -> {
            synchronized (lock) {
                System.out.println("t waits");
                try {
                    lock.wait(100, 1); // there is also an implementation of wait without nanos. Look at the implementations of wait with nanos! timeout ++!
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t awake again");
            }
        });

        t.start();
        t.join();

    }

    private int count = 0;

    @Test
    public void waitConditionAlwaysHasToBeCheckedInALoop() throws InterruptedException {
        // see also documentation of wait()

        Object lock = new Object();

        Thread t = new Thread(() -> {
            synchronized (lock) {
                System.out.println("t waits");
                while (count < 10) { // in very rare cases the thread can exit wait so we always have to check the condition in a loop!
                    try {
                        System.out.println("count not high enough!");
                        lock.wait();
                        System.out.println("t exited wait");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("t is done");
            }
        });

        t.start();
        Thread.sleep(10);
        synchronized (lock) {
            count = 5;
            lock.notify();
            Thread.sleep(10);
            count = 11;
            lock.notify();
        }
        t.join();


    }

}
