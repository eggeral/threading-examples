package software.egger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class bu_concurrent_hashmap_updating {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // BTW ConcurrentHashMap is for huge maps!
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        long size = map.mappingCount();

        // BTW hash buckets are no longer lists but trees. -> O(log(n))
        // even for "bad" hash functions!

        // Solving the update problem!
        map.put("c1", 0);
        runInThreadsAndLoops(2, () -> {
            int oldValue;
            int newValue;
            do {
                oldValue = map.get("c1");
                newValue = oldValue + 1;
            } while (!map.replace("c1", oldValue, newValue)); // retry until we get are correctly replacing the old value
        });

        System.out.println(map.get("c1"));

        // compute is even better
        map.put("c2", 0);
        runInThreadsAndLoops(2, () -> map.compute("c2", (key, oldValue) -> ++oldValue));
        System.out.println(map.get("c2"));

        runInThreadsAndLoops(2, () -> {
            map.computeIfAbsent("c3", key -> 0);
            map.computeIfPresent("c3", (key, oldValue) -> ++oldValue);
        });
        System.out.println(map.get("c3"));

        // The ultimate merge!
        runInThreadsAndLoops(2, () -> map.merge("c4", 1, (current, init) -> current + init));
        System.out.println(map.get("c4"));

        // Returning null removes an entry (for compute and merge!)
        map.compute("c3", (key, oldValue) -> null);
        System.out.println(map.get("c3"));

        // merge and compute should not take long! Similar to database transactions.
        // the longer they last the more likely another thread interferes!

    }

    static private void runInThreadsAndLoops(int numberOfThreads, Runnable runnable) throws ExecutionException, InterruptedException {
        List<CompletableFuture> futures = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            CompletableFuture current = CompletableFuture.runAsync(() -> {
                for (int j = 0; j < 100_000; j++) {
                    runnable.run();
                }
            });
            futures.add(current);
        }
        for (CompletableFuture future : futures) {
            future.get();
        }
    }

}
