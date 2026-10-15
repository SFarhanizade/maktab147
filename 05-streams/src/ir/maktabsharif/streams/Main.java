package ir.maktabsharif.streams;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    void main() {
        var people = List.of(
                new Person("Ali", 10),
                new Person("Reza", 10),
                new Person("Ahmad", 20),
                new Person("Hooman", 25),
                new Person("Hamed", 25)
        );

        Set<Person> oldest = people.stream()
                .reduce(new HashSet<>(),
                        (set, person) -> {
                            if (set.isEmpty()) {
                                return new HashSet<>(Set.of(person));
                            }
                            for (var p : set) {
                                if (person.age() > p.age()) {
                                    return new HashSet<>(Set.of(person));
                                } else if (person.age() == p.age()) {
                                    set.add(person);
                                    return set;
                                }
                            }

                            return set;
                        }, (set1, set2) -> {
                            set1.addAll(set2);
                            return set1;
                        });

        IO.println("Oldest person: " + oldest);
    }

    record Person(String name, int age) {
    }
}
