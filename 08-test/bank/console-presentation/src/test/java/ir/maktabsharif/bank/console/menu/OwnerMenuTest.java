package ir.maktabsharif.bank.console.menu;

import ir.maktabsharif.bank.account.usecase.owner.OwnerAccountService;
import ir.maktabsharif.bank.console.FakeConsole;
import ir.maktabsharif.bank.transaction.usecase.owner.OwnerTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OwnerMenuTest {

    private final FakeConsole console = new FakeConsole();

    private OwnerMenu menu(OwnerAccountService as, OwnerTransactionService ts) {
        return new OwnerMenu(console, p -> as, p -> ts);
    }

    @BeforeEach
    void setUp() {
        console.clear();
    }

    @Test
    void backFromRoot_returns() {
        var as = mock(OwnerAccountService.class);
        var ts = mock(OwnerTransactionService.class);
        console.addInput("5");
        menu(as, ts).show();
        assertThat(console.getOutput()).contains("Back to Main Menu");
        verifyNoInteractions(as, ts);
    }

    @Test
    void getBalance_callsService() {
        var as = mock(OwnerAccountService.class);
        var ts = mock(OwnerTransactionService.class);
        // 1=get balance, account number, 5=back
        console.addInput("1");
        console.addInput("9");
        console.addInput("5");
        menu(as, ts).show();
        verify(as).getBalance(any());
    }

    @Test
    void transfer_callsService() {
        var as = mock(OwnerAccountService.class);
        var ts = mock(OwnerTransactionService.class);
        // 2=transfer, 5=back
        console.addInput("2");
        console.addInput("5");
        menu(as, ts).show();
        verify(as).transfer(any());
    }

    @Test
    void showOneTransaction_callsFindById() {
        var as = mock(OwnerAccountService.class);
        var ts = mock(OwnerTransactionService.class);
        // 4=show one, id, 5=back
        console.addInput("4");
        console.addInput("42");
        console.addInput("5");
        menu(as, ts).show();
        verify(ts).findById(any());
    }
}