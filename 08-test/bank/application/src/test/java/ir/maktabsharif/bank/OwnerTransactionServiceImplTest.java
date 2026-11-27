package ir.maktabsharif.bank;

import ir.maktabsharif.bank.common.TransactionNotFoundException;
import ir.maktabsharif.bank.page.Page;
import ir.maktabsharif.bank.page.Pageable;
import ir.maktabsharif.bank.transaction.Transaction;
import ir.maktabsharif.bank.transaction.TransactionRepository;
import ir.maktabsharif.bank.transaction.usecase.owner.OwnerTransactionPresenter;
import ir.maktabsharif.bank.transaction.usecase.owner.OwnerTransactionServiceImpl;
import ir.maktabsharif.bank.transaction.usecase.owner.req.OwnerTransactionFindAllReq;
import ir.maktabsharif.bank.transaction.usecase.owner.req.OwnerTransactionFindByIdReq;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerTransactionServiceImplTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private OwnerTransactionPresenter presenter;

    @InjectMocks
    private OwnerTransactionServiceImpl service;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = mock(Transaction.class);
    }


    record OwnerTransactionFindAllTestReq(@Getter int page, @Getter int size)
            implements OwnerTransactionFindAllReq {
    }

    @Test
    @DisplayName("findAll: should query repository with pageable and present results")
    void findAll_shouldCallRepositoryAndPresent() {
        OwnerTransactionFindAllReq req = new OwnerTransactionFindAllTestReq(2, 10);

        @SuppressWarnings("unchecked")
        Page<Transaction> page = mock(Page.class);

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
        OwnerTransactionFindAllReq req = new OwnerTransactionFindAllTestReq(0, 5);

        @SuppressWarnings("unchecked")
        Page<Transaction> emptyPage = mock(Page.class);
        when(repository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        service.findAll(req);

        verify(presenter).presentFindAll(emptyPage);
    }

    @Test
    @DisplayName("findAll: should not call presenter when repository throws")
    void findAll_shouldNotCallPresenterOnRepositoryFailure() {
        OwnerTransactionFindAllReq req = new OwnerTransactionFindAllTestReq(0, 10);

        when(repository.findAll(any(Pageable.class)))
                .thenThrow(new RuntimeException("DB down"));

        assertThrows(RuntimeException.class, () -> service.findAll(req));
        verify(presenter, never()).presentFindAll(any());
    }

    @Test
    @DisplayName("findById: should present transaction when found")
    void findById_shouldPresentTransactionWhenFound() {
        OwnerTransactionFindByIdReq req = () -> 42L;

        when(repository.findById(req.getId())).thenReturn(Optional.of(transaction));

        service.findById(req);

        verify(repository, times(1)).findById(req.getId());
        verify(presenter, times(1)).presentFind(transaction);
    }

    @Test
    @DisplayName("findById: should throw TransactionNotFoundException when not found")
    void findById_shouldThrowWhenNotFound() {
        OwnerTransactionFindByIdReq req = () -> 99L;

        when(repository.findById(req.getId())).thenReturn(Optional.empty());

        TransactionNotFoundException ex = assertThrows(
                TransactionNotFoundException.class,
                () -> service.findById(req)
        );

        assertTrue(ex.getMessage().contains("99"),
                "Exception message should contain the missing id");
        verify(presenter, never()).presentFind(any());
    }

    @Test
    @DisplayName("findById: should not call presenter when repository throws")
    void findById_shouldNotCallPresenterOnRepositoryFailure() {
        OwnerTransactionFindByIdReq req = () -> 7L;

        when(repository.findById(req.getId())).thenThrow(new RuntimeException("DB down"));

        assertThrows(RuntimeException.class, () -> service.findById(req));
        verify(presenter, never()).presentFind(any());
    }
}