package ir.maktabsharif.bank.account.usecase.admin;

import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountCreateReq;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountDepositReq;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountFindAllReq;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountFindReq;

public interface AdminAccountService {
    void create(AdminAccountCreateReq req);

    void deposit(AdminAccountDepositReq req);

    void findAll(AdminAccountFindAllReq req);

    void find(AdminAccountFindReq req);
}
