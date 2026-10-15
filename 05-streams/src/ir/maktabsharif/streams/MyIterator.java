package ir.maktabsharif.streams;

import java.util.Iterator;

public class MyIterator implements Iterator<Integer> {
    private final Iterator<Integer> iterator;
    private Integer next;

    public MyIterator(Iterator<Integer> iterator) {
        this.iterator = iterator;
    }

    //while (iterator.hasNext()) {
    //            var number = iterator.next();
    //            if (number % 2 == 0)
    //                IO.println(number);
    //        }
    @Override
    public boolean hasNext() {
        var hasNext = iterator.hasNext();

        if (!hasNext) return false;

        next = iterator.next();
//var numbers = List.of(2, 1, 3, 4, 5, 6, 7, 8, 9);
//var numbers = List.of(1);
        while (next % 2 != 0) {
            if (iterator.hasNext())
                next = iterator.next();
            else return false;
        }

        return true;

    }

    @Override
    public Integer next() {
        return next;
    }
}
