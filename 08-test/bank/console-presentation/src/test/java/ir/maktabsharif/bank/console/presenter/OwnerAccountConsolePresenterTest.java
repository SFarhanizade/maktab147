package ir.maktabsharif.bank.console.presenter;

import ir.maktabsharif.bank.account.Account;
import ir.maktabsharif.bank.console.FakeConsole;
import ir.maktabsharif.bank.page.Page;
import ir.maktabsharif.bank.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OwnerAccountConsolePresenterTest {

    private final FakeConsole console = new FakeConsole();
    private final OwnerAccountConsolePresenter presenter = new OwnerAccountConsolePresenter(console);

    private Transaction tx(long id, String amount, Transaction.Type type, String from, String to) {
        Transaction t = mock(Transaction.class);
        when(t.getId()).thenReturn(id);
        when(t.getAmount()).thenReturn(new BigInteger(amount));
        when(t.getType()).thenReturn(type);
        Account f = mock(Account.class);
        when(f.getNumber()).thenReturn(from);
        Account d = mock(Account.class);
        when(d.getNumber()).thenReturn(to);
        when(t.getFrom()).thenReturn(f);
        when(t.getTo()).thenReturn(d);
        return t;
    }

    @Test
    void presentGetBalance_printsBalance() {
        presenter.presentGetBalance(BigInteger.valueOf(12345));
        assertThat(console.getOutput()).contains("Balance:").contains("12345");
    }

    @Test
    void presentFindAll_storesPageAndPrintsContent() {
        var t1 = tx(1L, "10", Transaction.Type.IN, "A", "B");
        var t2 = tx(2L, "20", Transaction.Type.OUT, "A", "B");
        Page<Transaction> page = mock(Page.class);
        when(page.content()).thenReturn(List.of(t1, t2));
        when(page.pageNumber()).thenReturn(0);

        presenter.presentFindAll(page);
        assertThat(presenter.getFindAllData()).isSameAs(page);
        String out = console.getOutput();
        assertThat(out).contains("Page 1");
        assertThat(out).contains("id: 1").contains("id: 2");
        assertThat(out).contains("IN").contains("OUT");
    }
}