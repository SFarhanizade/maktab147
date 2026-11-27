package ir.maktabsharif.bank.page;

public interface Pageable {
    int page();

    int size();

    static Pageable of(int page, int size) {
        return new PageableImpl(page, size);
    }

    default int offset() {
        return page() * size();
    }
}

record PageableImpl(int page, int size) implements Pageable {
}
