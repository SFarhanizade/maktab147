package ir.maktabsharif.functional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Main {
    static void main() {
        var names = Arrays.asList("ali", "hassan", "reza", "hamed", "mohammad");
        //anonymous inner class
        var comparator = new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                IO.println("here");
                return Integer.compare(o1.length(), o2.length());
            }

        };

        Collections.sort(names, comparator);
        Collections.sort(names);
        IO.println(names);

        IO.println("=============");
        final var byLength = new ByLength();

        Collections.sort(names, byLength);
        IO.println(names);
        //lambda expression
        Comparator<String> byLength2 = (String s1, String s2) -> {
            return Integer.compare(s2.length(), s1.length());
        };
        Comparator<String> byLength3 =
                (String s1, String s2) -> Integer.compare(s2.length(), s1.length());

        Comparator<String> byLength4 = (s1, s2) -> Integer.compare(s2.length(), s1.length());
        byLength2.compare("a", "b");

    }
}

class ByLength implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        return Integer.compare(o2.length(), o1.length());
    }
}

