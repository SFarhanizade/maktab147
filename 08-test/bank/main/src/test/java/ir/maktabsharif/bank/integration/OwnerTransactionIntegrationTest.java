package ir.maktabsharif.bank.integration;

import ir.maktabsharif.bank.account.Account;
import ir.maktabsharif.bank.account.JpaAccount;
import ir.maktabsharif.bank.account.usecase.admin.AdminAccountPresenter;
import ir.maktabsharif.bank.account.usecase.admin.AdminAccountService;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountCreateReq;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountDepositReq;
import ir.maktabsharif.bank.account.usecase.owner.OwnerAccountPresenter;
import ir.maktabsharif.bank.account.usecase.owner.OwnerAccountService;
import ir.maktabsharif.bank.account.usecase.owner.req.OwnerAccountTransferReq;
import ir.maktabsharif.bank.common.TransactionNotFoundException;
import ir.maktabsharif.bank.page.Page;
import ir.maktabsharif.bank.transaction.Transaction;
import ir.maktabsharif.bank.transaction.usecase.owner.OwnerTransactionPresenter;
import ir.maktabsharif.bank.transaction.usecase.owner.OwnerTransactionService;
import ir.maktabsharif.bank.transaction.usecase.owner.req.OwnerTransactionFindAllReq;
import ir.maktabsharif.bank.transaction.usecase.owner.req.OwnerTransactionFindByIdReq;
import ir.maktabsharif.bank.util.TestUtil;
import ir.maktabsharif.bank.util.WithPostgres;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OwnerTransactionIntegrationTest extends WithPostgres {
    private OwnerTransactionService transactionService;
    private OwnerAccountService ownerService;
    private AdminAccountService adminService;

    @Mock
    private OwnerTransactionPresenter presenter;

    @Mock
    private OwnerAccountPresenter ownerPresenter;

    @Mock
    private AdminAccountPresenter adminPresenter;

    @BeforeEach
    void beforeEach() {
        context.clear();
        context.setBean(OwnerTransactionPresenter.class, presenter);
        context.setBean(OwnerAccountPresenter.class, ownerPresenter);
        context.setBean(AdminAccountPresenter.class, adminPresenter);

        transactionService = context.getBean(OwnerTransactionService.class);
        ownerService = context.getBean(OwnerAccountService.class);
        adminService = context.getBean(AdminAccountService.class);

        TestUtil.truncateAllTables(postgres);
        reset(presenter, ownerPresenter, adminPresenter);
    }

    @Test
    void findAll_returnsAllTransactionsInOrder() {
        var source = createAccountWithBalance("Alice", new BigInteger("1000"));
        var destination = createAccountWithBalance("Bob", new BigInteger("0"));

        transfer(source.getNumber(), destination.getNumber(), new BigInteger("100"));
        transfer(source.getNumber(), destination.getNumber(), new BigInteger("200"));
        transfer(source.getNumber(), destination.getNumber(), new BigInteger("300"));

        transactionService.findAll(findAllReq(0, 10));

        ArgumentCaptor<Page<Transaction>> pageCaptor = ArgumentCaptor.captor();
        verify(presenter).presentFindAll(pageCaptor.capture());
        Page<Transaction> page = pageCaptor.getValue();

        assertThat(page.content()).hasSize(6);
        assertThat(page.content())
                .extracting(Transaction::getAmount)
                .containsExactly(
                        new BigInteger("100"),
                        new BigInteger("100"),
                        new BigInteger("200"),
                        new BigInteger("200"),
                        new BigInteger("300"),
                        new BigInteger("300")
                );
    }

    @Test
    void findAll_respectsPagination() {
        var source = createAccountWithBalance("Alice", new BigInteger("1000"));
        var destination = createAccountWithBalance("Bob", new BigInteger("0"));

        transfer(source.getNumber(), destination.getNumber(), new BigInteger("100"));
        transfer(source.getNumber(), destination.getNumber(), new BigInteger("200"));
        transfer(source.getNumber(), destination.getNumber(), new BigInteger("300"));
        transfer(source.getNumber(), destination.getNumber(), new BigInteger("400"));

        transactionService.findAll(findAllReq(1, 4));

        ArgumentCaptor<Page<Transaction>> pageCaptor = ArgumentCaptor.captor();
        verify(presenter).presentFindAll(pageCaptor.capture());
        Page<Transaction> page = pageCaptor.getValue();

        assertThat(page.content()).hasSize(4);
        assertThat(page.content())
                .extracting(Transaction::getAmount)
                .containsExactly(
                        new BigInteger("300"),
                        new BigInteger("300"),
                        new BigInteger("400"),
                        new BigInteger("400")
                );
        assertThat(page.totalElements()).isEqualTo(8);
    }

    @Test
    void findAll_whenNoTransactions_returnsEmptyPage() {
        transactionService.findAll(findAllReq(0, 10));

        ArgumentCaptor<Page<Transaction>> pageCaptor = ArgumentCaptor.captor();
        verify(presenter).presentFindAll(pageCaptor.capture());
        Page<Transaction> page = pageCaptor.getValue();

        assertThat(page.content()).isEmpty();
        assertThat(page.totalElements()).isZero();
    }

    @Test
    void findById_returnsMatchingTransaction() {
        var source = createAccountWithBalance("Alice", new BigInteger("1000"));
        var destination = createAccountWithBalance("Bob", new BigInteger("0"));

        var tx = transfer(source.getNumber(), destination.getNumber(), new BigInteger("250"));

        transactionService.findById(findByIdReq(tx.getId()));

        var txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(presenter).presentFind(txCaptor.capture());
        var found = txCaptor.getValue();

        assertThat(found.getId()).isEqualTo(tx.getId());
        assertThat(found.getAmount()).isEqualTo(new BigInteger("250"));
        assertThat(found.getFrom().getNumber()).isEqualTo(source.getNumber());
        assertThat(found.getTo().getNumber()).isEqualTo(destination.getNumber());
    }

    @Test
    void findById_nonExistent_throws() {
        assertThatThrownBy(() -> transactionService.findById(findByIdReq(999L)))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessageContaining("999");

        verify(presenter, never()).presentFind(any());
    }

    private Transaction transfer(String sourceNumber, String destinationNumber,
                                 BigInteger amount) {
        ownerService.transfer(transferReq(sourceNumber, destinationNumber, amount));

        var txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(ownerPresenter).presentTransfer(txCaptor.capture());
        var tx = txCaptor.getValue();
        clearInvocations(ownerPresenter);
        return tx;
    }

    private Account createAccountWithBalance(String ownerName, BigInteger initialBalance) {
        adminService.create(createReq(ownerName));

        var createCaptor = ArgumentCaptor.forClass(JpaAccount.class);
        verify(adminPresenter).presentCreate(createCaptor.capture());
        var created = createCaptor.getValue();
        clearInvocations(adminPresenter);

        if (initialBalance.signum() > 0) {
            adminService.deposit(depositReq(created.getId(), initialBalance));
            clearInvocations(adminPresenter);
        }
        return created;
    }

    private static OwnerTransactionFindAllReq findAllReq(int page, int size) {
        return new OwnerTransactionFindAllReq() {
            @Override
            public int getPage() {
                return page;
            }

            @Override
            public int getSize() {
                return size;
            }
        };
    }

    private static OwnerTransactionFindByIdReq findByIdReq(Long id) {
        return () -> id;
    }

    private static OwnerAccountTransferReq transferReq(String source, String destination,
                                                       BigInteger amount) {
        return new OwnerAccountTransferReq() {
            @Override
            public String getSourceNumber() {
                return source;
            }

            @Override
            public String getDestinationNumber() {
                return destination;
            }

            @Override
            public BigInteger getAmount() {
                return amount;
            }
        };
    }

    private static AdminAccountCreateReq createReq(String ownerName) {
        return () -> ownerName;
    }

    private static AdminAccountDepositReq depositReq(int id, BigInteger amount) {
        return new AdminAccountDepositReq() {
            @Override
            public int getId() {
                return id;
            }

            @Override
            public BigInteger getAmount() {
                return amount;
            }
        };
    }
}