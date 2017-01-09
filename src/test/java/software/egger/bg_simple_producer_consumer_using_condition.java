package software.egger;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class bg_simple_producer_consumer_using_condition {

    // The producer creates a value. The consumer gets all values created and writes it to the console!

    private volatile int value;

    private volatile boolean stop;

    private volatile boolean valueReady = false;

    @Test
    public void avoidBusyWaitUsingWaitNotify() throws InterruptedException {

        CountDownLatch done = new CountDownLatch(2);

        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();


        Runnable producer = () -> {
            while (!stop) {
                lock.lock();
                try {
                    while (valueReady) {
                        try {
                            condition.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    value++;

                    valueReady = true;
                    condition.signal();
                } finally {
                    lock.unlock();
                }
            }
        };

        Runnable consumer = () -> {
            while (!stop) {
                lock.lock();
                try {
                    while (!valueReady) {
                        try {
                            condition.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    System.out.println(value);

                    valueReady = false;
                    condition.signal();

                } finally {
                    lock.unlock();
                }
            }
        };

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(consumer);
        executorService.submit(producer);

        Thread.sleep(1);

        stop = true;

        executorService.shutdown(); // Sends an interrupt to the threads. Therefore releasing one of the threads.
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }
}
