package ir.maktabsharif.bank.console.menu;

import ir.maktabsharif.bank.account.usecase.admin.AdminAccountService;
import ir.maktabsharif.bank.console.FakeConsole;
import ir.maktabsharif.bank.page.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminMenuTest {

    private final FakeConsole console = new FakeConsole();

    @BeforeEach
    void setUp() {
        console.clear();
    }

    @Test
    void backFromRoot_returnsImmediately() {
        AdminAccountService svc = mock(AdminAccountService.class);
        var menu = new AdminMenu(console, _ -> svc);
        console.addInput("5");
        menu.show();
        assertThat(console.getOutput()).contains("Back to Main Menu");
        verifyNoInteractions(svc);
    }

    @Test
    void createAccount_invokesServiceCreate() {
        AdminAccountService svc = mock(AdminAccountService.class);
        var menu = new AdminMenu(console, _ -> svc);
        // 1=create, owner name, then 5=back
        console.addInput("1");
        console.addInput("5");
        menu.show();
        verify(svc).create(any());
    }

    @Test
    void showAllAccounts_paginationFlow() {
        AdminAccountService svc = mock(AdminAccountService.class);
        @SuppressWarnings("unchecked")
        Page<ir.maktabsharif.bank.account.Account> page = mock(Page.class);
        when(page.pageNumber()).thenReturn(0);
        when(page.totalElements()).thenReturn(10L);
        when(page.size()).thenReturn(10);
        when(page.content()).thenReturn(List.of());
        var pageable = mock(ir.maktabsharif.bank.page.Pageable.class);
        when(pageable.size()).thenReturn(10);
        when(page.pageable()).thenReturn(pageable);
        doNothing().when(svc).findAll(any());

        var menu = new AdminMenu(console, p -> {
            p.presentFindAll(page);
            return svc;
        });
        // 2=show all, n=next, b=back, 5=back
        console.addInput("2");
        console.addInput("n");
        console.addInput("b");
        console.addInput("5");
        menu.show();

        verify(svc, times(1)).findAll(any());
        assertThat(console.getOutput()).contains("There is no next page").asBoolean().isFalse();
    }

    @Test
    void showAllAccounts_previousAtFirstPageWarns() {
        AdminAccountService svc = mock(AdminAccountService.class);
        @SuppressWarnings("unchecked")
        Page<ir.maktabsharif.bank.account.Account> page = mock(Page.class);
        when(page.pageNumber()).thenReturn(0);
        when(page.totalElements()).thenReturn(20L);
        when(page.size()).thenReturn(10);
        when(page.content()).thenReturn(List.of());
        when(page.pageable()).thenReturn(mock(ir.maktabsharif.bank.page.Pageable.class));

        var menu = new AdminMenu(console, p -> {
            p.presentFindAll(page);
            return svc;
        });
        console.addInput("2");
        console.addInput("p");
        console.addInput("b");
        console.addInput("5");
        menu.show();
        assertThat(console.getOutput()).contains("There is no previous page");
    }

    @Test
    void invalidInput_reportsError() {
        AdminAccountService svc = mock(AdminAccountService.class);
        var menu = new AdminMenu(console, _ -> svc);
        console.addInput("99");
        console.addInput("5");
        menu.show();
        assertThat(console.getOutput()).contains("Invalid input!");
    }
}