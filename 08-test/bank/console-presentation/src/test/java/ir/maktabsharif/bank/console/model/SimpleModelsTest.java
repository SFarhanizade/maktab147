package ir.maktabsharif.bank.console.model;

import ir.maktabsharif.bank.console.FakeConsole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleModelsTest {

    private final FakeConsole console = new FakeConsole();

    @BeforeEach
    void setUp() {
        console.clear();
    }

    @Test
    void getBalanceModel_readsNumberOnce() {
        var m = new GetBalanceModel(console);
        console.addInput("ACC-1");
        assertThat(m.getNumber()).isEqualTo("ACC-1");
        assertThat(m.getNumber()).isEqualTo("ACC-1");
    }

    @Test
    void showOneModelByLong_readsLong() {
        var m = new ShowOneModelByLong(console);
        console.addInput("77");
        assertThat(m.getId()).isEqualTo(77L);
    }

    @Test
    void showOneModelByInt_readsInt() {
        var m = new ShowOneModelByInt(console);
        console.addInput("55");
        assertThat(m.getId()).isEqualTo(55);
    }

    @Test
    void createAccountModel_readsOwnerName() {
        var m = new CreateAccountModel(console);
        console.addInput("Ali");
        assertThat(m.getOwnerName()).isEqualTo("Ali");
    }
}