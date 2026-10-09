package ir.maktabsharif.functional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main {
    static void main() {
        var names = Arrays.asList("ali", "hassan", "reza", "hamed", "mohammad");
        //anonymous inner class
        var comparator = new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return Integer.compare(o1.length(), o2.length());
            }

        };


        Collections.sort(names,comparator);
        Collections.sort(names);
        IO.println(names);

        IO.println("=============");
        var byLength = new ByLength();
        Collections.sort(names,byLength);
        IO.println(names);

    }
}

class ByLength implements Comparator<String>{
    @Override
    public int compare(String o1, String o2) {
        return Integer.compare(o2.length(), o1.length());
    }
}

