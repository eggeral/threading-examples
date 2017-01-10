package software.egger;

import org.junit.Test;

import java.util.Random;
import java.util.Stack;
import java.util.concurrent.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class bj_blocking_queue {

    private volatile boolean done;

    @Test
    public void blockingQueueProducerConsumer() throws InterruptedException {

        done = false;

        BlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(10);

        Runnable producer = () -> {
            Random rnd = new Random();
            try {
                while (!done) {
                    int item = rnd.nextInt(1000);
                    Thread.sleep(100);
                    buffer.put(item); // put an item into the queue. Wait for space to become available.
                    System.out.println("Produced and pushed: " + item + ", buffer has " + buffer.size() + " elements.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable consumer = () -> {
            try {
                while (!done) {
                    int item = buffer.take(); // Take an item from the queue. Wait for item to be available
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

        Thread.sleep(1_000);

        done = true;

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }


}
