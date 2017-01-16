package software.egger;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.*;

@SuppressWarnings("Duplicates")
public class bm_example_count_chars_in_frankenstein_with_completion_service {

    @Test
    public void countWords() throws IOException, InterruptedException, ExecutionException {

        // Seq count algorithm is executed several times in order to show how it speeds up with every call.
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream("frankenstein.txt")))
        ) {
            int count = 0;
            String fileLine;
            ArrayList<String> lines = new ArrayList<>(1000);
            while ((fileLine = reader.readLine()) != null) {
                lines.add(fileLine);
            }
            System.out.println("Lines: " + lines.size());
            System.out.println("1 seq ===");

            long start = System.currentTimeMillis();
            for (String line : lines) {
                String[] words = line.split("[\\P{L}]+");
                for (String word : words) {
                    if (word.contains("th"))
                        count++;
                }
            }

            long end = System.currentTimeMillis();
            System.out.println("Count: " + count + " - " + (end - start) + "ms");

            System.out.println("2 seq ====");
            count = 0;
            start = System.currentTimeMillis();
            for (String line : lines) {
                String[] words = line.split("[\\P{L}]+");
                for (String word : words) {
                    if (word.contains("th"))
                        count++;
                }
            }
            end = System.currentTimeMillis();
            System.out.println("Count: " + count + " - " + (end - start) + "ms");

            System.out.println("3 seq ====");
            count = 0;
            start = System.currentTimeMillis();
            for (String line : lines) {
                String[] words = line.split("[\\P{L}]+");
                for (String word : words) {
                    if (word.contains("th"))
                        count++;
                }
            }
            end = System.currentTimeMillis();
            System.out.println("Count: " + count + " - " + (end - start) + "ms");

            System.out.println("4 seq ====");
            count = 0;
            start = System.currentTimeMillis();
            for (String line : lines) {
                String[] words = line.split("[\\P{L}]+");
                for (String word : words) {
                    if (word.contains("th"))
                        count++;
                }
            }
            end = System.currentTimeMillis();
            System.out.println("Count: " + count + " - " + (end - start) + "ms");

            System.out.println("5 seq ====");
            count = 0;
            start = System.currentTimeMillis();
            for (String line : lines) {
                String[] words = line.split("[\\P{L}]+");
                for (String word : words) {
                    if (word.contains("th"))
                        count++;
                }
            }
            end = System.currentTimeMillis();
            System.out.println("Count: " + count + " - " + (end - start) + "ms");


            System.out.println("4 par ====");


            class Worker implements Callable<Integer> {

                private final int start;
                private final int end;

                public Worker(int start, int end) {
                    this.start = start;
                    this.end = end;
                }

                @Override
                public Integer call() throws Exception {
                    int count = 0;
                    for (int idx = start; idx < end && idx < lines.size(); idx++) {
                        String line = lines.get(idx);
                        String[] words = line.split("[\\P{L}]+");
                        for (String word : words) {
                            if (word.contains("th"))
                                count++;
                        }
                    }
                    return count;
                }
            }

            ExecutorService executorService = Executors.newCachedThreadPool();
            ExecutorCompletionService<Integer> completionService = new ExecutorCompletionService<>(executorService);

            start = System.currentTimeMillis();
            int idx = 0;
            int workers = 0;
            int batchSize = 1000; // try 1 / 10 / 100 / 1000 / 10000
            while (idx < lines.size()) {
                Worker worker = new Worker(idx, idx + batchSize);
                completionService.submit(worker);
                workers++;
                idx += batchSize;
            }

            count = 0;
            for (int workerNr = 0; workerNr < workers; workerNr++) {
                int value = completionService.take().get();
                count += value;
            }

            end = System.currentTimeMillis();
            System.out.println("Count: " + count + " - " + (end - start) + "ms");

            System.out.println("====");


        }

    }
}
