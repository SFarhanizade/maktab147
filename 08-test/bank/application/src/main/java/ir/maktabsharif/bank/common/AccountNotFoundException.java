package ir.maktabsharif.bank.common;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends RuntimeException {
    private final int accountId;
    private final String accountNumber;

    public AccountNotFoundException(int accountId) {
        super("account [%d] not found".formatted(accountId));
        this.accountId = accountId;
        this.accountNumber = "no-number";
    }

    public AccountNotFoundException(String accountNumber) {
        super("account [%s] not found".formatted(accountNumber));
        this.accountNumber = accountNumber;
        this.accountId = -1;
    }
}
