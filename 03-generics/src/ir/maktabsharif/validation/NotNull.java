package ir.maktabsharif.validation;

public class NotNull implements Validator<Object> {

    @Override
    public boolean validate(Object value) {
        return value != null;
    }
}
