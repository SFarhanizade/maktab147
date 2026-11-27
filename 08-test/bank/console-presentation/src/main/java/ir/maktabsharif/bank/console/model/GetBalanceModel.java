package ir.maktabsharif.bank.console.model;

import ir.maktabsharif.bank.account.usecase.owner.req.OwnerAccountGetBalanceReq;
import ir.maktabsharif.bank.console.Console;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetBalanceModel implements OwnerAccountGetBalanceReq {
    private final Console console;
    private String number;

    @Override
    public String getNumber() {
        if (number == null) {
            console.print("Enter account number: ");
            number = console.readln();
        }

        return number;
    }
}
