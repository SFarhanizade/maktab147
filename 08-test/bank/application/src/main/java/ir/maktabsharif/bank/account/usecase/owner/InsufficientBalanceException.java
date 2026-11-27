package ir.maktabsharif.bank.account.usecase.owner;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class InsufficientBalanceException extends RuntimeException {
    private final String accountNumber;
    private final BigInteger amount;

    public InsufficientBalanceException(String accountNumber, BigInteger amount) {
        super("account [%s] has less balance than [%s]".formatted(accountNumber, amount));
        this.accountNumber = accountNumber;
        this.amount = amount;
    }
}
