package ir.maktabsharif.validation;

public class Min implements Validator<Number> {
    private final Number min;

    public Min(Number min) {
        this.min = min;
    }

    @Override
    public boolean validate(Number value) {
        return value.doubleValue() >= min.doubleValue();
    }
}
