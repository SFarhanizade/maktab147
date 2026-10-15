package ir.maktabsharif.streams;

import java.util.ArrayList;
import java.util.List;

public class SimpleExample {
    void main() {
        var numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        var result = new ArrayList<Integer>();
        for (var number : numbers) {
            if (number % 2 == 0)
                IO.println(number);
//                result.add(number);
        }
    }
}
