package software.egger;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class bb_example_things_are_faster_when_done_in_parallel_with_cyclic_barrier {

    @SuppressWarnings("Duplicates")
    @Test
    public void compareParallelAndSequentialSumUsingBarriers() throws InterruptedException {

        int numberOfValues = Integer.MAX_VALUE / 100;
        System.out.println("Init size: " + numberOfValues);
        List<Integer> integerList = new ArrayList<>(numberOfValues); // not need to synchronize this time!

        int numberOfTasks = 4;

        int batchSize;
        int lastBatchSize;

        if (numberOfValues % numberOfTasks == 0) {
            batchSize = numberOfValues / numberOfTasks;
            lastBatchSize = batchSize;
        } else {
            batchSize = numberOfValues / (numberOfTasks - 1);
            lastBatchSize = numberOfValues % (numberOfTasks - 1);
        }

        CountDownLatch initDone = new CountDownLatch(1);

        ExecutorService executorService = Executors.newCachedThreadPool();
        List<List<Integer>> partialInitResults = new ArrayList<>();

        CyclicBarrier initBarrier = new CyclicBarrier(
                numberOfTasks,
                () -> {
                    System.out.println("Merging partial results");
                    for (List<Integer> initResult : partialInitResults) {
                        integerList.addAll(initResult);
                    }
                    initDone.countDown();
                }
        );


        class InitTask implements Runnable {
            private List<Integer> partialInitResult;
            private int startIdx;
            private int endIdx;

            private InitTask(List<Integer> partialInitResult, int startIdx, int endIdx) {
                this.partialInitResult = partialInitResult;
                this.startIdx = startIdx;
                this.endIdx = endIdx;
            }


            @Override
            public void run() {
                Random rnd = new Random();
                System.out.println("Init from: " + startIdx + " to: " + endIdx);

                for (int idx = startIdx; idx < endIdx; idx++) {
                    partialInitResult.add(rnd.nextInt());
                }

                try {
                    initBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }

            }
        }

        Instant start = Instant.now();

        for (int idx = 0; idx < numberOfTasks - 1; idx++) {
            List<Integer> partialInitResult = new ArrayList<>(batchSize);
            partialInitResults.add(partialInitResult);
            executorService.submit(new InitTask(partialInitResult, idx * batchSize, idx * batchSize + batchSize));
        }

        List<Integer> partialInitResult = new ArrayList<>(lastBatchSize);
        partialInitResults.add(partialInitResult);
        executorService.submit(new InitTask(partialInitResult, (numberOfTasks - 1) * batchSize, (numberOfTasks - 1) * batchSize + lastBatchSize));

        initDone.await();
        System.out.println("Init done");
        System.out.println("Init -> It took: " + Duration.between(start, Instant.now()));
        System.out.println("===");
        assertThat(integerList.size(), is(numberOfValues));

        System.out.println("Starting sum");
        CountDownLatch sumDone = new CountDownLatch(1);

        List<Long> partialSums = new ArrayList<>(numberOfTasks);

        Instant sumStart = Instant.now();

        CyclicBarrier sumBarrier = new CyclicBarrier(
                numberOfTasks,
                () -> {
                    System.out.println("Merging sum results");

                    long sum = 0;

                    for (long partialSum : partialSums) {
                        sum = sum + partialSum;
                    }

                    System.out.println("Sum(par)-> It took: " + Duration.between(sumStart, Instant.now()));
                    System.out.println("Sum(par) -> The sum is: " + sum);
                    System.out.println("===");
                    System.out.println();

                    sumDone.countDown();
                }
        );


        class SumUpTask implements Runnable {
            private final List<Long> partialSumResults;
            private final int resultIndex;
            private int startIdx;
            private int endIdx;

            private SumUpTask(List<Long> partialSumResults, int resultIndex, int startIdx, int endIdx) {
                this.partialSumResults = partialSumResults;
                this.resultIndex = resultIndex;
                this.startIdx = startIdx;
                this.endIdx = endIdx;
            }


            @Override
            public void run() {
                long sum = 0;
                System.out.println("Start - Sum from: " + startIdx + " to: " + endIdx);
                for (int idx = startIdx; idx < endIdx; idx++) {
                    sum = sum + integerList.get(idx);
                }
                partialSumResults.set(resultIndex, sum);
                System.out.println("Done - Sum from: " + startIdx + " to: " + endIdx);
                try {
                    sumBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }

            }
        }

        for (int idx = 0; idx < numberOfTasks - 1; idx++) {
            System.out.println("Submitting task");
            partialSums.add(0L); // Needed! Otherwise List.set(idx) will fail!
            executorService.submit(new SumUpTask(partialSums, idx, idx * batchSize, idx * batchSize + batchSize));
        }

        System.out.println("Submitting task");
        partialSums.add(0L);
        executorService.submit(new SumUpTask(partialSums, numberOfTasks - 1, (numberOfTasks - 1) * batchSize, (numberOfTasks - 1) * batchSize + batchSize));

        System.out.println("Waiting for calculation to finish");
        sumDone.await();

    }

}
