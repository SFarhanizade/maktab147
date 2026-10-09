package ir.maktabsharif.functional.pipeline;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Main {
    static void main() {
        Predicate<Integer> isEven = number -> number % 2 == 0;
        Function<Integer, Integer> timesTen = number -> number * 10;
        Supplier<String> labelProvider = () -> "Result:";
        Consumer<Object> printer = value-> IO.println(value);
        Consumer<Integer> evenPrinter = number ->{
            if(isEven.test(number)){
                Integer multiplied = timesTen.apply(number);
                printer.accept(multiplied);
            }
        };

        var numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        String label = labelProvider.get();
        printer.accept(label);
        for (var number : numbers) {
            evenPrinter.accept(number);
        }
    }
}
