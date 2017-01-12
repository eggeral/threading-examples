package software.egger;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

public class bp_adder_and_accumulator {
    private static AtomicLong atomic = new AtomicLong();
    private static LongAdder adder = new LongAdder();
    private static LongAccumulator accumulator = new LongAccumulator((prev, current) -> prev + current, 0);

    @Test
    public void atomicAddAndAccumulateMethods() throws InterruptedException {
        // Atomics have bad performance when used with a lot of threads
        List<Thread> threads = new ArrayList<>();
        Instant start = Instant.now();
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 1_000_000; j++) {
                    atomic.incrementAndGet();
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println(Duration.between(start, Instant.now()).toMillis());
        System.out.println(atomic.get());

        // Use Adder of Accumulator if you have a lot of threads
        threads = new ArrayList<>();
        start = Instant.now();
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 1_000_000; j++) {
                    adder.increment();
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println(Duration.between(start, Instant.now()).toMillis());
        System.out.println(adder.sum());

        threads = new ArrayList<>();
        start = Instant.now();
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 1_000_000; j++) {
                    accumulator.accumulate(1);
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println(Duration.between(start, Instant.now()).toMillis());
        System.out.println(accumulator.get());

        // The cells hold the intermediate result for each thread. sum/get calculates the
        // final result. Calling sum too often kills the performance!

        adder.reset();
        threads = new ArrayList<>();
        start = Instant.now();
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 1_000_000; j++) {
                    adder.increment();
                    adder.sum();
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println(Duration.between(start, Instant.now()).toMillis());
        System.out.println(adder.sum());
    }
}
