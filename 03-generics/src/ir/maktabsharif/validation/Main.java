package ir.maktabsharif.validation;

import java.util.Collection;

public class Main {
    void main() {
        int a = 9;
        //1, 2.5
        //0.5, 3
        var min5 = new Min(5);
        var max10 = new Max(10);

        Validator<Number> between5And10 = Validator.of(min5, max10);
        var minNim = new Min(0.5);
        var between1And10 = new Between(1, 10);

        validate(between5And10, 1);

        IO.println("min(5,a): " + min(5, a));
        IO.println("min5.validate(a): " + min5.validate(a));
        IO.println("min5.validate(6.5): " + min5.validate(6.5));
        IO.println("min5.validate(6): " + min5.validate(6));
        IO.println("minNim.validate(1): " + minNim.validate(1));
        IO.println("max(14,a): " + max(14, a));
        IO.println("between(1,10,a): " + between(1, 10, a));
        IO.println("between1And10.validate(a): " + between1And10.validate(a));
    }

    <T> void validate(Validator<T> validator, T value) {
        IO.println("result: " + validator.validate(value));
    }

//    boolean rule(Object param,Object value){}

    boolean min(int min, int value) {
        return value >= min;
    }

    boolean max(int max, int value) {
        return value <= max;
    }

    boolean minLength(int length, String value) {
        return min(length, value.length());
    }

    boolean size(int size, Collection<?> value) {
        return value.size() == size;
    }

    boolean notNull(Object value) {
        return value != null;
    }

    boolean notEmpty(String value) {
        return !String.valueOf(value).isEmpty();//""
    }

    boolean notBlank(String value) {
        return !String.valueOf(value).isBlank();//""," ","null"
    }

    boolean between(int min, int max, int value) {
        return min(min, value) && max(max, value);
    }
}
