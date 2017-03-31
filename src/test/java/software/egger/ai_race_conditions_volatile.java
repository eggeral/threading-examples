package software.egger;

import org.junit.Test;

public class ai_race_conditions_volatile {
    static class Counter {
        private int currentValue;

        void inc() { // If you think currentValue ++ would solve the problem you are wrong!
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

    private int counter = 0;

    @Test
    public void verySimpleRaceCondition() throws InterruptedException {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (int idx = 0; idx < 100000; idx++) {
                    counter++;
                }
            }
        };

        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(counter);

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

        static SimpleSingleton getInstance() {
            if (instance == null) {
                try {
                    System.out.println("Waiting");
                    Thread.sleep(100); // The problem is the same even without sleep. But with sleep we get it more often.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Creating instance");
                SimpleSingleton result = new SimpleSingleton();
                instance = result;
                return result;
            }
            System.out.println("Done: " + instance);
            return instance;
        }

    }

    @Test
    public void notASingleton() throws InterruptedException {

        SimpleSingleton[] singletons = new SimpleSingleton[2];

        Thread t1 = new Thread(() -> singletons[0] = SimpleSingleton.getInstance());
        Thread t2 = new Thread(() -> singletons[1] = SimpleSingleton.getInstance());

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("We got the same singleton: " + (singletons[0] == singletons[1]));

    }

    private volatile boolean stop = false;
    //private boolean stop = false;
    // without volatile each thread keeps a copy of stop in cache.
    // The thread never leaves the while loop

    @Test
    public void cachedVariables() throws InterruptedException {

        // Threads keep variables in local processor registries or caches.
        // This means the value of storage is not always the same between treads.
        Thread t = new Thread(() -> {
            System.out.println("T started");
            while (!stop) ; // wait for stop
            System.out.println("T is done");
        });

        t.start();
        Thread.sleep(100); // give t1 some time to start
        System.out.println("stop");
        stop = true;
        t.join();

    }


    static class Stop {
        private boolean stop = false;

        public boolean isStop() {
            return stop;
        }

        public void setStop(boolean stop) {
            this.stop = stop;
        }
    }

    // setting volatile on object refs does makes the whole object volatile!
    private volatile Stop stopObject = new Stop();
    // Stop stopObject = new Stop();

    @Test
    public void cachedObjectRefs() throws InterruptedException {

        Thread t = new Thread(() -> {
            System.out.println("T started");
            while (!stopObject.isStop()) ; // wait for stop
            System.out.println("T is done");
        });

        t.start();
        Thread.sleep(100); // give t1 some time to start
        System.out.println("stop");
        stopObject.setStop(true);
        t.join();

    }


}
