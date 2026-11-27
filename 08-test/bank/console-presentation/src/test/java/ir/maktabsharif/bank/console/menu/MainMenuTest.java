package ir.maktabsharif.bank.console.menu;

import ir.maktabsharif.bank.console.FakeConsole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class MainMenuTest {

    private final FakeConsole console = new FakeConsole();

    @Mock
    private AdminMenu adminMenu;

    @Mock
    private OwnerMenu ownerMenu;

    private MainMenu undertest;

    @BeforeEach
    void setUp() {
        undertest = new MainMenu(console, adminMenu, ownerMenu);
    }

    @Test
    void exitChoice_returns() {
        console.addInput("3");
        undertest.show();
        assertThat(console.getOutput()).contains("Main Menu");
        verifyNoInteractions(adminMenu, ownerMenu);
    }

    @Test
    void adminChoice_delegatesToAdminMenu() {
        console.addInput("1");
        console.addInput("3");
        undertest.show();
        verify(adminMenu).show();
        verifyNoInteractions(ownerMenu);
    }

    @Test
    void ownerChoice_delegatesToOwnerMenu() {
        console.addInput("2");
        console.addInput("3");
        undertest.show();
        verify(ownerMenu).show();
        verifyNoInteractions(adminMenu);
    }

    @Test
    void invalidChoice_printsError() {
        console.addInput("0");
        console.addInput("3");
        undertest.show();
        assertThat(console.getOutput()).contains("Invalid input!");
    }
}