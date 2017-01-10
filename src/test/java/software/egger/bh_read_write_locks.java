package software.egger;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class bh_read_write_locks {
    static volatile long counter;

    @Test
    public void compareSynchronizedWithReadWriteLocks() {
        // It is said that ReadWriteLocks are better than synchronized because they allow
        // more than one reader if there is no writer.
        // But they are slow!
        Object mutex = new Object();
        measure(() -> {
            synchronized (mutex) {
                counter++;
            }
        });

        assertThat(counter, is(100_000_000L));

        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        measure(() -> {
            readWriteLock.writeLock().lock();
            counter++;
            readWriteLock.writeLock().unlock();
        });

        assertThat(counter, is(100_000_000L));

    }

    private static void measure(Runnable runnable) {
        Instant now = Instant.now();
        List<Thread> threads = new ArrayList<>();
        counter = 0;
        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 1_000_000; j++) {
                    runnable.run();
                }
            });
            threads.add(thread);
            thread.start();
        }
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(Duration.between(now, Instant.now()));
        System.out.println(counter);
        System.out.println("----");
    }
}
