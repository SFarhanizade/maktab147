package ir.maktabsharif.bank.console.presenter;

import ir.maktabsharif.bank.account.Account;
import ir.maktabsharif.bank.account.usecase.admin.AdminAccountPresenter;
import ir.maktabsharif.bank.console.Console;
import ir.maktabsharif.bank.page.Page;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminAccountConsolePresenter implements AdminAccountPresenter {
    private final Console console;
    @Getter
    private Page<Account> findAllData;

    @Override
    public void presentCreate(Account account) {
        console.println("Account[ id: %d, number: %s] created!".formatted(account.getId(), account.getNumber()));
    }

    @Override
    public void presentFindAll(Page<Account> accounts) {
        this.findAllData = accounts;

        console.println("\n===== Page %s =====".formatted(accounts.pageNumber() + 1));
        for (Account account : accounts.content()) {
            console.println("- id: %d, number: %s".formatted(account.getId(), account.getNumber()));
        }

    }

    @Override
    public void presentFind(Account account) {
        console.println("Account[ id: %d, number: %s]".formatted(account.getId(), account.getNumber()));
    }

    @Override
    public void presentDeposit(Account account) {
        console.println("Account[ id: %d, number: %s, balance: %s]".formatted(account.getId(), account.getNumber(), account.getBalance()));
    }
}
