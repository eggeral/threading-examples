package software.egger;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ar_timer {

    @Test
    public void executeWithDelay() throws InterruptedException {

        Timer timer = new Timer("my timer");

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Task executed");
            }
        };

        timer.schedule(task, 1000);

        for (int idx = 0; idx < 15; idx++) {
            System.out.println(idx);
            Thread.sleep(100);
        }

    }

    @Test
    public void executeAtFixedTime() throws InterruptedException {

        Timer timer = new Timer("my timer");

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Task executed");
            }
        };

        Instant timeToExecute = Instant.now().plusSeconds(1);
        System.out.println("Schedule at: " + timeToExecute);
        timer.schedule(task, Date.from(timeToExecute));

        for (int idx = 0; idx < 15; idx++) {
            System.out.println(idx + ": " + Instant.now());
            Thread.sleep(100);
        }

        // It the time is in the past. The task executes immediately.
        System.out.println("==");

        // NOTE: A task can only be scheduled once!
        task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("New task executed");
            }
        };

        timeToExecute = Instant.now().minusSeconds(1);
        timer.schedule(task, Date.from(timeToExecute));

        Thread.sleep(100); // give the task some time to execute

    }

    @SuppressWarnings("Duplicates")
    @Test
    public void executeAtFixedRate() throws InterruptedException {

        // Schedule at a fixed rate.
        // The system tries to catch up if execution times were missed.

        Timer timer = new Timer("my timer");

        Instant start = Instant.now();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("* " + Duration.between(start, Instant.now()) + ": Task executed");
            }
        };

        timer.scheduleAtFixedRate(task, 200, 500);

        for (int idx = 0; idx < 16; idx++) {
            System.out.println(Duration.between(start, Instant.now()) + ": " + idx);
            Thread.sleep(100);
        }


    }


    @SuppressWarnings("Duplicates")
    @Test
    public void executeAtFixedRateInThePast() throws InterruptedException {

        // Schedule at a fixed rate.
        // The system tries to catch up if execution times were missed.

        Timer timer = new Timer("my timer");

        Instant start = Instant.now();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("* " + Duration.between(start, Instant.now()) + ": Task executed");
            }
        };

        Instant timeToExecute = Instant.now().minusSeconds(2);

        timer.scheduleAtFixedRate(task, Date.from(timeToExecute), 500);

        for (int idx = 0; idx < 16; idx++) {
            System.out.println(Duration.between(start, Instant.now()) + ": " + idx);
            Thread.sleep(100);
        }


    }

    private int counter;

    @SuppressWarnings("Duplicates")
    @Test
    public void delayedFixedRate() throws InterruptedException {

        // Schedule at a fixed rate.
        // The system tries to catch up if execution times were missed.

        counter = 0;
        Timer timer = new Timer("my timer");

        Instant start = Instant.now();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("* " + Duration.between(start, Instant.now()) + ": Task executed");
                try {
                    if (counter == 1)
                        Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                counter ++;
            }
        };

        // missed times are executed as soon as possible
        timer.scheduleAtFixedRate(task, 0, 100);

        for (int idx = 0; idx < 16; idx++) {
            System.out.println(Duration.between(start, Instant.now()) + ": " + idx);
            Thread.sleep(50);
        }


    }



    @SuppressWarnings("Duplicates")
    @Test
    public void executeWithFixedDelay() throws InterruptedException {

        // Schedule at a fixed delay.
        // The system tries to keep the delay.

        Timer timer = new Timer("my timer");

        Instant start = Instant.now();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("* " + Duration.between(start, Instant.now()) + ": Task executed");
            }
        };

        timer.schedule(task, 200, 500);

        for (int idx = 0; idx < 16; idx++) {
            System.out.println(Duration.between(start, Instant.now()) + ": " + idx);
            Thread.sleep(100);
        }


    }


    @SuppressWarnings("Duplicates")
    @Test
    public void executeWithDelayInThePast() throws InterruptedException {

        Timer timer = new Timer("my timer");

        Instant start = Instant.now();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("* " + Duration.between(start, Instant.now()) + ": Task executed");
            }
        };

        Instant timeToExecute = Instant.now().minusSeconds(2);

        // other than on fixed rate missed tasks are not executed
        timer.schedule(task, Date.from(timeToExecute), 500);

        for (int idx = 0; idx < 16; idx++) {
            System.out.println(Duration.between(start, Instant.now()) + ": " + idx);
            Thread.sleep(100);
        }


    }


    @SuppressWarnings("Duplicates")
    @Test
    public void delayedExecuteWithDelay() throws InterruptedException {

        counter = 0;
        Timer timer = new Timer("my timer");

        Instant start = Instant.now();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("* " + Duration.between(start, Instant.now()) + ": Task executed");
                try {
                    if (counter == 1)
                        Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                counter ++;
            }
        };

        // next tasks is scheduled after the last task has finished.
        timer.schedule(task, 0, 100);

        for (int idx = 0; idx < 16; idx++) {
            System.out.println(Duration.between(start, Instant.now()) + ": " + idx);
            Thread.sleep(50);
        }

    }

    @SuppressWarnings("Duplicates")
    @Test
    public void timersCanBeCanceled() throws InterruptedException {

        Timer timer = new Timer("my timer");

        Instant start = Instant.now();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("* " + Duration.between(start, Instant.now()) + ": Task executed");
            }
        };

        timer.scheduleAtFixedRate(task, 200, 500);

        for (int idx = 0; idx < 16; idx++) {
            System.out.println(Duration.between(start, Instant.now()) + ": " + idx);
            if (idx == 8)
                timer.cancel();
            Thread.sleep(100);
        }

    }

    @SuppressWarnings("Duplicates")
    @Test
    public void timerTasksCanBeCanceled() throws InterruptedException {

        Timer timer = new Timer("my timer");

        Instant start = Instant.now();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("* " + Duration.between(start, Instant.now()) + ": Task executed");
            }
        };

        timer.scheduleAtFixedRate(task, 200, 500);

        for (int idx = 0; idx < 16; idx++) {
            System.out.println(Duration.between(start, Instant.now()) + ": " + idx);
            if (idx == 8)
                task.cancel();
            Thread.sleep(100);
        }

    }

}
