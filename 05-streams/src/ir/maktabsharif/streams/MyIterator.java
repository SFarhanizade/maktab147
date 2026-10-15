package ir.maktabsharif.streams;

import java.util.Iterator;
import java.util.function.Predicate;

public class MyIterator<E> implements Iterator<E> {
    private final Iterator<E> iterator;
    private final Predicate<E> condition;
    private E next;

    public MyIterator(Iterator<E> iterator, Predicate<E> condition) {
        this.iterator = iterator;
        this.condition = condition.negate();
    }

    @Override
    public boolean hasNext() {
        var hasNext = iterator.hasNext();
        if (!hasNext) return false;

        next = iterator.next();

        while (condition.test(next)) {
            if (iterator.hasNext())
                next = iterator.next();
            else return false;
        }

        return true;

    }

    @Override
    public E next() {
        return next;
    }
}
