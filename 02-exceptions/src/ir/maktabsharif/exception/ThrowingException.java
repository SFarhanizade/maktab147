package ir.maktabsharif.exception;

public class ThrowingException {
    void main() {
        try {
            validateState(getState(1));
            validateState(getState(2));
            validateState(getState(3));
            validateState(getState(4));
        } catch (RuntimeException e) {
            IO.println("Invalid state: " + e.getMessage());
        }
    }

    private void validateState(String state) {
        if (state.equals("Unknown")) {
            IO.println("Invalid state");
        } else {
            IO.println(state);
        }
    }

    String getState(int stateNumber) {
        //1->"OK"
        //2->"Failed"
        //3->"Something"
        return switch (stateNumber) {
            case 1 -> "OK";
            case 2 -> "Failed";
            case 3 -> "Something";
            default -> throw new RuntimeException("Unknown");
        };
    }
}
