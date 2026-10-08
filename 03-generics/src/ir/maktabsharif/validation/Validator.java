package ir.maktabsharif.validation;

public interface Validator<T> {

    static <T> Validator<T> of(Validator<T>... validators) {
        return new ComposedValidator<>(validators);
    }

    boolean validate(T value);


}
