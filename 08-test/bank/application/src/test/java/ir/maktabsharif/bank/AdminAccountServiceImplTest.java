package ir.maktabsharif.bank;

import ir.maktabsharif.bank.account.Account;
import ir.maktabsharif.bank.account.AccountFactory;
import ir.maktabsharif.bank.account.AccountRepository;
import ir.maktabsharif.bank.account.usecase.admin.AdminAccountPresenter;
import ir.maktabsharif.bank.account.usecase.admin.AdminAccountServiceImpl;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountCreateReq;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountDepositReq;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountFindAllReq;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountFindReq;
import ir.maktabsharif.bank.base.TransactionManager;
import ir.maktabsharif.bank.common.AccountNotFoundException;
import ir.maktabsharif.bank.page.Page;
import ir.maktabsharif.bank.page.Pageable;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAccountServiceImplTest {

    @Mock
    private AccountFactory factory;

    @Mock
    private AccountRepository repository;

    @Mock
    private AdminAccountPresenter presenter;

    @Mock
    private TransactionManager transactionManager;

    @InjectMocks
    private AdminAccountServiceImpl service;

    private Account account;

    @BeforeEach
    void setUp() {
        account = mock(Account.class);
    }

    @Test
    @DisplayName("create: should create account, save it and present it")
    void create_shouldCreateSaveAndPresent() {
        //Given
        AdminAccountCreateReq req = () -> "Alice";

        when(factory.createAccount(req.getOwnerName()))
                .thenReturn(account);

        //When
        service.create(req);

        //Then
        var ownerName = req.getOwnerName();
        verify(factory, times(1)).createAccount(ownerName);

        ArgumentCaptor<Account> saveCaptor = ArgumentCaptor.forClass(Account.class);
        verify(repository, times(1)).save(saveCaptor.capture());
        assertSame(account, saveCaptor.getValue());

        verify(presenter, times(1)).presentCreate(account);
    }

    @Test
    @DisplayName("create: should propagate exception if factory fails")
    void create_shouldPropagateFactoryException() {
        AdminAccountCreateReq req = () -> "Bob";

        when(factory.createAccount(req.getOwnerName()))
                .thenThrow(new IllegalArgumentException("Invalid owner name"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(req)
        );

        assertEquals("Invalid owner name", ex.getMessage());
        verify(repository, never()).save(any());
        verify(presenter, never()).presentCreate(any());
    }

    record AdminAccountFindAllTestReq(@Getter int page, @Getter int size) implements AdminAccountFindAllReq {
    }

    @Test
    @DisplayName("findAll: should query repository with pageable and present results")
    void findAll_shouldCallRepositoryAndPresent() {
        AdminAccountFindAllReq req = new AdminAccountFindAllTestReq(2, 10);

        @SuppressWarnings("unchecked")
        Page<Account> page = mock(Page.class);

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        service.findAll(req);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository, times(1)).findAll(pageableCaptor.capture());

        Pageable captured = pageableCaptor.getValue();
        assertEquals(2, captured.page());
        assertEquals(10, captured.size());

        verify(presenter, times(1)).presentFindAll(page);
    }

    @Test
    @DisplayName("findAll: should present empty page when repository returns empty page")
    void findAll_shouldPresentEmptyPage() {
        AdminAccountFindAllReq req = new AdminAccountFindAllTestReq(0, 5);

        @SuppressWarnings("unchecked")
        Page<Account> emptyPage = mock(Page.class);
        when(repository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        service.findAll(req);

        verify(presenter).presentFindAll(emptyPage);
    }

    @Test
    @DisplayName("find: should present account when found")
    void find_shouldPresentAccountWhenFound() {
        AdminAccountFindReq req = () -> 42;

        when(repository.findById(req.getId())).thenReturn(Optional.of(account));

        service.find(req);

        verify(repository, times(1)).findById(req.getId());
        verify(presenter, times(1)).presentFind(account);
    }

    @Test
    @DisplayName("find: should throw AccountNotFoundException when account not found")
    void find_shouldThrowWhenNotFound() {
        AdminAccountFindReq req = () -> 99;

        when(repository.findById(req.getId())).thenReturn(Optional.empty());

        AccountNotFoundException ex = assertThrows(
                AccountNotFoundException.class,
                () -> service.find(req)
        );

        assertTrue(ex.getMessage().contains("99"),
                "Exception message should contain the missing id");
        verify(presenter, never()).presentFind(any());
    }

    @Test
    @DisplayName("find: should not call presenter when repository throws")
    void find_shouldNotCallPresenterOnRepositoryFailure() {
        AdminAccountFindReq req = () -> 7;

        when(repository.findById(req.getId())).thenThrow(new RuntimeException("DB down"));

        assertThrows(RuntimeException.class, () -> service.find(req));
        verify(presenter, never()).presentFind(any());
    }

    record AdminAccountDepositTestReq(@Getter int id, @Getter BigInteger amount) implements AdminAccountDepositReq {
    }

    @Test
    @DisplayName("deposit: should add amount to balance and save within transaction")
    void deposit_shouldAddAmountAndSaveInTransaction() {
        AdminAccountDepositReq req = new AdminAccountDepositTestReq(1, new BigInteger("100"));
        BigInteger initialBalance = new BigInteger("50");
        BigInteger expectedBalance = new BigInteger("150");

        when(account.getBalance()).thenReturn(initialBalance);
        when(repository.findById(req.getId())).thenReturn(Optional.of(account));

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transactionManager).execute(any(Runnable.class));

        service.deposit(req);

        verify(transactionManager, times(1)).execute(any(Runnable.class));
        verify(repository, times(1)).findById(req.getId());
        verify(account, times(1)).setBalance(expectedBalance);
        verify(repository, times(1)).save(account);
    }

    @Test
    @DisplayName("deposit: should throw AccountNotFoundException and not save when account not found")
    void deposit_shouldThrowWhenAccountNotFound() {
        AdminAccountDepositReq req = new AdminAccountDepositTestReq(99, new BigInteger("25"));

        when(repository.findById(req.getId())).thenReturn(Optional.empty());

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transactionManager).execute(any(Runnable.class));

        AccountNotFoundException ex = assertThrows(
                AccountNotFoundException.class,
                () -> service.deposit(req)
        );

        assertTrue(ex.getMessage().contains("99"),
                "Exception message should contain the missing id");
        verify(repository, never()).save(any());
        verify(account, never()).setBalance(any());
    }

    @Test
    @DisplayName("deposit: should not save when transaction manager throws")
    void deposit_shouldNotSaveWhenTransactionFails() {
        AdminAccountDepositReq req = new AdminAccountDepositTestReq(5, new BigInteger("10"));

        doThrow(new RuntimeException("Transaction failed"))
                .when(transactionManager).execute(any(Runnable.class));

        assertThrows(RuntimeException.class, () -> service.deposit(req));
        verify(repository, never()).save(any());
    }
}