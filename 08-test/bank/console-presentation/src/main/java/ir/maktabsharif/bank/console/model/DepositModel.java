package ir.maktabsharif.bank.console.model;

import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountDepositReq;
import ir.maktabsharif.bank.console.Console;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@RequiredArgsConstructor
public class DepositModel implements AdminAccountDepositReq {
    private final Console console;
    private int id;
    private BigInteger amount;

    @Override
    public int getId() {
        if (id == 0) {
            console.print("Enter account number: ");
            id = Integer.parseInt(console.readln());
        }

        return id;
    }

    @Override
    public BigInteger getAmount() {
        if (amount == null) {
            console.print("Enter amount: ");
            amount = new BigInteger(console.readln());
        }

        return amount;
    }
}