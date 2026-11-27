package ir.maktabsharif.bank.account.usecase.owner;

import ir.maktabsharif.bank.account.usecase.owner.req.OwnerAccountGetBalanceReq;
import ir.maktabsharif.bank.account.usecase.owner.req.OwnerAccountTransferReq;

public interface OwnerAccountService {
    void getBalance(OwnerAccountGetBalanceReq req);
    void transfer(OwnerAccountTransferReq req);
}
