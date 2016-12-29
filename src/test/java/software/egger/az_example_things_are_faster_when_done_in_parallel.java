package software.egger;


import org.junit.Test;
import software.egger.util.ImportantWork;

import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class az_example_things_are_faster_when_done_in_parallel {
    @Test
    public void doSomeImportantWork10Times() {
        Instant start = Instant.now();
        int workCounter = 0;

        for (int idx = 0; idx < 10; idx++) {
            ImportantWork.doWork();
            workCounter++;
        }

        System.out.println("Did some important work " + workCounter + " times.");
        System.out.println("It took: " + Duration.between(start, Instant.now()));
    }

    @Test
    public void doSomeImportantWork10TimesInParallel() throws InterruptedException {
        Instant start = Instant.now();

        Runnable worker = () -> {
            int workCounter = 0;
            for (int idx = 0; idx < 5; idx++) {
                ImportantWork.doWork();
                workCounter++;
            }
            System.out.println("Did some important work " + workCounter + " times.");
        };

        Thread thread1 = new Thread(worker);
        Thread thread2 = new Thread(worker);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("It took: " + Duration.between(start, Instant.now()));
    }

    private int sharedWorkCounter; // This is a field because local vars have to be final in lambdas.

    @Test
    public void workingOnSomethingTogetherIsDangerous() throws InterruptedException {
        sharedWorkCounter = 0;

        Instant start = Instant.now();

        Runnable worker = () -> {
            for (int idx = 0; idx < 5; idx++) {
                ImportantWork.doWork();
                sharedWorkCounter++;
            }
        };

        Thread thread1 = new Thread(worker);
        Thread thread2 = new Thread(worker);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Did some important work " + sharedWorkCounter + " times."); // sharedWorkCounter should be 10 but ...
        System.out.println("It took: " + Duration.between(start, Instant.now()));

    }

    @SuppressWarnings("Duplicates")
    @Test
    public void workingOnSomethingTogetherInASaveWay() throws InterruptedException {
        sharedWorkCounter = 0;

        Object sharedWorkCounterLock = new Object();

        Instant start = Instant.now();

        Runnable worker = () -> {
            synchronized (sharedWorkCounterLock) {
                for (int idx = 0; idx < 5; idx++) {
                    ImportantWork.doWork();
                    sharedWorkCounter++;
                }
            }
        };

        Thread thread1 = new Thread(worker);
        Thread thread2 = new Thread(worker);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Did some important work " + sharedWorkCounter + " times."); // sharedWorkCounter should be 10 but ...
        System.out.println("It took: " + Duration.between(start, Instant.now()));


    }

    @SuppressWarnings("Duplicates")
    @Test
    public void workingOnSomethingTogetherInAFastAndSaveWay() throws InterruptedException {
        sharedWorkCounter = 0;

        Object sharedWorkCounterLock = new Object();

        Instant start = Instant.now();

        Runnable worker = () -> {
            for (int idx = 0; idx < 5; idx++) {
                ImportantWork.doWork();
                synchronized (sharedWorkCounterLock) {
                    sharedWorkCounter++;
                }
            }
        };

        Thread thread1 = new Thread(worker);
        Thread thread2 = new Thread(worker);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Did some important work " + sharedWorkCounter + " times."); // sharedWorkCounter should be 10 but ...
        System.out.println("It took: " + Duration.between(start, Instant.now()));


    }

}
