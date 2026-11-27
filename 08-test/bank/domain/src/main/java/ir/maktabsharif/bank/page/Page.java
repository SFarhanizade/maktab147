package ir.maktabsharif.bank.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Page<E> {
    List<E> content();

    Pageable pageable();

    long totalElements();

    default int pageNumber() {
        return pageable().page();
    }

    default int size() {
        return content().size();
    }

    static <E> Page<E> of(List<? extends E> content, Pageable pageable, long totalElements) {
        return new PageImpl<>(new ArrayList<>(content), pageable, totalElements);
    }

    static <E> Page<E> empty(Pageable pageable) {
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

}

record PageImpl<E>(List<E> content, Pageable pageable, long totalElements) implements Page<E> {

}
