package ir.maktabsharif.bank.account.usecase.admin;

import ir.maktabsharif.bank.account.Account;
import ir.maktabsharif.bank.page.Page;

public interface AdminAccountPresenter {
    void presentCreate(Account account);

    void presentFindAll(Page<Account> accounts);

    void presentFind(Account account);

    void presentDeposit(Account account);
}
