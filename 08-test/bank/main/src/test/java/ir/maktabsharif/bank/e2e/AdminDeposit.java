package ir.maktabsharif.bank.e2e;

import ir.maktabsharif.bank.account.Account;
import ir.maktabsharif.bank.account.AccountRepository;
import ir.maktabsharif.bank.console.Console;
import ir.maktabsharif.bank.console.menu.MainMenu;
import ir.maktabsharif.bank.util.TestUtil;
import ir.maktabsharif.bank.util.WithPostgres;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigInteger;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AdminDeposit extends WithPostgres {
    private FakeConsole console;

    private MainMenu menu;
    private AccountRepository accountRepository;

    @BeforeEach
    void beforeEach() {
        context.clear();
        console = new FakeConsole();
        context.setBean(Console.class, console);
        menu = context.getBean(MainMenu.class);
        accountRepository = context.getBean(AccountRepository.class);
        TestUtil.truncateAllTables(postgres);
    }


    @Test
    void adminCanDeposit() {
        enterAdminMenu();
        createAccount("Bob");
        exitFromAdminMenu();
        menu.show();

        var bobAccount = accountRepository.findAll().stream().findFirst().orElseThrow();
        var bobAccountId = bobAccount.getId();

        enterAdminMenu();
        deposit(bobAccountId.toString(), "500");
        exitFromAdminMenu();
        menu.show();

        assertThat(accountRepository.findById(bobAccountId))
                .isPresent()
                .get()
                .extracting(Account::getBalance)
                .isEqualTo(new BigInteger("500"));
    }

    private void deposit(String accountId, String amount) {
        console.addInput("4");
        console.addInput(accountId);
        console.addInput(amount);
    }

    private void exitFromAdminMenu() {
        console.addInput("5");
        console.addInput("3");
    }

    private void createAccount(String ownername) {
        console.addInput("1");
        console.addInput(ownername);
    }

    private void enterAdminMenu() {
        console.addInput("1");
    }
}