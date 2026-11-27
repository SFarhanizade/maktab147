package ir.maktabsharif.bank.transaction;

import ir.maktabsharif.bank.account.Account;
import ir.maktabsharif.bank.account.JpaAccount;

import java.math.BigInteger;

public class JpaTransactionFactory implements TransactionFactory {
    @Override
    public Transaction createOutTransaction(Account from, Account to, BigInteger amount) {
        return new JpaTransaction(amount, (JpaAccount) to, (JpaAccount) from, Transaction.Type.OUT);
    }

    @Override
    public Transaction createInTransaction(Account to, Account from, BigInteger amount) {
        return new JpaTransaction(amount, (JpaAccount) to, (JpaAccount) from, Transaction.Type.IN);
    }
}
