package ir.maktabsharif.bank.account.usecase.owner;

import ir.maktabsharif.bank.account.Account;
import ir.maktabsharif.bank.account.AccountRepository;
import ir.maktabsharif.bank.account.usecase.owner.req.OwnerAccountGetBalanceReq;
import ir.maktabsharif.bank.account.usecase.owner.req.OwnerAccountTransferReq;
import ir.maktabsharif.bank.base.TransactionManager;
import ir.maktabsharif.bank.common.AccountNotFoundException;
import ir.maktabsharif.bank.transaction.Transaction;
import ir.maktabsharif.bank.transaction.TransactionFactory;
import ir.maktabsharif.bank.transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OwnerAccountServiceImpl implements OwnerAccountService {
    private final AccountRepository repository;
    private final TransactionManager transactionManager;
    private final OwnerAccountPresenter presenter;
    private final TransactionFactory transactionFactory;
    private final TransactionRepository transactionRepository;

    @Override
    public void getBalance(OwnerAccountGetBalanceReq req) {
        var account = findByNumber(req.getNumber());

        presenter.presentGetBalance(account.getBalance());

    }

    @Override
    public void transfer(OwnerAccountTransferReq req) {
        var sourceAccount = findByNumber(req.getSourceNumber());
        var destinationAccount = findByNumber(req.getDestinationNumber());

        Transaction outTransaction = transactionFactory.createOutTransaction(
                sourceAccount, destinationAccount, req.getAmount()
        );

        Transaction inTransaction = transactionFactory.createInTransaction(
                destinationAccount, sourceAccount, req.getAmount()
        );

        transactionManager.execute(() -> {
            if (sourceAccount.getBalance().compareTo(req.getAmount()) < 0)
                throw new InsufficientBalanceException(req.getSourceNumber(), req.getAmount());

            sourceAccount.setBalance(sourceAccount.getBalance().subtract(req.getAmount()));
            destinationAccount.setBalance(destinationAccount.getBalance().add(req.getAmount()));
            repository.save(sourceAccount);
            repository.save(destinationAccount);
            transactionRepository.save(outTransaction);
            transactionRepository.save(inTransaction);
        });

        presenter.presentTransfer(outTransaction);
    }

    private Account findByNumber(String number) {
        return repository.findByNumber(number)
                .orElseThrow(() -> new AccountNotFoundException(number));
    }
}
