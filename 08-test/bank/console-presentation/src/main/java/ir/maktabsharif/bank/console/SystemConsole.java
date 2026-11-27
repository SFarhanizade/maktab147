package ir.maktabsharif.bank.console;

public class SystemConsole implements Console {
    @Override
    public String readln() {
        return IO.readln();
    }

    @Override
    public void print(String s) {
        IO.print(s);
    }

    @Override
    public void println(String s) {
        IO.println(s);
    }
}
