package software.egger;

import org.junit.Ignore;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class au_executor_service {

    @Test(expected = OutOfMemoryError.class)
    @Ignore // Leaves system in out of memory state. Do not execute together with other tests.
    public void executeALotOfThreads() throws InterruptedException {

        List<Thread> threads = new ArrayList<>();

        for (int idx = 0; idx < 3_000; idx++) {
            int num = idx;
            Thread thread = new Thread(() -> {
                System.out.println(num);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

    }


    @SuppressWarnings("Duplicates")
    @Test(expected = OutOfMemoryError.class)
    @Ignore // Leaves system in out of memory state. Do not execute together with other tests.
    public void executeALotOfThreadsWithCachedThreadPool() throws InterruptedException {

        ExecutorService executorService = Executors.newCachedThreadPool(); // Create new threads as needed. Reuse threads. Terminate thread if not used for 60sec

        for (int idx = 0; idx < 3_000; idx++) {
            int num = idx;
            executorService.submit(() -> {
                System.out.println(num);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown(); // execute all scheduled tasks but do not schedule new ones.
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }


    @SuppressWarnings("Duplicates")
    @Test
    public void executeALotOfThreadsWithExecutorService() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(1000);

        for (int idx = 0; idx < 3_000; idx++) {
            int num = idx;
            executorService.submit(() -> {
                System.out.println(num);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown(); // execute all scheduled tasks but do not schedule new ones.
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }

    @Test
    public void shutDownTheExecutorServiceNow() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(1000);

        executorService.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Task interrupted");
            }
        });


        executorService.shutdownNow(); // returns a list of tasks not executed.
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }

    // See executors class for different types of thread pools etc.


}
