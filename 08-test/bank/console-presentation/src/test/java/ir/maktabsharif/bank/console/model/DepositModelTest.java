package ir.maktabsharif.bank.console.model;

import ir.maktabsharif.bank.console.FakeConsole;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

class DepositModelTest {

    @Test
    void gettersPromptOnceAndCache() {
        var console = new FakeConsole();
        var model = new DepositModel(console);
        console.addInput("42");
        console.addInput("1000");

        assertThat(model.getId()).isEqualTo(42);
        assertThat(model.getAmount()).isEqualTo(new BigInteger("1000"));

        assertThat(model.getId()).isEqualTo(42);
        assertThat(model.getAmount()).isEqualTo(new BigInteger("1000"));

    }
}