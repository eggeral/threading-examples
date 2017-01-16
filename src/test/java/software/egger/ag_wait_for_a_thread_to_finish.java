package software.egger;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.fail;

public class ag_wait_for_a_thread_to_finish {

    @Test
    public void simpleJoin() throws InterruptedException {

        Instant start = Instant.now();

        // without join
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("Work done 1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.start();

        System.out.println("Done without join: " + Duration.between(start, Instant.now()));
        // but where is Work done! Intellij kills the thread!!!

        start = Instant.now();

        // with join
        thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("Work done 2");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join();

        System.out.println("Done with join: " + Duration.between(start, Instant.now()));


    }

    @Test
    public void joinWithTimeout() throws InterruptedException {
        Instant start = Instant.now();

        // without join
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
                System.out.println("Work done 1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join(100);
        System.out.println("Thread is alive: " + thread.isAlive());
        System.out.println("Done with join: " + Duration.between(start, Instant.now()));
        System.out.println("Wait for thread to really exit");
        thread.join();
        System.out.println("Thread done: " + Duration.between(start, Instant.now()));

    }
}
