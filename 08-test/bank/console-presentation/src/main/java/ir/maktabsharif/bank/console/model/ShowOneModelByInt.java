package ir.maktabsharif.bank.console.model;

import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountFindReq;
import ir.maktabsharif.bank.console.Console;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ShowOneModelByInt implements AdminAccountFindReq {
    private final Console console;
    private int id;


    @Override
    public int getId() {
        if (id == 0) {
            console.print("Enter Id: ");
            id = Integer.parseInt(console.readln());
        }

        return id;
    }
}