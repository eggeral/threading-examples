package software.egger;

import org.junit.Test;

public class aj_synchronized {

    static class SynchronizedCounter {
        private int currentValue;

        synchronized void inc() {
            int tmp = currentValue;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentValue = tmp + 1;
        }

        private int getCount() {
            return currentValue;
        }
    }

    static class PrivateLockSynchronizedCounter {
        private int currentValue;

        // Prevent others from stealing our lock!
        private Object lock = new Object();

        void inc() {

            synchronized (lock) {
                int tmp = currentValue;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentValue = tmp + 1;
            }

        }

        private int getCount() {
            return currentValue;
        }
    }

    @Test
    public void usingSynchronizedCounter() throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();

        Runnable worker = () -> {
            for (int idx = 0; idx < 5; idx++) {
                counter.inc();
                System.out.println("Counter: " + counter.getCount());
            }
        };

        // now we get 10!

        Thread t1 = new Thread(worker);
        Thread t2 = new Thread(worker);

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }


    @Test
    public void usingPrivateSynchronizedCounter() throws InterruptedException {
        PrivateLockSynchronizedCounter counter = new PrivateLockSynchronizedCounter();

        Runnable worker = () -> {
            for (int idx = 0; idx < 5; idx++) {
                counter.inc();
                System.out.println("Counter: " + counter.getCount());
            }
        };

        // now we get 10!

        Thread t1 = new Thread(worker);
        Thread t2 = new Thread(worker);

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }

    static class ABetterSingleton {
        private volatile static ABetterSingleton instance;

        static ABetterSingleton getInstance() {
            if (instance == null) {
                synchronized (ABetterSingleton.class) {
                    if (instance == null) {
                        try {
                            System.out.println("Waiting");
                            Thread.sleep(100); // The problem is the same even without sleep. But with sleep we get it more often.
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Creating instance");
                        ABetterSingleton result = new ABetterSingleton();
                        instance = result;
                        return result;
                    }
                }
            }
            System.out.println("Done: " + instance);
            return instance;
        }

    }

    @Test
    public void singleton() throws InterruptedException {

        ABetterSingleton[] singletons = new ABetterSingleton[2];

        Thread t1 = new Thread(() -> singletons[0] = ABetterSingleton.getInstance());
        Thread t2 = new Thread(() -> singletons[1] = ABetterSingleton.getInstance());

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("We got the same singleton: " + (singletons[0] == singletons[1]));

    }

    @Test
    public void reentrant() {
        // Reentrant means that a thread does not wait for locks it already owns.
        Object lock = new Object();

        synchronized (lock) {
            System.out.println("Got the lock the first time");
            synchronized (lock) {
                System.out.println("Got the lock the second time");
            }
        }

    }

}


