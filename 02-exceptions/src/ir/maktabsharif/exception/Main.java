package ir.maktabsharif.exception;

public class Main {
    void main() {
        IO.println(divide(2, 1));
        IO.println(divide(6, 3));
        try {
            IO.println(divide(6, 0));
        } catch (ArithmeticException e) {
            IO.println("Divide by zero.");
        }
        try {
            IO.println(divide(6, null));
        } catch (NullPointerException e) {
            IO.println("NullPointerException");
        }
        try {
            IO.println(divide(6, null));//unchecked
        } catch (Exception e) {
            IO.println("Exception");
        }
        IO.println("End");
    }

    int divide(int a, Integer b) {

        return a / b;
    }
}