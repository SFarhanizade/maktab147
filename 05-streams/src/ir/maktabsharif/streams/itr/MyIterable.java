package ir.maktabsharif.streams.itr;

import java.util.Iterator;

public class MyIterable<E> implements Iterable<E> {
    private final Iterator<E> iterator;

    public MyIterable(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    @Override
    public Iterator<E> iterator() {
        return iterator;
    }
}
