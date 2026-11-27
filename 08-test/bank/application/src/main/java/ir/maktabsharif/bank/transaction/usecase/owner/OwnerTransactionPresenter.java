package ir.maktabsharif.bank.transaction.usecase.owner;

import ir.maktabsharif.bank.page.Page;
import ir.maktabsharif.bank.transaction.Transaction;

public interface OwnerTransactionPresenter {
    void presentFindAll(Page<Transaction> transactions);

    void presentFind(Transaction transaction);
}
