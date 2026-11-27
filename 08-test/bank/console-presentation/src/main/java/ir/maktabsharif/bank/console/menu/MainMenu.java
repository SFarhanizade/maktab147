package ir.maktabsharif.bank.console.menu;

import ir.maktabsharif.bank.console.Console;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MainMenu {
    private final Console console;
    private final AdminMenu adminMenu;
    private final OwnerMenu ownerMenu;

    public void show() {
        while (true) {
            console.print("""
                    ===== Main Menu =====
                    
                    1. Admin Menu
                    2. Owner Menu
                    3. Exit
                    >\s""");

            var menuIndex = Integer.parseInt(console.readln());

            switch (menuIndex) {
                case 1 -> adminMenu.show();
                case 2 -> ownerMenu.show();
                case 3 -> {
                    return;
                }
                default -> console.println("Invalid input!");
            }
        }
    }
}
