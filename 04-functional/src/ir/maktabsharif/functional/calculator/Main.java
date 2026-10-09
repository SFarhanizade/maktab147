package ir.maktabsharif.functional.calculator;

public class Main {
    static void main() {
        IO.print("""
                Choose operation:
                1.Add
                2.Subtract
                3.Multiply
                4.Division
                > """);
        int operationIndex = Integer.parseInt(IO.readln());
        Operation addOperation =  (a, b) -> a + b;
        Operation subOperation =  (a, b) -> a - b;
        Operation mulOperation =  (a, b) -> a * b;
        Operation divOperation =  (a, b) -> a / b;

        Operation operation = switch (operationIndex) {
            case 1 -> addOperation;
            case 2 -> subOperation;
            case 3 -> mulOperation;
            case 4 -> divOperation;
            default -> throw new UnsupportedOperationException(operationIndex + " is not supported!!!");
        };

        IO.print("Enter first param: ");
        int a = Integer.parseInt(IO.readln());
        IO.print("Enter second param: ");
        int b = Integer.parseInt(IO.readln());

        var result = Calculator.calculate(operation, a, b);

        IO.println("result: " + result);
    }
}
