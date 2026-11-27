package ir.maktabsharif.bank.account.usecase.admin.req;

import java.math.BigInteger;

public interface AdminAccountDepositReq {
    int getId();

    BigInteger getAmount();
}
