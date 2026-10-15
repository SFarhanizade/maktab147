package ir.maktabsharif.streams.itr;

import java.util.List;

public class SimpleExample {

    void main() {
        IO.println("===numbers===");
        var numbers = List.of(2, 1, 3, 4, 5, 6, 7, 8, 9, 10);
        numbers.stream()
                .filter(number -> number % 2 == 0)
                .forEach(IO::println);

        IO.println("===names===");
        var names = List.of("ali", "reza", "hassan");
        names.stream()
                .filter(name -> name.length() > 3)
                .forEach(IO::println);
    }
}
