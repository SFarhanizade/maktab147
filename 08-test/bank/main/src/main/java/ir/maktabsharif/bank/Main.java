package ir.maktabsharif.bank;

import ir.maktabsharif.bank.console.menu.MainMenu;

public class Main {
    void main() {
        var context = ApplicationContext.getInstance();
        var menu = context.getBean(MainMenu.class);
        menu.show();
    }
}