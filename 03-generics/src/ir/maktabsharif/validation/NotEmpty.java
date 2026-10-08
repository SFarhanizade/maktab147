package ir.maktabsharif.validation;

public class NotEmpty implements Validator<String> {

    @Override
    public boolean validate(String value) {
        return !String.valueOf(value).isEmpty();
    }
}
