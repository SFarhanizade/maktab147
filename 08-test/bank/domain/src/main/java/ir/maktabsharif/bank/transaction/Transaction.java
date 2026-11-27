package ir.maktabsharif.bank.transaction;

import ir.maktabsharif.bank.account.Account;
import ir.maktabsharif.bank.base.BaseDomain;
import lombok.Getter;

import java.math.BigInteger;

public interface Transaction extends BaseDomain<Long> {
    BigInteger getAmount();

    Account getTo();

    Account getFrom();

    Type getType();

    enum Type {
        IN(1),
        OUT(2);

        @Getter
        private final int code;

        Type(int code) {
            this.code = code;
        }
    }
}
