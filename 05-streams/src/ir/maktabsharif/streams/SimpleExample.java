package ir.maktabsharif.streams;

import java.util.ArrayList;
import java.util.List;

public class SimpleExample {
    void main() {
        var names = List.of("ali", "mohammad", "hassan", "reza");
        var students = new ArrayList<Student>();
        for (var name : names) {
            Student student = new Student(name);
            students.add(student);
        }

        IO.println(students);
    }
}

record Student(String name){
    @Override
    public String toString() {
        return "name="+name;
    }
}