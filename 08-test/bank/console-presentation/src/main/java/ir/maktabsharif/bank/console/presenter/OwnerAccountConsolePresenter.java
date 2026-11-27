package ir.maktabsharif.bank.console.presenter;

import ir.maktabsharif.bank.account.usecase.owner.OwnerAccountPresenter;
import ir.maktabsharif.bank.console.Console;
import ir.maktabsharif.bank.page.Page;
import ir.maktabsharif.bank.transaction.Transaction;
import ir.maktabsharif.bank.transaction.usecase.owner.OwnerTransactionPresenter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@RequiredArgsConstructor
public class OwnerAccountConsolePresenter implements OwnerAccountPresenter, OwnerTransactionPresenter {
    private final Console console;
    @Getter
    private Page<Transaction> findAllData;

    @Override
    public void presentGetBalance(BigInteger balance) {
        console.println("Balance: " + balance.toString());
    }

    @Override
    public void presentTransfer(Transaction transaction) {
        console.println("""
                Transaction succeeded!
                transaction id: %d
                %s moved from %s to %s"""
                .formatted(
                        transaction.getId(),
                        transaction.getAmount(),
                        transaction.getFrom().getNumber(),
                        transaction.getTo().getNumber()));
    }

    @Override
    public void presentFindAll(Page<Transaction> transactions) {
        this.findAllData = transactions;

        console.println("\n===== Page %s =====".formatted(transactions.pageNumber() + 1));
        for (Transaction transaction : transactions.content()) {
            console.println("- id: %d, amount: %s, type: %s"
                    .formatted(
                            transaction.getId(),
                            transaction.getAmount(),
                            transaction.getType().name()
                    ));
        }
    }

    @Override
    public void presentFind(Transaction transaction) {
        console.println("Transaction[ id: %d, amount: %s, type: %s, from: %s, to: %s]"
                .formatted(
                        transaction.getId(),
                        transaction.getAmount(),
                        transaction.getType().name(),
                        transaction.getFrom().getNumber(),
                        transaction.getTo().getNumber()
                ));
    }
}
