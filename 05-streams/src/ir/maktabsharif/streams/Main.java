package ir.maktabsharif.streams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    void main() {
        var people = List.of(
                new Person("Ali", 10),
                new Person("Reza", 10),
                new Person("Ahmad", 20),
                new Person("Hooman", 25),
                new Person("Hamed", 25)
        );

        String result = people.stream()
                .map(Person::name)
                .collect(Collectors.joining(", "));

        IO.println(result);
    }

    record Person(String name,int age){}
}
