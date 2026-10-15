package ir.maktabsharif.streams;

import java.util.List;
import java.util.function.Predicate;

public class SimpleExample {
    void main() {
        IO.println("===numbers===");
        var numbers = List.of(2, 1, 3, 4, 5, 6, 7, 8, 9, 10);
        var iterator = numbers.iterator();
        Predicate<Integer> isEven = number -> number % 2 == 0;
        iterator = new MyIterator<>(iterator, isEven);
        var myIterable = new MyIterable<>(iterator);

        for (var number : myIterable) {
            IO.println(number);
        }


        IO.println("===names===");
        var names = List.of("ali", "reza", "hassan");
        Predicate<String> hasMinLength = name -> name.length()>3;
        var myIterable2 = new MyIterable<>(new MyIterator<>(names.iterator(), hasMinLength));

        for (var name : myIterable2) {
            IO.println(name);
        }
    }
}
