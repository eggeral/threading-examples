package software.egger;

import org.junit.Test;

public class af_interrupting_threads {

    @Test
    public void simpleInterrupt() throws InterruptedException {

        Thread thread = new Thread(() -> {

            while (!Thread.interrupted()) {
                for (long idx = 0; idx < 999999999; idx++) ; // sleep loop ( Thread.sleep avoided on purpose because of InterruptedException. Will show sleep way later!
                System.out.println("Tick");
            }

        });

        thread.start();
        Thread.sleep(1000);
        thread.interrupt();

    }

    @Test
    public void interruptClearsTheInterruptedFlag() throws InterruptedException {

        Thread thread = new Thread(() -> {

            while (!Thread.interrupted()) {
                for (long idx = 0; idx < 999999999; idx++) ;
                System.out.println("Tick1: " + Thread.currentThread().isInterrupted()); // Thread.interrupted clears the interrupted flag!
            }

            while (!Thread.interrupted()) {
                for (long idx = 0; idx < 999999999; idx++) ;
                System.out.println("Tick2: " + Thread.currentThread().isInterrupted()); // Thread.interrupted clears the interrupted flag!
            }

        });

        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
        Thread.sleep(1000);
        thread.interrupt();

    }

    @Test
    public void interruptingSleepingThread() {

        Thread thread = new Thread(() -> {

            try {
                Thread.sleep(10000);
                System.out.println("done");
            } catch (InterruptedException e) {
                System.out.println("interrupted");
                System.out.println("isInterrupted(): " + Thread.currentThread().isInterrupted());
                System.out.println("interrupted(): " + Thread.interrupted());
            }


        });

        thread.start();
        thread.interrupt();

    }
}
