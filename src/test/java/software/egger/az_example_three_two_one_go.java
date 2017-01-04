package software.egger;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class az_example_three_two_one_go {

    // Runners run and measure the time they need to complete 100 cycles. The main tread decides who is the winner.
    // Make sure all runners a ready before starting the countdown

    @SuppressWarnings("Duplicates")
    @Test
    public void olympicHundredMeterFinal() throws InterruptedException, ExecutionException {

        int numberOfRunners = 8;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRunners); // What happens if we do not have enough thread?

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch runnersReady = new CountDownLatch(numberOfRunners);

        List<Future<Duration>> runners = new ArrayList<>();

        for (int runnerNumber = 1; runnerNumber <= numberOfRunners; runnerNumber++) {
            int number = runnerNumber;
            runners.add(executorService.submit(() -> {
                Random rnd = new Random();
                Thread.sleep(100); // prepare for run.
                System.out.println("Runner " + number + " ready.");
                runnersReady.countDown();
                startLatch.await();
                Instant start = Instant.now();
                System.out.println("Runner " + number + " started.");
                for (int i = 0; i < 100; i++) {
                    // Busy running ;-)
                    Thread.sleep(rnd.nextInt(20));
                }
                System.out.println("Runner " + number + " finished.");
                return Duration.between(start, Instant.now());
            }));
        }

        System.out.println("On your marks");
        System.out.println("Ready");
        runnersReady.await();
        System.out.println("!!BANG!!");
        startLatch.countDown();

        int winner = -1;
        Duration winnerDuration = Duration.ZERO;

        for (int runnerNumber = 1; runnerNumber <= numberOfRunners; runnerNumber++) {
            Future<Duration> runner = runners.get(runnerNumber - 1);
            Duration duration = runner.get();

            System.out.println("Runner: " + runnerNumber + " needed: " + duration);

            if (winner < 0 || duration.compareTo(winnerDuration) < 0) {
                winner = runnerNumber;
                winnerDuration = duration;
            }
        }

        System.out.println("The winner is runner: " + winner + ", it took: " + winnerDuration);

        executorService.shutdownNow();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }

}
