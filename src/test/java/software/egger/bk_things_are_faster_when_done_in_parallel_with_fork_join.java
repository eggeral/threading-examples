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

public class bk_things_are_faster_when_done_in_parallel_with_fork_join {

    @Test
    public void calculateTheSumUsingForkJoin() throws InterruptedException {

        // Fork Join splits a task into smaller tasks and combines the result. Each worker maintains its own queue to maximize performance.
        // If a worker has nothing to do it my steal work from another worker

        int numberOfValues = Integer.MAX_VALUE / 100;
        System.out.println("Init size: " + numberOfValues);

        class InitTask extends RecursiveTask<List<Integer>> {
            private int startIdx;
            private int endIdx;

            private InitTask(int startIdx, int endIdx) {
                this.startIdx = startIdx;
                this.endIdx = endIdx;
            }

            @Override
            protected List<Integer> compute() {


                if (endIdx - startIdx > 1000) {

                    int mid = startIdx + (endIdx - startIdx) / 2;
                    InitTask left = new InitTask(startIdx, mid);
                    InitTask right = new InitTask(mid + 1, endIdx);

                    right.fork();

                    List<Integer> result = left.compute();
                    result.addAll(right.join());
                    return result;

                } else {

                    List<Integer> partialResult = new ArrayList<>();
                    Random rnd = new Random();
                    //System.out.println("Init from: " + startIdx + " to: " + endIdx);

                    for (int idx = 0; idx <= endIdx - startIdx; idx++) {
                        partialResult.add(rnd.nextInt());
                    }

                    return partialResult;

                }

            }
        }

        Instant start = Instant.now();

        ForkJoinPool pool = new ForkJoinPool();
        List<Integer> integerList = pool.invoke(new InitTask(0, numberOfValues - 1));

        System.out.println("Init done");
        System.out.println("Init -> It took: " + Duration.between(start, Instant.now()));
        System.out.println("===");
        assertThat(integerList.size(), is(numberOfValues));

        System.out.println("Starting sum");


        class SumTask extends RecursiveTask<Long> {
            private int startIdx;
            private int endIdx;

            private SumTask(int startIdx, int endIdx) {
                this.startIdx = startIdx;
                this.endIdx = endIdx;
            }

            @Override
            protected Long compute() {


                if (endIdx - startIdx > 1000) {

                    int mid = startIdx + (endIdx - startIdx) / 2;
                    SumTask left = new SumTask(startIdx, mid);
                    SumTask right = new SumTask(mid + 1, endIdx);

                    right.fork();
                    return left.compute() + right.join();

                } else {

                    //System.out.println("Sum from: " + startIdx + " to: " + endIdx);

                    long sum = 0;

                    for (int idx = startIdx; idx < endIdx; idx++) {
                        sum += integerList.get(idx);
                    }

                    return sum;

                }

            }
        }

        Instant sumStart = Instant.now();
        long sum = pool.invoke(new SumTask(0, integerList.size()));

        System.out.println("Sum: " + sum + " -> It took: " + Duration.between(sumStart, Instant.now()));
        System.out.println("===");

    }

}
