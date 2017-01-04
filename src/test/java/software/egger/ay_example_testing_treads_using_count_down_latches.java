package software.egger;

import org.junit.Test;

import java.util.concurrent.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ay_example_testing_treads_using_count_down_latches {

    private volatile boolean stop;

    @Test
    public void cachedVariables() throws InterruptedException {

        CountDownLatch threadStarted = new CountDownLatch(1);
        CountDownLatch threadGotStop = new CountDownLatch(1);


        Thread t = new Thread(() -> {
            System.out.println("T started");
            threadStarted.countDown();
            while (!stop) ; // wait for stop
            threadGotStop.countDown();
            System.out.println("T is done");
        });

        t.start();
        System.out.println("Wait for t to start");
        threadStarted.await();
        stop = true;
        threadGotStop.await();
        t.join();

    }

}
