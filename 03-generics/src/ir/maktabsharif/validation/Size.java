package ir.maktabsharif.validation;

import java.util.Collection;

public class Size implements Validator<Collection<?>> {
    private final int size;

    public Size(int size) {
        this.size = size;
    }

    @Override
    public boolean validate(Collection<?> value) {
        return value.size() == size;
    }
}
