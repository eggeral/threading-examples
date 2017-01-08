package software.egger;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.Random;
import java.util.Stack;
import java.util.concurrent.*;

public class bd_semaphore {

    int counter = 0;

    @Test
    public void mutex() throws InterruptedException {

        Semaphore mutex = new Semaphore(1); // a semaphore with one permit is a mutex

        CountDownLatch done = new CountDownLatch(2);
        // good old increment example
        Runnable incrementer = () -> {
            try {
                for (int idx = 0; idx < 10_000_000; idx++) {
                    mutex.acquire();
                    counter++;
                    mutex.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            done.countDown();
        };

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(incrementer);
        executorService.submit(incrementer);

        done.await();

        assertThat(counter, is(20_000_000));
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }

    private volatile boolean done;

    @Test
    public void semaphoreSimpleProducerConsumer() throws InterruptedException {

        done = false;

        Semaphore fillCount = new Semaphore(0); // items currently produced
        Semaphore emptyCount = new Semaphore(10); // remaining buffer space

        Stack<Integer> buffer = new Stack<>();

        Runnable producer = () -> {
            Random rnd = new Random();
            try {
                while (!done) {
                    int item = rnd.nextInt(1000);
                    Thread.sleep(100);
                    emptyCount.acquire();
                    buffer.push(item);
                    System.out.println("Produced and pushed: " + item + ", buffer has " + buffer.size() + " elements.");
                    fillCount.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable consumer = () -> {
            try {
                while (!done) {
                    fillCount.acquire();
                    int item = buffer.pop();
                    emptyCount.release();
                    System.out.println("Consumed: " + item + ", buffer has " + buffer.size() + " elements.");
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };


        // Note that we have never more than 10 items in the buffer!

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(producer);
        executorService.submit(consumer);

        Thread.sleep(30_000);

        done = true;

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }


}
