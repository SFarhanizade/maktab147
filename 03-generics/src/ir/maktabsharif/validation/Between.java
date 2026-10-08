package ir.maktabsharif.validation;

public class Between implements Validator<Number> {
    private final Number min;
    private final Number max;

    public Between(Number min, Number max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean validate(Number value) {
        return value.doubleValue() >= min.doubleValue() && value.doubleValue() <= max.doubleValue();
    }
}
