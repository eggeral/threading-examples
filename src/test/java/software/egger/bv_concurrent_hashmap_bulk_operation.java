package software.egger;

import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class bv_concurrent_hashmap_bulk_operation {
    @Test
    public void bulkOperations() {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        Map<String, Integer> tmp = Stream.of("aaaa", "aa", "aaaaa", "aaaaaaaa", "aaaaaaa")
                .collect(Collectors.toMap(x -> x, String::length));
        map.putAll(tmp);

        // searching for things (only returns the first value found!!!)
        String result = map.search(2, // threshold for parallel processing
                (key, value) -> (value == 2 || value == 4) ? key : null // null signals value not found (WTF)
        );


        System.out.println(result);
        // map.searchEntries();
        // map.searchKeys();
        // map.searchValues();

        // apply something to each key
        System.out.println("---");
        map.forEachKey(2, System.out::println);
        map.forEachKey(2, v -> "key: " + v, System.out::println);

        // reduce the keys
        System.out.println("---");
        System.out.println(map.reduceKeys(2, (lhs, rhs) -> lhs + "," + rhs)); // stream reduce works different! WTF

        // reduce the values
        System.out.println(map.reduceValues(2, (lhs, rhs) -> lhs + rhs));
        // transformers can be used as filters
        Integer sum = map.reduceValues(2, v -> v < 5 ? v : null,
                (lhs, rhs) -> lhs + rhs);
        System.out.println(sum);

    }
}
