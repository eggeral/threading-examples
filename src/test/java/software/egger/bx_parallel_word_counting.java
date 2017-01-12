package software.egger;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class bx_parallel_word_counting {

    @Test
    public void measureCountingWords() throws IOException {
        Stream<String> lines = Files.lines(Paths.get("frankenstein.txt"));
        Instant start = Instant.now();
        System.out.println(lines.flatMap(l -> Arrays.stream(l.split("[\\P{L}]+")))
                .collect(Collectors.groupingBy(w -> w.length(), Collectors.counting())));
        System.out.println(Duration.between(start, Instant.now()));

        // a second time in order to minimize JVM Hotspot influence
        lines = Files.lines(Paths.get("frankenstein.txt"));
        start = Instant.now();
        System.out.println(lines.flatMap(l -> Arrays.stream(l.split("[\\P{L}]+")))
                .collect(Collectors.groupingBy(w -> w.length(), Collectors.counting())));
        System.out.println(Duration.between(start, Instant.now()));

        lines = Files.lines(Paths.get("frankenstein.txt"));
        start = Instant.now();
        System.out.println(lines.parallel().flatMap(l -> Arrays.stream(l.split("[\\P{L}]+")))
                .collect(Collectors.groupingByConcurrent(w -> w.length(), Collectors.counting())));
        System.out.println(Duration.between(start, Instant.now()));
    }
}
