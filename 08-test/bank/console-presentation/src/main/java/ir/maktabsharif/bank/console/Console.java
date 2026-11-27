package ir.maktabsharif.bank.console;

public interface Console {
    String readln();

    void print(String s);

    void println(String s);

    static Console system() {
        return new Console() {
            public String readln() {
                return IO.readln();
            }

            public void print(String s) {
                IO.print(s);
            }

            public void println(String s) {
                IO.println(s);
            }
        };
    }
}