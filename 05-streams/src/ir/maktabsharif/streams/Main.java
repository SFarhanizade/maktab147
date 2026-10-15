package ir.maktabsharif.streams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    void main() {
        var numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Map<Boolean, List<Integer>> result = numbers.stream()
                .collect(Collectors.partitioningBy(number -> number % 2 == 0));

        var evens = result.get(true);
        var odds = result.get(false);

        IO.println("evens:");
        IO.println(evens);

        IO.println("=====");
        IO.println("odds:");
        IO.println(odds);
    }
}
