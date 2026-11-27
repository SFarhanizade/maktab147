package ir.maktabsharif.bank.transaction.usecase.owner;

import ir.maktabsharif.bank.common.TransactionNotFoundException;
import ir.maktabsharif.bank.page.Page;
import ir.maktabsharif.bank.page.Pageable;
import ir.maktabsharif.bank.transaction.Transaction;
import ir.maktabsharif.bank.transaction.TransactionRepository;
import ir.maktabsharif.bank.transaction.usecase.owner.req.OwnerTransactionFindAllReq;
import ir.maktabsharif.bank.transaction.usecase.owner.req.OwnerTransactionFindByIdReq;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OwnerTransactionServiceImpl implements OwnerTransactionService {
    private final TransactionRepository repository;
    private final OwnerTransactionPresenter presenter;

    @Override
    public void findAll(OwnerTransactionFindAllReq req) {
        Page<Transaction> transactions = repository.findAll(
                Pageable.of(req.getPage(), req.getSize())
        );

        presenter.presentFindAll(transactions);
    }

    @Override
    public void findById(OwnerTransactionFindByIdReq req) {
        Transaction transaction = repository.findById(req.getId())
                .orElseThrow(() -> new TransactionNotFoundException(req.getId()));

        presenter.presentFind(transaction);
    }
}
