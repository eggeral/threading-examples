package software.egger;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class ao_example_producer_consumer_queue_with_multiple_consumers {

    private volatile int value;

    private volatile boolean stop;
    private volatile boolean valueReady = false;

    @Test
    public void thereAreSeveralConsumerThreadsInOrderToGetABetterThroughput() throws InterruptedException {
        // all consumers are doing the same. If an item is ready only the first consumer gets the item.

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
                    lock.notifyAll();
                }
            }
        });

        Runnable worker = () -> {
            while (!stop) {
                synchronized (lock) {
                    while (!valueReady) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    System.out.println(Thread.currentThread().getName() + ": " + value);

                    valueReady = false;
                    lock.notify();
                }

            }
        };

        producer.start();
        Thread consumer1 = new Thread(worker);
        Thread consumer2 = new Thread(worker);

        consumer1.start();
        consumer2.start();

        Thread.sleep(1); // wait for longer (60000) to see busy wait loop in processor monitor
        stop = true;
        producer.join();
        consumer1.join();
        consumer2.join();
    }


    @Test
    public void ifSeveralConsumersShouldGetTheSameValueWeUseOnConsumerToDistributeTheWork() throws InterruptedException {
        // all consumers are doing the same. If an item is ready only the first consumer gets the item.

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
                    lock.notifyAll();
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

                    // this is simplified!!! We would need a 1 to 1 producer consumer pattern here.
                    List<Thread> threads = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        Thread thread = new Thread(() ->
                                System.out.println(Thread.currentThread().getName() + ": " + value)
                        );
                        thread.start();
                    }

                    for (Thread thread : threads) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    valueReady = false;
                    lock.notify();
                }

            }
        });

        producer.start();
        consumer.start();

        Thread.sleep(5); // wait for longer (60000) to see busy wait loop in processor monitor
        stop = true;
        producer.join();
        consumer.join();
    }


}
