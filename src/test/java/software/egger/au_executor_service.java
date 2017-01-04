package software.egger;

import org.junit.Ignore;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

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

    @Test
    public void scheduledExecutorService() throws InterruptedException {

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

        executorService.scheduleAtFixedRate(
                () -> System.out.println("Tick"),
                0,
                100,
                TimeUnit.MILLISECONDS
        );

        Thread.sleep(500);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

    }

    private int count = 0;

    @Test
    public void scheduledExecutorServiceWithException() throws InterruptedException {

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

        // Tasks are not scheduled again if the throw an exception!
        executorService.scheduleAtFixedRate(
                () -> {
                    System.out.println("Tick");
                    if (count > 1)
                        throw new IllegalStateException("KABOOM");
                    count++;
                },
                0,
                100,
                TimeUnit.MILLISECONDS
        );

        Thread.sleep(500);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

    }

    @Test
    public void invokeAllRunsAllCallablesAndWaitsForThemToFinish() throws InterruptedException, ExecutionException {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

        List<Callable<Integer>> tasks = new ArrayList<>();

        for (int idx = 0; idx < 10; idx++) {
            int number = idx;
            tasks.add(() -> {
                Random rnd = new Random();
                Thread.sleep(100);
                System.out.println("Number: " + number + " is done.");
                return rnd.nextInt(1000);
            });
        }

        System.out.println("== Starting tasks");
        List<Future<Integer>> results = executorService.invokeAll(tasks);
        System.out.println("== All tasks done");

        for (Future<Integer> result : results) {
            System.out.println("Result: " + result.get());
            System.out.println("Done: " + result.isDone());
            System.out.println("Canceled: " + result.isCancelled());
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

    }

    // See executors class for different types of thread pools etc.


}
