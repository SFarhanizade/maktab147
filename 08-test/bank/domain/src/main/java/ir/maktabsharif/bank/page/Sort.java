package ir.maktabsharif.bank.page;

public interface Sort {
    String property();

    Direction direction();


    enum Direction {
        ASC, DESC
    }

    static Sort asc(String property) {
        return new SortImpl(property, Direction.ASC);
    }

    static Sort desc(String property) {
        return new SortImpl(property, Direction.DESC);
    }
}

record SortImpl(String property, Sort.Direction direction) implements Sort {
}
