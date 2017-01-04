package software.egger;

import org.junit.Test;

import java.util.concurrent.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ax_countdown_latches {

    @Test
    public void waitForALatchToBecomeZero() throws InterruptedException, ExecutionException {

        ExecutorService executorService = Executors.newCachedThreadPool();

        CountDownLatch latch = new CountDownLatch(2);
        Future<Integer> result = executorService.submit(() -> {
            System.out.println("Waiting for latch to become 0");
            latch.await();
            System.out.println("Latch is now 0");
            return 42;
        });

        System.out.println("Latch count: " + latch.getCount());
        latch.countDown();
        System.out.println("Latch count: " + latch.getCount());
        latch.countDown();

        assertThat(result.get(), is(42)); // waits for the thread to finish and returns the result.

        executorService.shutdownNow();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }

    @Test
    public void waitWithTimeoutForALatchToBecomeZero() throws InterruptedException, ExecutionException {

        ExecutorService executorService = Executors.newCachedThreadPool();

        CountDownLatch latch = new CountDownLatch(2);
        Future<Integer> result = executorService.submit(() -> {
            System.out.println("Waiting for latch to become 0");
            if (!latch.await(10, TimeUnit.MILLISECONDS))
                System.out.println("Timeout");
            else
                System.out.println("Latch is now 0");
            return 42;
        });

        System.out.println("Latch count: " + latch.getCount());
        latch.countDown();
        Thread.sleep(50);
        System.out.println("Latch count: " + latch.getCount());
        latch.countDown();

        assertThat(result.get(), is(42)); // waits for the thread to finish and returns the result.

        executorService.shutdownNow();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }


}
