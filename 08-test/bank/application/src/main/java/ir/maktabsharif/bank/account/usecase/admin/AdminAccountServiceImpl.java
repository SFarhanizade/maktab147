package ir.maktabsharif.bank.account.usecase.admin;

import ir.maktabsharif.bank.account.Account;
import ir.maktabsharif.bank.account.AccountFactory;
import ir.maktabsharif.bank.account.AccountRepository;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountCreateReq;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountDepositReq;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountFindAllReq;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountFindReq;
import ir.maktabsharif.bank.base.TransactionManager;
import ir.maktabsharif.bank.common.AccountNotFoundException;
import ir.maktabsharif.bank.page.Page;
import ir.maktabsharif.bank.page.Pageable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminAccountServiceImpl implements AdminAccountService {
    private final AccountFactory factory;
    private final AccountRepository repository;
    private final TransactionManager transactionManager;
    private final AdminAccountPresenter presenter;


    @Override
    public void create(AdminAccountCreateReq req) {
        Account account = factory.createAccount(req.getOwnerName());
        repository.save(account);
        presenter.presentCreate(account);
    }

    @Override
    public void deposit(AdminAccountDepositReq req) {
        transactionManager.execute(() -> {
            Account account = repository.findById(req.getId())
                    .orElseThrow(() -> new AccountNotFoundException(req.getId()));

            account.setBalance(account.getBalance().add(req.getAmount()));
            repository.save(account);
            presenter.presentDeposit(account);
        });
    }

    @Override
    public void findAll(AdminAccountFindAllReq req) {
        Page<Account> accounts = repository.findAll(
                Pageable.of(req.getPage(), req.getSize())
        );

        presenter.presentFindAll(accounts);

    }

    @Override
    public void find(AdminAccountFindReq req) {
        Account account = repository.findById(req.getId())
                .orElseThrow(() -> new AccountNotFoundException(req.getId()));

        presenter.presentFind(account);
    }
}
