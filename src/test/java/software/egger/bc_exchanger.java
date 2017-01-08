package software.egger;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;

public class bc_exchanger {

    // exchangers can be used for buffer or pipeline designs (ls -l | grep "ab" | wc -l)
    // pipelines sometimes do not really exchange objects but pass them to the next stage in the pipeline.

    // one exchanger fills up a buffer the other empties the buffer. If both are ready the exchange to buffer.

    // bartender - guest example

    static class Glass {
        boolean[] filled = new boolean[10];
        String name;

        @Override
        synchronized public String toString() {

            String filledStr = "";

            int idx = 0;
            while (idx < filled.length && filled[idx]) {
                filledStr += "o";
                idx++;
            }

            return "Glass{" +
                    "owner='" + owner + '\'' +
                    ", name='" + name + '\'' +
                    ", filled=[" + filledStr + "]" +
                    '}';
        }

        String owner;
    }

    volatile boolean done;

    @Test
    public void exchangersExchangeObjectsAtSyncPoints() throws InterruptedException {

        done = false;

        ExecutorService executorService = Executors.newCachedThreadPool();

        Exchanger<Glass> exchange = new Exchanger<>();

        Runnable bartender = () -> {
            Glass glassToFill = new Glass();
            glassToFill.name = "A";
            glassToFill.owner = "Bartender";
            while (!done) {
                try {
                    System.out.println(glassToFill.toString());
                    int idx = 0;
                    while (idx < glassToFill.filled.length && glassToFill.filled[idx] == true) {
                        idx++;
                    }
                    if (idx >= glassToFill.filled.length) {
                        System.out.println("Bartender waits for glass to fill");
                        glassToFill = exchange.exchange(glassToFill);
                        glassToFill.owner = "Bartender";
                    } else {
                        glassToFill.filled[idx] = true;
                    }
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable guest = () -> {
            Glass glassToEmpty = new Glass();
            glassToEmpty.name = "B";
            glassToEmpty.owner = "Guest";
            while (!done) {
                try {
                    System.out.println(glassToEmpty.toString());
                    int idx = glassToEmpty.filled.length - 1;
                    while (idx >= 0 && !glassToEmpty.filled[idx]) {
                        idx--;
                    }
                    if (idx < 0) {
                        System.out.println("Guest waits for glass to empty");
                        glassToEmpty = exchange.exchange(glassToEmpty);
                        glassToEmpty.owner = "Guest";
                    } else {
                        glassToEmpty.filled[idx] = false;
                    }
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };


        executorService.submit(bartender);
        executorService.submit(guest);

        Thread.sleep(1000);

        done = true;

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }

    // Leaving out details like handling of exceptions, await timeout, interruptions.

}
