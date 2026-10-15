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

        //using Stream
        Double avg = people.stream()
                .collect(Collectors.averagingInt(Person::age));

        IO.println("Average age: "+avg);

        //using IntStream
        Double avg2 = people.stream()
                .mapToInt(Person::age)
                .average()
                .getAsDouble();

        IO.println("Average age: "+avg2);

    }

    record Person(String name,int age){}
}
