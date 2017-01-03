package software.egger;

import java.util.Timer;
import java.util.TimerTask;

public class as_example_progress_bar_with_timer {

    static void doWork() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    // execute in Terminal! \b does not work in IntelliJ console output.
    // cd build/classes/test
    // java -cp . software.egger.as_example_progress_bar_with_timer
    public static void main(String[] args) throws InterruptedException {

        Timer animationTimer = new Timer();

        TimerTask animate = new TimerTask() {

            int state = 0;
            String[] frames = new String[]{"-\b", "\\\b", "/\b", "|\b"};

            @Override
            public void run() {
                System.out.print(frames[state]);
                if (state < frames.length - 1)
                    state++;
                else
                    state = 0;
            }
        };

        Thread thread = new Thread(() -> {
            animationTimer.schedule(animate, 0, 50);
            for (int idx = 0; idx < 10; idx++) {
                System.out.print("=");
                doWork();
            }
            animate.cancel();
        });

        System.out.println("START");
        thread.start();
        thread.join();
        System.out.print(" ");
        System.out.println();
        System.out.println("DONE");

        animationTimer.cancel(); // make sure the timer is canceled. Otherwise a thread is kept alive and the JVM does not exit.

    }

}
