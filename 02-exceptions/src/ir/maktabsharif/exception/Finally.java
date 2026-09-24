package ir.maktabsharif.exception;

import java.util.Random;

public class Finally {
    void main() {
        int result = someMethod();

        IO.println("result: "+result);
    }

    int someMethod() {
        var random = new Random();
        try {
            if(random.nextBoolean()){
                throw new RuntimeException();
            }
            return 1;
        } catch (Exception e) {
            IO.println("error");
            return -1;
        } finally {
            IO.println("finally");
            return 0;
        }
    }
}
