package software.egger;

import org.junit.Test;

import java.util.Random;
import java.util.Stack;
import java.util.concurrent.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class be_phaser {


    // A Phaser is a generic Cyclic Barrier. The number of threads arriving can be changed.
    int counter = 0;

    @Test
    public void threadWorkSplitIntoPhases() throws InterruptedException {

        Phaser phaser = new Phaser();

        Runnable worker = () -> {
            System.out.println(Thread.currentThread().getName() + " waiting for all others to arrive.");

            int phase = phaser.arriveAndAwaitAdvance();
            System.out.println(Thread.currentThread().getName() + " arrived at phase: " + phase);

            phase = phaser.arriveAndAwaitAdvance();
            System.out.println(Thread.currentThread().getName() + " arrived at phase: " + phase);

        };

        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int idx = 0; idx < 5; idx++) {
            int phase = phaser.register();
            System.out.println("== Registered at phase: " + phase);
            executorService.submit(worker);
        }

        System.out.println("== Main thread  done");

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }

}
