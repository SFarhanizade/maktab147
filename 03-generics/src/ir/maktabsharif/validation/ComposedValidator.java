package ir.maktabsharif.validation;

class ComposedValidator<T> implements Validator<T> {
    private final Validator<T>[] validators;

    public ComposedValidator(Validator<T>[] validators) {
        this.validators = validators;
    }

    @Override
    public boolean validate(T value) {
        boolean result = true;

        for (var validator : validators) {
            result = result && validator.validate(value);
        }
        return result;
    }
}