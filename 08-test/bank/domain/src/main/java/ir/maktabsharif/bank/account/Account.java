package ir.maktabsharif.bank.account;

import ir.maktabsharif.bank.base.BaseDomain;

import java.math.BigInteger;

public interface Account extends BaseDomain<Integer> {
    String getNumber();

    BigInteger getBalance();

    void setBalance(BigInteger balance);
}
