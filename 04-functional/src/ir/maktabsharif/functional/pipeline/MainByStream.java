package ir.maktabsharif.functional.pipeline;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MainByStream {
    static void main() {
        Predicate<Integer> isEven = number -> number % 2 == 0;
        Predicate<Integer> isDividableBy5 = number -> number % 5 == 0;
        Predicate<Integer> condition = isEven.and(isDividableBy5);
        Function<Integer, Integer> timesTen = number -> number * 10;
        Supplier<String> labelProvider = () -> "Result:";
        Consumer<Object> printer = value -> IO.println(value);

        var numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        String label = labelProvider.get();
        printer.accept(label);
        numbers.stream()
                .filter(condition)
                .map(timesTen)
                .forEach(printer);


    }
}
