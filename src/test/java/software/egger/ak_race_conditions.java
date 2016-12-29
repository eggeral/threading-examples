package software.egger;

import org.junit.Test;

public class ak_race_conditions {
    static class Counter {
        private int currentValue;

        public void inc() {
            int tmp = currentValue;
            try {
                Thread.sleep(100); // The problem is the same even without sleep. But with sleep we get it more often.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentValue = tmp + 1;
        }

        private int getCount() {
            return currentValue;
        }
    }

    @Test
    public void classicAddProblem() throws InterruptedException {
        Counter counter = new Counter();

        Runnable worker = () -> {
            for (int idx = 0; idx < 5; idx++) {
                counter.inc();
                System.out.println("Counter: " + counter.getCount());
            }
        };

        // We expect the counter to be 10 at the end. But we get 5

        Thread t1 = new Thread(worker);
        Thread t2 = new Thread(worker);

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }

    static class SimpleSingleton {
        private static SimpleSingleton instance;

        public static SimpleSingleton getInstance() {
            if (instance == null) {
                try {
                    Thread.sleep(100); // The problem is the same even without sleep. But with sleep we get it more often.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                instance = new SimpleSingleton();
            }
            return instance;
        }

    }

    @Test
    public void notASingleton() {

        SimpleSingleton s1 = null;
        SimpleSingleton s2 = null;

        Runnable worker = () -> {
            s1 = SimpleSingleton.getInstance();
        };

        // We expect the counter to be 10 at the end. But we get 5

        Thread t1 = new Thread(worker);
        Thread t2 = new Thread(worker);

        t1.start();
        t2.start();

        t1.join();
        t2.join();


    }

}
