package ir.maktabsharif.bank.account.usecase.owner;

import ir.maktabsharif.bank.transaction.Transaction;

import java.math.BigInteger;

public interface OwnerAccountPresenter {
    void presentGetBalance(BigInteger balance);

    void presentTransfer(Transaction outTransaction);
}
