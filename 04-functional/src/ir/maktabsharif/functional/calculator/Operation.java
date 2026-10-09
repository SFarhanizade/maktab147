package ir.maktabsharif.functional.calculator;

@FunctionalInterface
public interface Operation {
    int operate(int a, int b);

    default void x(){}
}
