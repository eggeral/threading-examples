package software.egger.message06;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.concurrent.locks.StampedLock;

public class Sensor implements Runnable {

    private StampedLock lock = new StampedLock();

    private volatile double channel1;
    private volatile double channel2;

    private volatile boolean stop = false;

    @Override
    public void run() {

        while (!stop) {
            long stamp = lock.writeLock();
            double base = Instant.now().getLong(ChronoField.MILLI_OF_SECOND) / 1000.0;
            try {
                channel1 = Math.sin(base * 2 * Math.PI);
                channel2 = Math.sin(base * 3 * Math.PI) * 2;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                lock.unlockWrite(stamp);
            }
        }

    }

    public void stop() {
        stop = true;
    }


    public double getValue() {
        long stamp = lock.tryOptimisticRead();
        double currentC1 = channel1;
        double currentC2 = channel2;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                currentC1 = channel1;
                currentC2 = channel2;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return currentC1 + currentC2;
    }
}
