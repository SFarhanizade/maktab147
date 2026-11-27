package ir.maktabsharif.bank.e2e;

import ir.maktabsharif.bank.console.Console;

import java.util.ArrayDeque;
import java.util.Deque;

public class FakeConsole implements Console {
    private final Deque<String> inputs = new ArrayDeque<>();
    private StringBuilder output = new StringBuilder();

    public void addInput(String s) {
        inputs.add(s);
    }

    public String getOutput() {
        return output.toString();
    }

    @Override
    public String readln() {
        var value = inputs.poll();
        if (value == null)
            throw new IllegalStateException("Console has no input yet!");

        return value;
    }

    @Override
    public void print(String s) {
        output.append(s);
    }

    @Override
    public void println(String s) {
        output.append(s).append("\n");
    }

    public void clear() {
        inputs.clear();
        output = new StringBuilder();
    }
}