package ir.maktabsharif.validation;

public class Max implements Validator<Number> {
    private final Number max;

    public Max(Number max) {
        this.max = max;
    }

    @Override
    public boolean validate(Number value) {
        return value.doubleValue() <= max.doubleValue();
    }
}
