package software.egger;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

public class at_example_fixed_rate_timer_but_without_catch_up {

    // example where tasks do not queue up when they are executed periodically
    // but they take longer than the period.
    private volatile boolean inProgress = false;

    @Test
    public void delayedFixedRate() throws InterruptedException {

        // Schedule at a fixed rate.

        Object lock = new Object();

        Timer timer = new Timer("my timer");

        Instant start = Instant.now();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                synchronized (lock) {
                    if (inProgress) {
                        System.out.println("Skipping this run! Operation in progress.");
                        return;
                    }
                    inProgress = true;
                    Thread thread = new Thread(() -> {
                        System.out.println("* " + Duration.between(start, Instant.now()) + ": Task executed");
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        synchronized (lock) {
                            inProgress = false;
                        }
                        System.out.println("Task done");
                    });
                    thread.start();
                }
            }
        };

        // missed times are executed as soon as possible
        timer.scheduleAtFixedRate(task, 0, 100);

        for (int idx = 0; idx < 16; idx++) {
            System.out.println(Duration.between(start, Instant.now()) + ": " + idx);
            Thread.sleep(50);
        }


    }


}
