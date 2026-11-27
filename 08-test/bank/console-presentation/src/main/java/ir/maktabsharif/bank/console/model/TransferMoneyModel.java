package ir.maktabsharif.bank.console.model;

import ir.maktabsharif.bank.account.usecase.owner.req.OwnerAccountTransferReq;
import ir.maktabsharif.bank.console.Console;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@Getter
@RequiredArgsConstructor
public class TransferMoneyModel implements OwnerAccountTransferReq {
    private final Console console;
    private String sourceNumber;
    private String destinationNumber;
    private BigInteger amount;

    @Override
    public String getSourceNumber() {
        if (sourceNumber == null) {
            console.print("Enter source account number: ");
            sourceNumber = console.readln();
        }

        return sourceNumber;
    }

    @Override
    public String getDestinationNumber() {
        if (destinationNumber == null) {
            console.print("Enter destination account number: ");
            destinationNumber = console.readln();
        }

        return destinationNumber;
    }

    @Override
    public BigInteger getAmount() {
        if (amount == null) {
            console.print("Enter amount to transfer: ");
            amount = new BigInteger(console.readln());
        }

        return amount;
    }
}