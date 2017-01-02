package software.egger;

import org.junit.Test;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;

public class ae_sleep {

    @Test
    public void waitUsingALoop() {
        Instant start = Instant.now();
        System.out.println("Start: " + start);
        BigInteger max = new BigInteger("99"); // use more 9s (99999999 ~3 secs on my Macbook) and watch the CPU
        BigInteger idx = BigInteger.ZERO;
        while (!idx.equals(max)) {
            idx = idx.add(BigInteger.ONE);
        }
        System.out.println("End: " + Duration.between(start, Instant.now()));
    }

    @Test
    public void threadSleep() {
        Instant start = Instant.now();
        System.out.println("Start: " + start);
        try {
            Thread.sleep(100); // this is not exact
        } catch (InterruptedException e) {
            e.printStackTrace(); // we will see later why we need this
        }
        System.out.println("End: " + Duration.between(start, Instant.now()));
    }

    @Test
    public void threadSleepWithNanos() {
        Instant start = Instant.now();
        System.out.println("Start: " + start);
        try {
            Thread.sleep(1, 10); // this is not exact
        } catch (InterruptedException e) {
            e.printStackTrace(); // we will see later why we need this
        }
        System.out.println("End: " + Duration.between(start, Instant.now()));
    }

}
