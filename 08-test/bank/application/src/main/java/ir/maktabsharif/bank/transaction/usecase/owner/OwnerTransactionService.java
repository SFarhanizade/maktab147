package ir.maktabsharif.bank.transaction.usecase.owner;

import ir.maktabsharif.bank.transaction.usecase.owner.req.OwnerTransactionFindAllReq;
import ir.maktabsharif.bank.transaction.usecase.owner.req.OwnerTransactionFindByIdReq;

public interface OwnerTransactionService {
    void findAll(OwnerTransactionFindAllReq req);

    void findById(OwnerTransactionFindByIdReq req);
}
