package ir.maktabsharif.exception;

public class Finally {
    void main() {
        try {
            IO.println("calculate(1);");
            calculate(1);
            IO.println("success!");
        } catch (Exception e) {
            IO.println("error");
        } finally {
            IO.println("finally");
        }
    }

    void calculate(int x) {
        throw new RuntimeException();
    }
}
