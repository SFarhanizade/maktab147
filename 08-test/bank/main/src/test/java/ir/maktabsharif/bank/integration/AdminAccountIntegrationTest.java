package ir.maktabsharif.bank.integration;

import ir.maktabsharif.bank.account.Account;
import ir.maktabsharif.bank.account.JpaAccount;
import ir.maktabsharif.bank.account.JpaAccount_;
import ir.maktabsharif.bank.account.usecase.admin.AdminAccountPresenter;
import ir.maktabsharif.bank.account.usecase.admin.AdminAccountService;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountCreateReq;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountDepositReq;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountFindAllReq;
import ir.maktabsharif.bank.account.usecase.admin.req.AdminAccountFindReq;
import ir.maktabsharif.bank.page.Page;
import ir.maktabsharif.bank.util.TestUtil;
import ir.maktabsharif.bank.util.WithPostgres;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminAccountIntegrationTest extends WithPostgres {
    private AdminAccountService service;

    @Mock
    private AdminAccountPresenter presenter;

    @BeforeEach
    void beforeEach() {
        context.clear();
        context.setBean(AdminAccountPresenter.class, presenter);
        service = context.getBean(AdminAccountService.class);
        TestUtil.truncateAllTables(postgres);
        reset(presenter);
    }

    @Test
    void testCreateAndFindAll() {
        var owner = "Bob";
        service.create(createReq(owner));

        var accountCaptor = ArgumentCaptor.forClass(JpaAccount.class);
        verify(presenter).presentCreate(accountCaptor.capture());
        var account1 = accountCaptor.getValue();
        assertThat(account1.getBalance()).isEqualTo(BigInteger.ZERO);
        assertThat(account1.getOwnerName()).isEqualTo(owner);

        service.findAll(new PageReq(0, 10));
        ArgumentCaptor<Page<Account>> accountPageCaptor = ArgumentCaptor.captor();
        verify(presenter).presentFindAll(accountPageCaptor.capture());
        var accountPage = accountPageCaptor.getValue();
        assertThat(accountPage)
                .satisfies(p -> assertThat(p.size()).isEqualTo(1))
                .satisfies(p -> assertThat(p.content())
                        .asInstanceOf(LIST)
                        .first()
                        .extracting(JpaAccount_.OWNER_NAME)
                        .isEqualTo(owner));
    }

    @Test
    void testDeposit() {
        var owner = "Alice";
        service.create(createReq(owner));

        var createCaptor = ArgumentCaptor.forClass(Account.class);
        verify(presenter).presentCreate(createCaptor.capture());
        var created = createCaptor.getValue();
        var accountId = created.getId();

        service.deposit(depositReq(accountId, new BigInteger("100")));

        var depositCaptor = ArgumentCaptor.forClass(Account.class);
        verify(presenter).presentDeposit(depositCaptor.capture());
        var deposited = depositCaptor.getValue();
        assertThat(deposited.getId()).isEqualTo(accountId);
        assertThat(deposited.getBalance()).isEqualTo(100);
    }

    @Test
    void testDepositToNonExistentAccount() {
        var nonExistentId = 99999;
        assertThatThrownBy(() -> service.deposit(depositReq(nonExistentId, BigInteger.TEN)))
                .hasMessageContaining(String.valueOf(nonExistentId));
        verify(presenter, never()).presentDeposit(any());
    }

    @Test
    void testFind() {
        var owner = "Charlie";
        service.create(createReq(owner));

        var createCaptor = ArgumentCaptor.forClass(Account.class);
        verify(presenter).presentCreate(createCaptor.capture());
        var created = createCaptor.getValue();

        service.find(findReq(created.getId()));

        var findCaptor = ArgumentCaptor.forClass(JpaAccount.class);
        verify(presenter).presentFind(findCaptor.capture());
        var found = findCaptor.getValue();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getOwnerName()).isEqualTo(owner);
    }

    @Test
    void testFindNonExistentAccount() {
        var nonExistentId = 99999;
        assertThatThrownBy(() -> service.find(findReq(nonExistentId)))
                .hasMessageContaining(String.valueOf(nonExistentId));
        verify(presenter, never()).presentFind(any());
    }

    @Test
    void testFindAllPagination() {
        for (int i = 0; i < 5; i++) {
            service.create(createReq("Owner" + i));
        }
        clearInvocations(presenter);

        service.findAll(new PageReq(0, 10));

        ArgumentCaptor<Page<Account>> pageCaptor = ArgumentCaptor.captor();
        verify(presenter).presentFindAll(pageCaptor.capture());
        var page = pageCaptor.getValue();
        assertThat(page.size()).isEqualTo(5);
        assertThat(page.content()).hasSize(5);
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

    private static AdminAccountFindReq findReq(int id) {
        return () -> id;
    }

    record PageReq(@Getter int page, @Getter int size) implements AdminAccountFindAllReq {
    }
}