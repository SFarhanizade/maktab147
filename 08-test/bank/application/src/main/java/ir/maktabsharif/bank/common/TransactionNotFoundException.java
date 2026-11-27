package ir.maktabsharif.bank.common;

import lombok.Getter;

@Getter
public class TransactionNotFoundException extends RuntimeException {
    private final long transactionId;

    public TransactionNotFoundException(long transactionId) {
        super("transaction [%d] not found".formatted(transactionId));
        this.transactionId = transactionId;
    }
}
