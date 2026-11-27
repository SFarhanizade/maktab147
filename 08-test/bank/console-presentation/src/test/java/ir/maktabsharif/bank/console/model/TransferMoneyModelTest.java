package ir.maktabsharif.bank.console.model;

import ir.maktabsharif.bank.console.FakeConsole;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

class TransferMoneyModelTest {

    @Test
    void collectsAllFieldsAndCaches() {
        var console = new FakeConsole();
        var model = new TransferMoneyModel(console);
        console.addInput("111");
        console.addInput("222");
        console.addInput("500");
        assertThat(model.getSourceNumber()).isEqualTo("111");
        assertThat(model.getDestinationNumber()).isEqualTo("222");
        assertThat(model.getAmount()).isEqualTo(new BigInteger("500"));
        assertThat(model.getSourceNumber()).isEqualTo("111"); // cached
    }
}