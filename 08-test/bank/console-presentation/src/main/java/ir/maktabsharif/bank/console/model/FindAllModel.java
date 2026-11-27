package ir.maktabsharif.bank.console.model;

import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountFindAllReq;
import ir.maktabsharif.bank.transaction.usecase.owner.req.OwnerTransactionFindAllReq;
import lombok.Getter;

@Getter
public class FindAllModel implements AdminAccountFindAllReq, OwnerTransactionFindAllReq {
    public int page;
    public int size;

    public FindAllModel(int size) {
        this.size = size;
    }
}