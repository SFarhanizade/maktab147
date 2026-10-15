package ir.maktabsharif.streams;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        var oldest = people.stream()
                .reduce((person1, person2) -> {
                    if (person1.age() > person2.age())
                        return person1;
                    return person2;
                })
                .get();

        IO.println("Oldest person: "+oldest);
    }

    record Person(String name,int age){}
}
