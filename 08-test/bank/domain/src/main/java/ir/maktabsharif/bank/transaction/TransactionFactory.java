package ir.maktabsharif.bank.transaction;

import ir.maktabsharif.bank.account.Account;

import java.math.BigInteger;

public interface TransactionFactory {
    Transaction createOutTransaction(Account from, Account to, BigInteger amount);

    Transaction createInTransaction(Account to, Account from, BigInteger amount);
}
