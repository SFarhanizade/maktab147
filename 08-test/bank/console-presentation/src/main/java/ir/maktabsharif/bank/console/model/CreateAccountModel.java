package ir.maktabsharif.bank.console.model;

import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountCreateReq;
import ir.maktabsharif.bank.console.Console;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateAccountModel implements AdminAccountCreateReq {
    private final Console console;
    private String ownerName;

    @Override
    public String getOwnerName() {
        if (ownerName == null) {
            console.print("Enter owner name: ");
            ownerName = console.readln();
        }

        return ownerName;
    }
}