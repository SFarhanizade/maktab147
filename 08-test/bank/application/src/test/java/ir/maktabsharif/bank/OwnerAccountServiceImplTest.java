package ir.maktabsharif.bank;

import ir.maktabsharif.bank.account.Account;
import ir.maktabsharif.bank.account.AccountRepository;
import ir.maktabsharif.bank.account.usecase.owner.InsufficientBalanceException;
import ir.maktabsharif.bank.account.usecase.owner.OwnerAccountPresenter;
import ir.maktabsharif.bank.account.usecase.owner.OwnerAccountServiceImpl;
import ir.maktabsharif.bank.account.usecase.owner.req.OwnerAccountGetBalanceReq;
import ir.maktabsharif.bank.account.usecase.owner.req.OwnerAccountTransferReq;
import ir.maktabsharif.bank.base.TransactionManager;
import ir.maktabsharif.bank.common.AccountNotFoundException;
import ir.maktabsharif.bank.transaction.Transaction;
import ir.maktabsharif.bank.transaction.TransactionFactory;
import ir.maktabsharif.bank.transaction.TransactionRepository;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerAccountServiceImplTest {

    @Mock
    private AccountRepository repository;
    @Mock
    private TransactionManager transactionManager;
    @Mock
    private OwnerAccountPresenter presenter;
    @Mock
    private TransactionFactory transactionFactory;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private OwnerAccountServiceImpl service;

    private Account sourceAccount;
    private Account destinationAccount;

    private static final BigInteger AMOUNT = new BigInteger("100");
    private static final BigInteger SUFFICIENT_BALANCE = new BigInteger("500");

    @BeforeEach
    void setUp() {
        sourceAccount = mock(Account.class);
        destinationAccount = mock(Account.class);
    }

    private void runTransactionActionSynchronously() {
        doAnswer(invocation -> {
            Runnable action = invocation.getArgument(0);
            action.run();
            return null;
        }).when(transactionManager).execute(any(Runnable.class));
    }

    @Test
    @DisplayName("getBalance: should look up by number and present the balance")
    void getBalance_shouldPresentBalanceWhenAccountFound() {
        OwnerAccountGetBalanceReq req = () -> "ACC-123";
        BigInteger balance = new BigInteger("1500");

        when(repository.findByNumber("ACC-123")).thenReturn(Optional.of(sourceAccount));
        when(sourceAccount.getBalance()).thenReturn(balance);

        service.getBalance(req);

        verify(repository).findByNumber("ACC-123");
        verify(sourceAccount).getBalance();
        verify(presenter).presentGetBalance(balance);
        verifyNoMoreInteractions(repository, presenter);
    }

    @Test
    @DisplayName("getBalance: should present zero balance when account balance is zero")
    void getBalance_shouldPresentZeroBalance() {
        OwnerAccountGetBalanceReq req = () -> "ACC-000";

        when(repository.findByNumber("ACC-000")).thenReturn(Optional.of(sourceAccount));
        when(sourceAccount.getBalance()).thenReturn(BigInteger.ZERO);

        service.getBalance(req);

        verify(presenter).presentGetBalance(BigInteger.ZERO);
    }

    @Test
    @DisplayName("getBalance: should throw AccountNotFoundException when account not found")
    void getBalance_shouldThrowWhenNotFound() {
        OwnerAccountGetBalanceReq req = () -> "MISSING-999";

        when(repository.findByNumber("MISSING-999")).thenReturn(Optional.empty());

        AccountNotFoundException ex = assertThrows(
                AccountNotFoundException.class,
                () -> service.getBalance(req)
        );

        assertTrue(ex.getMessage().contains("MISSING-999"));
        verify(presenter, never()).presentGetBalance(any());
        verify(sourceAccount, never()).getBalance();
    }

    @Test
    @DisplayName("getBalance: should not call presenter when repository throws")
    void getBalance_shouldNotCallPresenterOnRepositoryFailure() {
        OwnerAccountGetBalanceReq req = () -> "ACC-ERR";

        when(repository.findByNumber("ACC-ERR"))
                .thenThrow(new RuntimeException("DB down"));

        assertThrows(RuntimeException.class, () -> service.getBalance(req));

        verify(presenter, never()).presentGetBalance(any());
    }

    record OwnerAccountTransferTestReq(@Getter String sourceNumber,
                                       @Getter String destinationNumber,
                                       @Getter BigInteger amount) implements OwnerAccountTransferReq {
    }

    @Test
    @DisplayName("transfer: should create both transactions, save them, and present the OUT transaction when balance is sufficient")
    void transfer_shouldSucceedWhenBalanceSufficient() {
        OwnerAccountTransferReq req = new OwnerAccountTransferTestReq("ACC-SRC", "ACC-DST", AMOUNT);

        Transaction outTransaction = mock(Transaction.class);
        Transaction inTransaction = mock(Transaction.class);

        when(repository.findByNumber("ACC-SRC")).thenReturn(Optional.of(sourceAccount));
        when(repository.findByNumber("ACC-DST")).thenReturn(Optional.of(destinationAccount));

        when(sourceAccount.getBalance()).thenReturn(SUFFICIENT_BALANCE);
        when(destinationAccount.getBalance()).thenReturn(new BigInteger("0"));

        when(transactionFactory.createOutTransaction(sourceAccount, destinationAccount, AMOUNT))
                .thenReturn(outTransaction);
        when(transactionFactory.createInTransaction(destinationAccount, sourceAccount, AMOUNT))
                .thenReturn(inTransaction);

        runTransactionActionSynchronously();

        service.transfer(req);

        verify(transactionFactory).createOutTransaction(sourceAccount, destinationAccount, AMOUNT);
        verify(transactionFactory).createInTransaction(destinationAccount, sourceAccount, AMOUNT);

        verify(transactionRepository).save(outTransaction);
        verify(transactionRepository).save(inTransaction);
        verify(transactionManager).execute(any(Runnable.class));

        verify(presenter).presentTransfer(outTransaction);
        verify(presenter, never()).presentTransfer(inTransaction);
    }

    @Test
    @DisplayName("transfer: should succeed when balance exactly equals the amount")
    void transfer_shouldSucceedWhenBalanceEqualsAmount() {
        OwnerAccountTransferReq req = new OwnerAccountTransferTestReq("ACC-SRC", "ACC-DST", AMOUNT);

        Transaction outTransaction = mock(Transaction.class);
        Transaction inTransaction = mock(Transaction.class);

        when(repository.findByNumber("ACC-SRC")).thenReturn(Optional.of(sourceAccount));
        when(repository.findByNumber("ACC-DST")).thenReturn(Optional.of(destinationAccount));

        when(sourceAccount.getBalance()).thenReturn(AMOUNT); // balance == amount
        when(destinationAccount.getBalance()).thenReturn(new BigInteger("0"));

        when(transactionFactory.createOutTransaction(any(), any(), any())).thenReturn(outTransaction);
        when(transactionFactory.createInTransaction(any(), any(), any())).thenReturn(inTransaction);

        runTransactionActionSynchronously();

        service.transfer(req);

        verify(transactionRepository).save(outTransaction);
        verify(transactionRepository).save(inTransaction);
        verify(presenter).presentTransfer(outTransaction);
    }

    @Test
    @DisplayName("transfer: should save OUT transaction before IN transaction")
    void transfer_shouldSaveOutBeforeIn() {
        OwnerAccountTransferReq req = new OwnerAccountTransferTestReq("ACC-SRC", "ACC-DST", AMOUNT);

        Transaction outTransaction = mock(Transaction.class);
        Transaction inTransaction = mock(Transaction.class);

        when(repository.findByNumber("ACC-SRC")).thenReturn(Optional.of(sourceAccount));
        when(repository.findByNumber("ACC-DST")).thenReturn(Optional.of(destinationAccount));
        when(sourceAccount.getBalance()).thenReturn(SUFFICIENT_BALANCE);
        when(destinationAccount.getBalance()).thenReturn(new BigInteger("0"));

        when(transactionFactory.createOutTransaction(sourceAccount, destinationAccount, AMOUNT))
                .thenReturn(outTransaction);
        when(transactionFactory.createInTransaction(destinationAccount, sourceAccount, AMOUNT))
                .thenReturn(inTransaction);

        runTransactionActionSynchronously();

        service.transfer(req);

        InOrder inOrder = inOrder(transactionRepository);
        inOrder.verify(transactionRepository).save(outTransaction);
        inOrder.verify(transactionRepository).save(inTransaction);
    }


    @Test
    @DisplayName("transfer: should throw InsufficientBalanceException and not save when balance is insufficient")
    void transfer_shouldThrowWhenBalanceInsufficient() {
        OwnerAccountTransferReq req = new OwnerAccountTransferTestReq("ACC-SRC", "ACC-DST", AMOUNT);

        when(repository.findByNumber("ACC-SRC")).thenReturn(Optional.of(sourceAccount));
        when(repository.findByNumber("ACC-DST")).thenReturn(Optional.of(destinationAccount));

        // balance (50) < amount (100)
        when(sourceAccount.getBalance()).thenReturn(new BigInteger("50"));

        Transaction outTransaction = mock(Transaction.class);
        Transaction inTransaction = mock(Transaction.class);
        when(transactionFactory.createOutTransaction(any(), any(), any())).thenReturn(outTransaction);
        when(transactionFactory.createInTransaction(any(), any(), any())).thenReturn(inTransaction);

        runTransactionActionSynchronously();

        InsufficientBalanceException ex = assertThrows(
                InsufficientBalanceException.class,
                () -> service.transfer(req)
        );

        verify(transactionRepository, never()).save(any());
        verify(presenter, never()).presentTransfer(any());
    }

    @Test
    @DisplayName("transfer: should throw InsufficientBalanceException when balance is just below amount by one")
    void transfer_shouldThrowWhenBalanceJustBelowAmount() {
        OwnerAccountTransferReq req = new OwnerAccountTransferTestReq("ACC-SRC", "ACC-DST", AMOUNT);

        when(repository.findByNumber("ACC-SRC")).thenReturn(Optional.of(sourceAccount));
        when(repository.findByNumber("ACC-DST")).thenReturn(Optional.of(destinationAccount));

        when(sourceAccount.getBalance()).thenReturn(AMOUNT.subtract(BigInteger.ONE));

        when(transactionFactory.createOutTransaction(any(), any(), any()))
                .thenReturn(mock(Transaction.class));
        when(transactionFactory.createInTransaction(any(), any(), any()))
                .thenReturn(mock(Transaction.class));

        runTransactionActionSynchronously();

        assertThrows(InsufficientBalanceException.class, () -> service.transfer(req));
        verify(transactionRepository, never()).save(any());
        verify(presenter, never()).presentTransfer(any());
    }


    @Test
    @DisplayName("transfer: should throw AccountNotFoundException when source account not found")
    void transfer_shouldThrowWhenSourceNotFound() {
        OwnerAccountTransferReq req = new OwnerAccountTransferTestReq("MISSING-SRC", "ACC-DST", AMOUNT);

        when(repository.findByNumber("MISSING-SRC")).thenReturn(Optional.empty());

        AccountNotFoundException ex = assertThrows(
                AccountNotFoundException.class,
                () -> service.transfer(req)
        );

        assertTrue(ex.getMessage().contains("MISSING-SRC"));
        verify(repository, never()).findByNumber("ACC-DST");
        verifyNoInteractions(transactionFactory, transactionManager, transactionRepository);
        verify(presenter, never()).presentTransfer(any());
    }

    @Test
    @DisplayName("transfer: should throw AccountNotFoundException when destination account not found")
    void transfer_shouldThrowWhenDestinationNotFound() {
        OwnerAccountTransferReq req = new OwnerAccountTransferTestReq("ACC-SRC", "MISSING-DST", AMOUNT);

        when(repository.findByNumber("ACC-SRC")).thenReturn(Optional.of(sourceAccount));
        when(repository.findByNumber("MISSING-DST")).thenReturn(Optional.empty());

        AccountNotFoundException ex = assertThrows(
                AccountNotFoundException.class,
                () -> service.transfer(req)
        );

        assertTrue(ex.getMessage().contains("MISSING-DST"));
        verifyNoInteractions(transactionFactory, transactionManager, transactionRepository);
        verify(presenter, never()).presentTransfer(any());
    }

    @Test
    @DisplayName("transfer: should not save or throw when transaction manager does not run the action")
    void transfer_shouldNotSaveWhenActionNotRun() {
        OwnerAccountTransferReq req = new OwnerAccountTransferTestReq("ACC-SRC", "ACC-DST", AMOUNT);

        when(repository.findByNumber("ACC-SRC")).thenReturn(Optional.of(sourceAccount));
        when(repository.findByNumber("ACC-DST")).thenReturn(Optional.of(destinationAccount));

        Transaction outTransaction = mock(Transaction.class);
        when(transactionFactory.createOutTransaction(any(), any(), any())).thenReturn(outTransaction);
        when(transactionFactory.createInTransaction(any(), any(), any())).thenReturn(mock(Transaction.class));

        doNothing().when(transactionManager).execute(any(Runnable.class));

        assertDoesNotThrow(() -> service.transfer(req));

        verify(transactionRepository, never()).save(any());
        verify(presenter).presentTransfer(outTransaction);
    }

    @Test
    @DisplayName("transfer: should propagate exception from transaction manager and not present")
    void transfer_shouldPropagateExceptionFromTransactionManager() {
        OwnerAccountTransferReq req = new OwnerAccountTransferTestReq("ACC-SRC", "ACC-DST", AMOUNT);

        when(repository.findByNumber("ACC-SRC")).thenReturn(Optional.of(sourceAccount));
        when(repository.findByNumber("ACC-DST")).thenReturn(Optional.of(destinationAccount));

        when(transactionFactory.createOutTransaction(any(), any(), any()))
                .thenReturn(mock(Transaction.class));
        when(transactionFactory.createInTransaction(any(), any(), any()))
                .thenReturn(mock(Transaction.class));

        doThrow(new RuntimeException("tx commit failed"))
                .when(transactionManager).execute(any(Runnable.class));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.transfer(req)
        );

        assertEquals("tx commit failed", ex.getMessage());
        verify(transactionRepository, never()).save(any());
        verify(presenter, never()).presentTransfer(any());
    }
}