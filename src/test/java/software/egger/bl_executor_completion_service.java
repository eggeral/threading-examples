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

public class bl_executor_completion_service {

    @Test
    public void getTheResultsOfSeveralTasks() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutorCompletionService<Integer> completionService = new ExecutorCompletionService<>(executorService);

        Callable<Integer> supplier = () -> {
            Random rnd = new Random();
            int value = rnd.nextInt(1000);
            Thread.sleep(value);
            return rnd.nextInt(value);
        };

        for (int idx = 0; idx < 10; idx++) {
            completionService.submit(supplier);
        }

        int sum = 0;
        for (int idx = 0; idx < 10; idx++) {
            int value = completionService.take().get();
            System.out.println("Got: " + value);
            sum += value;
        }

        System.out.println("Sum is:" + sum);
    }

    //

}
