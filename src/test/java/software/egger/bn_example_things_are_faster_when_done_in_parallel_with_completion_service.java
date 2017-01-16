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

public class bn_example_things_are_faster_when_done_in_parallel_with_completion_service {

    @Test
    public void sumUpArray() throws InterruptedException, ExecutionException {
        int numberOfValues = Integer.MAX_VALUE / 100;
        System.out.println("Init size: " + numberOfValues);
        int[] integerList = new int[numberOfValues];

        class InitializerResult {
            public int startIdx;
            public int[] values;
        }

        class Initializer implements Callable<InitializerResult> {

            private final int start;
            private final int end;

            public Initializer(int start, int end) {
                this.start = start;
                this.end = end < numberOfValues ? end : numberOfValues;
            }

            @Override
            public InitializerResult call() throws Exception {
                Random rnd = new Random();

                int length = end - start;
                int[] values = new int[length];

                for (int idx = 0; idx < length; idx++) {
                    values[idx] = rnd.nextInt();
                }

                InitializerResult result = new InitializerResult();
                result.startIdx = start;
                result.values = values;
                return result;
            }
        }

        ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutorCompletionService<InitializerResult> initCompletionService = new ExecutorCompletionService<>(executorService);

        long start = System.currentTimeMillis();
        int idx = 0;
        int initializers = 0;
        int batchSize = 100_000_000; // also show 10_000_000 / 100_000_000
        while (idx < numberOfValues) {
            Initializer initializer = new Initializer(idx, idx + batchSize);
            initCompletionService.submit(initializer);
            initializers++;
            idx += batchSize;
        }

        for (int initializerNr = 0; initializerNr < initializers; initializerNr++) {

            InitializerResult result = initCompletionService.take().get();
            System.arraycopy(result.values, 0, integerList, result.startIdx, result.values.length);

        }

        long end = System.currentTimeMillis();
        System.out.println("Init done. Took: " + (end - start) + "ms - " + initializers + " Initializers.");

        class Summer implements Callable<Long> {

            private final int start;
            private final int end;

            public Summer(int start, int end) {
                this.start = start;
                this.end = end < numberOfValues ? end : numberOfValues;
            }

            @Override
            public Long call() throws Exception {
                long result = 0;

                for (int idx = start; idx < end; idx++) {
                    result += integerList[idx];
                }

                return result;
            }
        }

        ExecutorCompletionService<Long> sumCompletionService = new ExecutorCompletionService<>(executorService);

        start = System.currentTimeMillis();
        idx = 0;
        int summers = 0;
        while (idx < numberOfValues) {
            Summer summer = new Summer(idx, idx + batchSize);
            sumCompletionService.submit(summer);
            summers++;
            idx += batchSize;
        }

        long sum = 0;
        for (int summerNr = 0; summerNr < summers; summerNr++) {

            sum += sumCompletionService.take().get();

        }

        end = System.currentTimeMillis();
        System.out.println("Sum done. " + sum + " Took: " + (end - start) + "ms - " + initializers + " Initializers.");

    }
}
