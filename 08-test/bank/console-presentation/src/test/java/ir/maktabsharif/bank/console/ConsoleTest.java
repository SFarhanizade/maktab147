package ir.maktabsharif.bank.console;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleTest {

    @Test
    void fakeConsolePrintsAndReads() {
        var console = new FakeConsole();
        console.addInput("hello");
        console.print("prompt>");
        assertEquals("hello", console.readln());
        console.println("done");
        assertEquals("prompt>done\n", console.getOutput());
    }

    @Test
    void fakeConsoleThrowsWhenNoInput() {
        var console = new FakeConsole();
        assertThrows(IllegalStateException.class, console::readln);
    }
}