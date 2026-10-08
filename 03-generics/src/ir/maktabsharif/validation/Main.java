package ir.maktabsharif.validation;

import java.util.Collection;

public class Main {
    void main() {
        int a = 12;

        IO.println("min(5,a): "+min(5,a));
        IO.println("max(14,a): "+max(14,a));
        IO.println("between(1,10,a): "+between(1,10,a));
    }

    boolean rule(Object param,Object value){}

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
