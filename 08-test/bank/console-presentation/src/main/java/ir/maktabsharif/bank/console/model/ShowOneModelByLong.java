package ir.maktabsharif.bank.console.model;

import ir.maktabsharif.bank.console.Console;
import ir.maktabsharif.bank.transaction.usecase.owner.req.OwnerTransactionFindByIdReq;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ShowOneModelByLong implements OwnerTransactionFindByIdReq {
    private final Console console;
    private Long id;


    @Override
    public Long getId() {
        if (id == null) {
            console.print("Enter Id: ");
            id = Long.parseLong(console.readln());
        }

        return id;
    }
}