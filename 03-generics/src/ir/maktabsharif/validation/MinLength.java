package ir.maktabsharif.validation;

public class MinLength implements Validator<String> {
    private final int min;

    public MinLength(int min) {
        this.min = min;
    }

    @Override
    public boolean validate(String value) {
        return value.length() >= min;
    }
}
