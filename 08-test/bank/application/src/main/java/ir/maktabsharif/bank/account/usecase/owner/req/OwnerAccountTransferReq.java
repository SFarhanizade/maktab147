package ir.maktabsharif.bank.account.usecase.owner.req;

import java.math.BigInteger;

public interface OwnerAccountTransferReq {
    String getSourceNumber();

    String getDestinationNumber();

    BigInteger getAmount();
}
