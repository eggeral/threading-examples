package software.egger;


import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ak_example_things_are_faster_when_done_in_parallel {
    @Test
    public void sumUpAnArray() {
        Random rnd = new Random();

        int size = Integer.MAX_VALUE / 100;
        System.out.println("Init size: " + size);
        List<Integer> integerList = new ArrayList<>(size); // make sure to set the initial size of the array. Otherwise this becomes a performance bottleneck!

        Instant start = Instant.now();
        for (int idx = 0; idx < size; idx++) {
            integerList.add(rnd.nextInt());
            if (idx % 10_000 == 0)
                printMemoryUsage();

        }

        System.out.println("Init done");
        System.out.println("Init -> It took: " + Duration.between(start, Instant.now()));
        System.out.println("===");
        System.out.println("List size is: " + integerList.size() + "should be: " + size);

        assertThat(integerList.size(), is(size));

        long sum = 0;
        start = Instant.now();

        for (Integer item : integerList) {
            sum = sum + item;
        }

        System.out.println("Sum -> It took: " + Duration.between(start, Instant.now()));
        System.out.println("Sum -> The sum is: " + sum);
        System.out.println("===");
        System.out.println();
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void sumUpAnArrayWithFasterInitSplitWork() throws InterruptedException {

        int size = Integer.MAX_VALUE / 100;
        System.out.println("Init size: " + size);
        //List<Integer> integerList = new ArrayList<>(); // not thread safe!
        //List<Integer> integerList = new Vector<>(); // Vector is thread safe!
        List<Integer> integerList = Collections.synchronizedList(new ArrayList<>(size)); // Make an existing list Thread safe

        BiConsumer<Integer, Integer> init = (start, end) -> {
            Random rnd = new Random(); // make sure each thread gets its own random! Otherwise rnd is the bottleneck!
            List<Integer> local = new ArrayList<>(size); // local buffer is needed because otherwise the synchronized access to integerList makes things slow again.

            System.out.println("Init from: " + start + " to: " + end);
            for (int idx = start; idx < end; idx++) {
                local.add(rnd.nextInt());
            }

            integerList.addAll(local);
        };

        List<Thread> threads = new ArrayList<>();
        int batchSize = size / 4; // Set different batch sizes in order to see performance differences
        int startIndex = 0;
        while (startIndex < size) {
            int currentStart = startIndex;
            int end = Math.min(startIndex + batchSize, size);
            Thread thread = new Thread(() -> init.accept(currentStart, end));
            threads.add(thread);
            startIndex = end;
        }

        Instant start = Instant.now();

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Init done");
        System.out.println("Init -> It took: " + Duration.between(start, Instant.now()));
        System.out.println("===");
        assertThat(integerList.size(), is(size));

        long sum = 0;
        start = Instant.now();

        for (Integer item : integerList) {
            sum = sum + item;
        }

        System.out.println("Sum -> It took: " + Duration.between(start, Instant.now()));
        System.out.println("Sum -> The sum is: " + sum);
        System.out.println("===");
        System.out.println();
    }


    private volatile int nextIdx = 0;

    @Test
    public void sumUpAnArrayWithFasterInitTakeNext() throws InterruptedException {

        Object nextIdxLock = new Object();

        int size = Integer.MAX_VALUE / 100;
        System.out.println("Init size: " + size);
        List<Integer> integerList = Collections.synchronizedList(new ArrayList<>(size));

        List<Thread> threads = new ArrayList<>();
        int numberOfThreads = 8;
        for (int idx = 0; idx < numberOfThreads; idx++) {
            Thread thread = new Thread(() -> {
                Random rnd = new Random(); // make sure each thread gets its own random! Otherwise rnd is the bottleneck!
                List<Integer> local = new ArrayList<>(size);
                while (true) {
                    synchronized (nextIdxLock) {
                        if (nextIdx >= size)
                            break;
                        nextIdx++;
                    }
                    local.add(rnd.nextInt());
                }
                integerList.addAll(local);
            });
            threads.add(thread);
        }

        Instant start = Instant.now();

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Init done");
        System.out.println("Init -> It took: " + Duration.between(start, Instant.now()));
        System.out.println("===");
        assertThat(integerList.size(), is(size));

        long sum = 0;
        start = Instant.now();

        for (Integer item : integerList) {
            sum = sum + item;
        }

        System.out.println("Sum -> It took: " + Duration.between(start, Instant.now()));
        System.out.println("Sum -> The sum is: " + sum);
        System.out.println("===");
        System.out.println();
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void compareParallelAndSequentialSum() throws InterruptedException {

        Object writeListLock = new Object();

        int size = Integer.MAX_VALUE / 100;
        System.out.println("Init size: " + size);
        List<Integer> integerList = new ArrayList<>(size); // this time we manually synchronize list write access in order to speed up read access later.
        //List<Integer> integerList = Collections.synchronizedList(new ArrayList<>(size)); // try this to see how slow the sum is if we use the synchronized list.
        BiConsumer<Integer, Integer> init = (start, end) -> {
            Random rnd = new Random();
            List<Integer> local = new ArrayList<>(size);

            System.out.println("Init from: " + start + " to: " + end);
            for (int idx = start; idx < end; idx++) {
                local.add(rnd.nextInt());
            }

            synchronized (writeListLock) {
                integerList.addAll(local);
            }
        };

        List<Thread> threads = new ArrayList<>();
        int batchSize = size / 4;
        int startIndex = 0;
        while (startIndex < size) {
            int currentStart = startIndex;
            int end = Math.min(startIndex + batchSize, size);
            Thread thread = new Thread(() -> init.accept(currentStart, end));
            threads.add(thread);
            startIndex = end;
        }

        Instant start = Instant.now();

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Init done");
        System.out.println("Init -> It took: " + Duration.between(start, Instant.now()));
        System.out.println("===");
        assertThat(integerList.size(), is(size));

        long seqSum = 0;
        start = Instant.now();

        for (Integer item : integerList) {
            seqSum = seqSum + item;
        }

        System.out.println("Sum(seq)-> It took: " + Duration.between(start, Instant.now()));
        System.out.println("Sum(seq) -> The sum is: " + seqSum);
        System.out.println("===");
        System.out.println();

        long[] parSum = new long[]{0};
        Object globalSumLock = new Object();

        BiConsumer<Integer, Integer> sumUp = (startIdx, endIdx) -> {
            long sum = 0;
            for (int idx = startIdx; idx < endIdx; idx++) {
                sum = sum + integerList.get(idx);
            }
            synchronized (globalSumLock) {
                parSum[0] = parSum[0] + sum;
            }
        };

        threads.clear();
        startIndex = 0;
        while (startIndex < size) {
            int currentStart = startIndex;
            int end = Math.min(startIndex + batchSize, size);
            Thread thread = new Thread(() -> sumUp.accept(currentStart, end));
            threads.add(thread);
            startIndex = end;
        }

        start = Instant.now();

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Sum(par)-> It took: " + Duration.between(start, Instant.now()));
        System.out.println("Sum(par) -> The sum is: " + parSum[0]);
        System.out.println("===");
        System.out.println();

        assertThat(parSum[0], is(seqSum));


    }

    private static void printMemoryUsage() {

        int mb = 1024 * 1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        System.out.println("##### Heap utilization statistics [MB] #####");

        //Print used memory
        System.out.println("Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        //Print free memory
        System.out.println("Free Memory:"
                + runtime.freeMemory() / mb);

        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);

        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);
    }
}
