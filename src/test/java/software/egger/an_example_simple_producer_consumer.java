package software.egger;

import org.junit.Test;

@SuppressWarnings("Duplicates")
public class an_example_simple_producer_consumer {

    // The producer creates a value. The consumer gets all values created and writes it to the console!

    private volatile int value;

    private volatile boolean stop;

    @Test // This is our completely wrong starting point.
    public void producesConsumerWithoutWaitNotify() throws InterruptedException {

        value = 0;
        stop = false;

        Thread producer = new Thread(() -> {
            while (!stop) {
                value++;
            }
        });

        Thread consumer = new Thread(() -> {
            while (!stop) {
                System.out.println(value);
            }
        });

        producer.start();
        consumer.start();
        Thread.sleep(1);
        stop = true;
        producer.join();
        consumer.join();

    }

    private volatile boolean valueReady = false;

    @Test
    public void makeSureTheConsumerGetsAllValues() throws InterruptedException {

        value = 0;
        stop = false;
        valueReady = false;

        Thread producer = new Thread(() -> {
            while (!stop) {
                if (!valueReady) {
                    value++;
                    valueReady = true;
                }
            }
        });

        Thread consumer = new Thread(() -> {
            while (!stop) {
                if (valueReady) {
                    //       if (value % 1000 == 0)
                    System.out.println(value);
                    valueReady = false;
                }
            }
        });

        producer.start();
        consumer.start();
        Thread.sleep(1); // wait for longer (60000) to see busy wait loop in processor monitor
        stop = true;
        producer.join();
        consumer.join();

    }

    @Test
    public void avoidBusyWaitUsingWaitNotify() throws InterruptedException {
        value = 0;
        stop = false;
        valueReady = false;
        Object lock = new Object();

        Thread producer = new Thread(() -> {
            while (!stop) {
                synchronized (lock) {
                    while (valueReady) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    value++;

                    valueReady = true;
                    lock.notify();
                }
            }
        });

        Thread consumer = new Thread(() -> {
            while (!stop) {
                synchronized (lock) {
                    while (!valueReady) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    // if (value % 1000 == 0)
                        System.out.println(value);

                    valueReady = false;
                    lock.notify();
                }

            }
        });

        producer.start();
        consumer.start();
        Thread.sleep(1); // wait for longer (60000) to see busy wait loop in processor monitor
        stop = true;
        producer.join();
        consumer.join();
    }
}
