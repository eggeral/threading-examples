package software.egger;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class bt_concurrent_hash_map {

    @Test
    public void concurrentHashMapsAreOnlyProtectingTheirInternalStructure() throws ExecutionException, InterruptedException {
        //Concurrent only means that you can not damage the internal structure!!
        ConcurrentHashMap<String, Integer> countStore = new ConcurrentHashMap<>();
        countStore.put("c1", 0);
        countStore.put("c2", 0);

        runInThreadsAndLoops(2, () -> {
            int value1 = countStore.get("c1");
            int value2 = countStore.get("c2");
            countStore.put("c1", ++value1);
            countStore.put("c2", ++value2);
        });

        System.out.println(countStore);
    }

    @SuppressWarnings("Duplicates")
    static private void runInThreadsAndLoops(int numberOfThreads, Runnable runnable) throws ExecutionException, InterruptedException {
        List<CompletableFuture> futures = new ArrayList<>();


        for (int i = 0; i < numberOfThreads; i++) {
            CompletableFuture current = CompletableFuture.runAsync(
                    () -> {
                        for (int j = 0; j < 100_000; j++) {
                            runnable.run();
                        }
                    }
            );
            futures.add(current);
        }
        for (CompletableFuture future : futures) {
            future.get();
        }
    }
}
