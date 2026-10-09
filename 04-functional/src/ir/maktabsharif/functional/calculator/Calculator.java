package ir.maktabsharif.functional.calculator;

public class Calculator {
    private Calculator() {
    }

    public static int calculate(Operation operation, int a, int b) {
        return operation.operate(a, b);
    }
}
