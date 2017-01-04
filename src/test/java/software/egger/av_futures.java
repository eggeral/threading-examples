package software.egger;

import org.junit.Test;

import java.util.concurrent.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class av_futures {

    @Test
    public void aFutureReturnsTheResultOfACalculationPerformedByAThread() throws InterruptedException, ExecutionException {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<Integer> calculate = () -> { // Note that we use Callable not Runnable
            Thread.sleep(100);
            return 42;
        };

        Future<Integer> result = executorService.submit(calculate);
        assertThat(result.get(), is(42)); // waits for the thread to finish and returns the result.

        executorService.shutdownNow();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }

    @Test
    public void aFutureCanBeAskedIfItIsDone() throws InterruptedException, ExecutionException {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<Integer> calculate = () -> { // Note that we use Callable not Runnable
            Thread.sleep(100);
            return 42;
        };

        Future<Integer> result = executorService.submit(calculate);
        while (!result.isDone()) {
            Thread.sleep(10);
            System.out.println("waiting");
        }
        assertThat(result.get(), is(42)); // waits for the thread to finish and returns the result.

        executorService.shutdownNow();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }

    @Test
    public void futuresWithExceptions() throws InterruptedException {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<Integer> result = executorService.submit(() -> 10 / 0);
        try {
            result.get();
            fail("Expected exception! But there was none!");
        } catch (ExecutionException ex) {
            System.out.println("Got exception: " + ex.getMessage() + ", cause was: " + ex.getCause().getMessage());
        }

        executorService.shutdownNow();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }

    private Integer calculate(String name, int result) {

        System.out.println(name + " executing");
        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            System.out.println(name + " interrupted");
        }
        System.out.println(name + " done");

        return result;

    }

    @Test
    public void cancelAFuture() throws InterruptedException {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<Integer> result1 = executorService.submit(() -> calculate("t1", 42));
        Future<Integer> result2 = executorService.submit(() -> calculate("t2", 73));
        Future<Integer> result3 = executorService.submit(() -> calculate("t3", 21));

        // Note: t1 is executed. t2 has to wait because we use a single thread executor.
        System.out.println("cancel t2: " + result2.cancel(false)); // t1 is executed currently. t2 will never get executed
        Thread.sleep(100);
        System.out.println("cancel t1: " + result1.cancel(true)); // Cancel and interrupt t1
        Thread.sleep(100);
        System.out.println("cancel t3: " + result3.cancel(false)); // t3 is already running. Nothing happens.
        Thread.sleep(100);
        System.out.println("cancel t3: " + result3.cancel(true)); // Sorry t3 already canceled. Nothing happens :-(
        Thread.sleep(100);

        System.out.println("Checking future states");

        assertThat(result1.isCancelled(), is(true));
        assertThat(result2.isCancelled(), is(true));
        assertThat(result3.isCancelled(), is(true));

        assertThat(result1.isDone(), is(true));
        assertThat(result2.isDone(), is(true));
        assertThat(result3.isDone(), is(true));

        executorService.shutdownNow();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

        // t3 outputs interrupted and done because it is interrupted by shutdownNow()

    }

    @Test
    public void getWithTimeout() throws InterruptedException, ExecutionException {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<Integer> calculate = () -> { // Note that we use Callable not Runnable
            Thread.sleep(100);
            return 42;
        };

        Future<Integer> result = executorService.submit(calculate);

        try {
            result.get(10, TimeUnit.MILLISECONDS);
            fail("Expected timeout exception!");
        } catch (TimeoutException e) {
            System.out.println("Got expected exception");
        }

        executorService.shutdownNow();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }

    @Test
    public void scheduledFuture() throws InterruptedException, ExecutionException {

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

        ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate( // sorry no return values (only for schedule(...))
                () -> System.out.println("Tick"),
                0,
                100,
                TimeUnit.MILLISECONDS
        );

        // scheduledFuture.get(); // this blocks until the task is done. In our case never!!!
        Thread.sleep(500);

        scheduledFuture.cancel(false); // stop scheduling new tasks.

        // scheduledFuture.get(); // throws exception because we are already canceled

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

    }

}
