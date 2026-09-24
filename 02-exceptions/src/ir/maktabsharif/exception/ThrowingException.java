package ir.maktabsharif.exception;

public class ThrowingException {
    void main() {
        try {
            validateState(getState(1));
            validateState(getState(2));
            validateState(getState(3));
            validateState(getState(4));
        } catch (InvalidStateException e) {
            IO.println("Invalid state: " + e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException e) {
            IO.println("RuntimeException: " + e.getMessage());
            e.printStackTrace();
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

//        int x = 1/0;

        return switch (stateNumber) {
            case 1 -> "OK";
            case 2 -> "Failed";
            case 3 -> "Something";
            default -> throw new InvalidStateException("Unknown");
        };
    }
}

class InvalidStateException extends RuntimeException {
    public InvalidStateException(String message) {
        super(message);
    }
}
