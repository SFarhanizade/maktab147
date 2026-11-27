package ir.maktabsharif.bank.console.presenter;

import ir.maktabsharif.bank.account.Account;
import ir.maktabsharif.bank.console.FakeConsole;
import ir.maktabsharif.bank.page.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminAccountConsolePresenterTest {

    private final FakeConsole console = new FakeConsole();
    private final AdminAccountConsolePresenter presenter = new AdminAccountConsolePresenter(console);

    private Account account(int id, String number, String balance) {
        Account a = mock(Account.class);
        when(a.getId()).thenReturn(id);
        when(a.getNumber()).thenReturn(number);
        when(a.getBalance()).thenReturn(new BigInteger(balance));
        return a;
    }


    @BeforeEach
    void setUp() {
        console.clear();
    }

    @Test
    void presentCreate_printsAccount() {
        presenter.presentCreate(account(1, "1001", "0"));
        assertThat(console.getOutput()).contains("created!").contains("1").contains("1001");
    }

    @Test
    void presentFindAll_storesPageAndPrintsAccounts() {
        Page<Account> page = mock(Page.class);
        var a1 = account(1, "A1", "0");
        var a2 = account(2, "A2", "0");
        when(page.content()).thenReturn(List.of(a1, a2));
        when(page.pageNumber()).thenReturn(2);

        presenter.presentFindAll(page);
        assertThat(presenter.getFindAllData()).isSameAs(page);
        String out = console.getOutput();
        assertThat(out).contains("Page 3");
        assertThat(out).contains("A1").contains("A2");
    }

    @Test
    void presentFind_printsAccount() {
        presenter.presentFind(account(5, "555", "0"));
        assertThat(console.getOutput()).contains("Account[").contains("5").contains("555");
    }

    @Test
    void presentDeposit_printsBalance() {
        presenter.presentDeposit(account(3, "300", "1000"));
        assertThat(console.getOutput()).contains("balance:").contains("1000");
    }
}