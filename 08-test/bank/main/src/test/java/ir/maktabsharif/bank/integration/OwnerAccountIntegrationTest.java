package ir.maktabsharif.bank.integration;

import ir.maktabsharif.bank.account.Account;
import ir.maktabsharif.bank.account.JpaAccount;
import ir.maktabsharif.bank.account.usecase.admin.AdminAccountPresenter;
import ir.maktabsharif.bank.account.usecase.admin.AdminAccountService;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountCreateReq;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountDepositReq;
import ir.maktabsharif.bank.account.usecase.owner.InsufficientBalanceException;
import ir.maktabsharif.bank.account.usecase.owner.OwnerAccountPresenter;
import ir.maktabsharif.bank.account.usecase.owner.OwnerAccountService;
import ir.maktabsharif.bank.account.usecase.owner.req.OwnerAccountGetBalanceReq;
import ir.maktabsharif.bank.account.usecase.owner.req.OwnerAccountTransferReq;
import ir.maktabsharif.bank.transaction.Transaction;
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
public class OwnerAccountIntegrationTest extends WithPostgres {
    private OwnerAccountService ownerService;
    private AdminAccountService adminService;

    @Mock
    private OwnerAccountPresenter presenter;

    @Mock
    private AdminAccountPresenter adminPresenter;

    @BeforeEach
    void beforeEach() {
        context.clear();
        context.setBean(OwnerAccountPresenter.class, presenter);
        context.setBean(AdminAccountPresenter.class, adminPresenter);

        ownerService = context.getBean(OwnerAccountService.class);
        adminService = context.getBean(AdminAccountService.class);

        TestUtil.truncateAllTables(postgres);
        reset(presenter, adminPresenter);
    }

    @Test
    void getBalance_returnsCurrentBalance() {
        var account = createAccountWithBalance("Alice", new BigInteger("250"));

        ownerService.getBalance(getBalanceReq(account.getNumber()));

        var balanceCaptor = ArgumentCaptor.forClass(BigInteger.class);
        verify(presenter).presentGetBalance(balanceCaptor.capture());
        assertThat(balanceCaptor.getValue()).isEqualTo(new BigInteger("250"));
    }

    @Test
    void getBalance_nonExistentAccount_throws() {
        var number = "0000000000";

        assertThatThrownBy(() -> ownerService.getBalance(getBalanceReq(number)))
                .hasMessageContaining(number);
        verify(presenter, never()).presentGetBalance(any());
    }

    @Test
    void transfer_movesFundsAndPresentsOutTransaction() {
        var source = createAccountWithBalance("Bob", new BigInteger("1000"));
        var destination = createAccountWithBalance("Carol", new BigInteger("100"));

        ownerService.transfer(transferReq(source.getNumber(), destination.getNumber(),
                new BigInteger("300")));

        var txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(presenter).presentTransfer(txCaptor.capture());
        var tx = txCaptor.getValue();
        assertThat(tx.getAmount()).isEqualTo(new BigInteger("300"));
        assertThat(tx.getFrom().getNumber()).isEqualTo(source.getNumber());
        assertThat(tx.getTo().getNumber()).isEqualTo(destination.getNumber());

        assertThat(balanceOf(source.getNumber())).isEqualTo(new BigInteger("700"));
        assertThat(balanceOf(destination.getNumber())).isEqualTo(new BigInteger("400"));
    }

    @Test
    void transfer_insufficientBalance_throwsAndLeavesBalancesUntouched() {
        var source = createAccountWithBalance("Dave", new BigInteger("50"));
        var destination = createAccountWithBalance("Eve", new BigInteger("0"));

        assertThatThrownBy(() -> ownerService.transfer(
                transferReq(source.getNumber(), destination.getNumber(), new BigInteger("100"))))
                .isInstanceOf(InsufficientBalanceException.class);

        verify(presenter, never()).presentTransfer(any());

        assertThat(balanceOf(source.getNumber())).isEqualTo(new BigInteger("50"));
        assertThat(balanceOf(destination.getNumber())).isEqualTo(new BigInteger("0"));
    }

    @Test
    void transfer_exactBalance_succeeds() {
        var source = createAccountWithBalance("Frank", new BigInteger("75"));
        var destination = createAccountWithBalance("Grace", new BigInteger("0"));

        ownerService.transfer(transferReq(source.getNumber(), destination.getNumber(),
                new BigInteger("75")));

        verify(presenter).presentTransfer(any());
        assertThat(balanceOf(source.getNumber())).isEqualTo(BigInteger.ZERO);
        assertThat(balanceOf(destination.getNumber())).isEqualTo(new BigInteger("75"));
    }

    @Test
    void transfer_sourceAccountNotFound_throws() {
        var destination = createAccountWithBalance("Heidi", BigInteger.TEN);

        assertThatThrownBy(() -> ownerService.transfer(
                transferReq("99", destination.getNumber(), BigInteger.TEN)))
                .hasMessageContaining("99");

        verify(presenter, never()).presentTransfer(any());
    }

    @Test
    void transfer_destinationAccountNotFound_throws() {
        var source = createAccountWithBalance("Ivan", BigInteger.TEN);

        assertThatThrownBy(() -> ownerService.transfer(
                transferReq(source.getNumber(), "88", BigInteger.TEN)))
                .hasMessageContaining("88");

        verify(presenter, never()).presentTransfer(any());
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

    private BigInteger balanceOf(String accountNumber) {
        var captor = ArgumentCaptor.forClass(BigInteger.class);
        ownerService.getBalance(getBalanceReq(accountNumber));
        verify(presenter).presentGetBalance(captor.capture());
        clearInvocations(presenter);
        return captor.getValue();
    }

    private static OwnerAccountGetBalanceReq getBalanceReq(String number) {
        return () -> number;
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