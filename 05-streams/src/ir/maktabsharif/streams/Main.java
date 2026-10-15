package ir.maktabsharif.streams;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Main {
     void main() {
         Predicate<String> stringPredicate = input -> !input.equals("0");
         Function<String,Integer> toLength = String::length;
         Consumer<Object> print = IO::println;

         Stream.generate(IO::readln)
                 .takeWhile(stringPredicate)
                 .map(toLength)
                 .forEach(print);
     }
}
