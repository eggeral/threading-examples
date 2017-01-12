package software.egger;

import org.junit.Test;

import java.util.concurrent.*;

public class bq_completable_future {

    @Test
    public void whenFutureDoneDoX() throws ExecutionException, InterruptedException {
        // Until Java 8 there was no easy way of saying to a Future. When done then do X
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Future<Long> worker1 = executor.submit(() -> doWork("w1", 2));
        Future<Long> worker2 = executor.submit(() -> doWork("w2", 3));

        System.out.println("sum = " + (worker1.get() + worker2.get()));
        executor.shutdown();
        System.out.println("---");

        CompletableFuture future = CompletableFuture.supplyAsync(() -> doWork("w1", 99));
        // normally there is no need to wait for a completable future! But we do it here as part of the example
        future.get();
        System.out.println("---");

        CountDownLatch done = new CountDownLatch(1);
        future = CompletableFuture.supplyAsync(
                () -> doWork("w1", 12)
        ).thenAcceptBoth(CompletableFuture.supplyAsync(
                        () -> doWork("w2", 23)), // if w2 and w1 are done add the results
                        (result1, result2) -> {
                            System.out.println("sum = " + (result1 + result2));
                            done.countDown();
                        });
        done.await();
    }

    private static Long doWork(String worker, long result) {
        System.out.println(worker + " start");
        sleep(1000);
        System.out.println(worker + " end");
        return result;
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
