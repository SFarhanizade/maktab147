package ir.maktabsharif.validation;

public class NotBlank implements Validator<String> {

    @Override
    public boolean validate(String value) {
        return !String.valueOf(value).isBlank();
    }
}
