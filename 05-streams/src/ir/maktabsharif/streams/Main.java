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

        Map<Integer, List<Person>> result = people.stream()
                .collect(Collectors.groupingBy(Person::age));

        for (var entry : result.entrySet()) {
            IO.println(entry);
        }
    }

    record Person(String name,int age){}
}
