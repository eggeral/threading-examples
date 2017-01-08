package software.egger;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ba_cyclic_barrier {

    @Test
    public void waitForOtherThreadsToReachTheBarrier() throws InterruptedException, ExecutionException, BrokenBarrierException {

        ExecutorService executorService = Executors.newCachedThreadPool();

        CyclicBarrier barrier = new CyclicBarrier( // used to split up work and wait for the workers to finish their calculation.
                5, // we have to know how many threads are going to arrive.
                () -> System.out.println("All threads reached the barrier") // (optional) executed before! the other threads continue
        );

        Runnable task = () -> {
            try {
                Random rnd = new Random();
                Thread.sleep(rnd.nextInt(100));
                System.out.println("Waiting for all threads to arrive at the barrier. Threads already waiting: " + barrier.getNumberWaiting());
                int number = barrier.await();
                System.out.println("Barrier tripped. I was number: " + number);
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        };

        executorService.submit(task);
        executorService.submit(task);
        executorService.submit(task);
        executorService.submit(task);

        System.out.println("Main thread waits for barrier.");
        int number = barrier.await();
        System.out.println("Main thread continues. Was " + number + " at the barrier.");

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }


    @Test
    public void otherThanCountDownLatchesCyclicBarriersCabBeReset() throws InterruptedException, ExecutionException, BrokenBarrierException {

        ExecutorService executorService = Executors.newCachedThreadPool();

        CyclicBarrier barrier = new CyclicBarrier( // used to split up work and wait for the workers to finish their calculation.
                3, // we have to know how many threads are going to arrive.
                () -> System.out.println("All threads reached the barrier") // (optional) executed before! the other threads continue
        );

        Runnable task = () -> {
            try {
                Random rnd = new Random();
                Thread.sleep(rnd.nextInt(100));
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        };

        executorService.submit(task);
        executorService.submit(task);

        System.out.println("Main thread waits for barrier.");
        barrier.await();
        System.out.println("Main thread continues");

        System.out.println("== And again");
        barrier.reset();

        executorService.submit(task);
        executorService.submit(task);

        System.out.println("Main thread waits for barrier.");
        barrier.await();
        System.out.println("Main thread continues");

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }

    // Leaving out details like handling of exceptions, await timeout, interruptions.

}
