package software.egger;

import org.junit.Test;

import java.util.concurrent.*;

public class br_combining_completable_futures {

    CountDownLatch done = new CountDownLatch(1);

    @Test
    public void combineMultipleFutures() throws ExecutionException, InterruptedException {

        CompletableFuture.supplyAsync(() -> 10L).thenAccept(System.out::println).thenRun(done::countDown);
        System.out.println("---");
        done.await();

        // ..Async methods run the subsequent method in a new thread pool.
        done = new CountDownLatch(1);
        CompletableFuture.supplyAsync(() -> 10L).thenAcceptAsync(System.out::println).thenRun(done::countDown);
        System.out.println("---");
        done.await();

        done = new CountDownLatch(1);
        // passing results from one task to the other
        CompletableFuture.supplyAsync(() -> 10L).thenApply(x -> x * 2).thenAccept(System.out::println).thenRun(done::countDown);
        System.out.println("---");
        done.await();

        done = new CountDownLatch(1);
        CompletableFuture.supplyAsync(() -> 10L).thenRun(() -> System.out.println("TEST")).thenRun(done::countDown);
        System.out.println("---");
        done.await();

        done = new CountDownLatch(1);
        CompletableFuture.supplyAsync(() -> 10L)
                .thenCombine(CompletableFuture.supplyAsync(() -> 15L), (r1, r2) -> r1 + r2)
                .thenAccept(System.out::println)
                .thenRun(done::countDown);
        System.out.println("---");
        done.await();


        // What about exceptions?
        done = new CountDownLatch(1);
        CompletableFuture.supplyAsync(() -> 10L).thenApply(x -> {
            throw new NullPointerException(("BANG"));
        }).handle((r, e) -> {
            if (e != null)
                return e.getMessage();
            return r.toString();
        }).thenAccept(System.out::println)
                .thenRun(done::countDown);
        System.out.println("---");
        done.await();

        done = new CountDownLatch(1);
        CompletableFuture.allOf(
                CompletableFuture.supplyAsync(() -> 10L),
                CompletableFuture.supplyAsync(() -> 20L)
        ).thenRun(() -> System.out.println("Hallo")).thenRun(done::countDown);
        System.out.println("---");
        done.await();

        done = new CountDownLatch(1);
        CompletableFuture.supplyAsync(() -> 10L)
                .applyToEither(CompletableFuture.supplyAsync(() -> 20L),
                        x -> x * 5) // 10 * 5 or 20 * 5 which ever completes first
                .thenAccept(System.out::println)
                .thenRun(done::countDown);
        System.out.println("---");
        done.await();

    }
}
