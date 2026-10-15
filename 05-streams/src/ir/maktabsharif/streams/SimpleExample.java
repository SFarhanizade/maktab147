package ir.maktabsharif.streams;

import java.util.List;

public class SimpleExample {
    void main() {
        var numbers = List.of(2, 1, 3, 4, 5, 6, 7, 8, 9, 10);
        var iterator = numbers.iterator();
        iterator = new MyIterator(iterator);
        var myIterable = new MyIterable<>(iterator);

        for (var number : myIterable) {
            IO.println(number);
        }
    }
}
