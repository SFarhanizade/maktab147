package ir.maktabsharif.bank.console.menu;

import ir.maktabsharif.bank.account.usecase.admin.AdminAccountPresenter;
import ir.maktabsharif.bank.account.usecase.admin.AdminAccountService;
import ir.maktabsharif.bank.console.Console;
import ir.maktabsharif.bank.console.model.CreateAccountModel;
import ir.maktabsharif.bank.console.model.DepositModel;
import ir.maktabsharif.bank.console.model.FindAllModel;
import ir.maktabsharif.bank.console.model.ShowOneModelByInt;
import ir.maktabsharif.bank.console.presenter.AdminAccountConsolePresenter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public class AdminMenu {
    private final Console console;
    private final Function<AdminAccountPresenter, AdminAccountService> serviceProvider;

    public void show() {
        while (true) {
            console.print("""
                    ===== Admin Menu =====
                    
                    1. Create Account
                    2. Show All Accounts
                    3. Show One Account
                    4. Deposit
                    5. Back
                    >\s""");

            var menuIndex = Integer.parseInt(console.readln());

            switch (menuIndex) {
                case 1 -> createAccount();
                case 2 -> showAllAccounts();
                case 3 -> showOneAccount();
                case 4 -> deposit();
                case 5 -> {
                    console.println("Back to Main Menu...");
                    return;
                }
                default -> console.println("Invalid input!");
            }
        }
    }


    private void deposit() {
        serviceProvider.apply(new AdminAccountConsolePresenter(console))
                .deposit(new DepositModel(console));
    }


    public void createAccount() {
        serviceProvider.apply(new AdminAccountConsolePresenter(console))
                .create(new CreateAccountModel(console));
    }


    public void showAllAccounts() {
        var model = new FindAllModel(10);
        var presenter = new AdminAccountConsolePresenter(console);
        var service = serviceProvider.apply(presenter);
        service.findAll(model);
        while (true) {
            console.print("""
                    Next(n), Previous(p), Back(b)
                    >\s""");
            switch (console.readln().toLowerCase()) {
                case "n" -> {
                    var page = presenter.getFindAllData();
                    if ((page.pageNumber() + 1L) * page.pageable().size() < page.totalElements() && page.size() == page.pageable().size()) {
                        model.page++;
                        service.findAll(model);
                    } else {
                        console.println("There is no next page!");
                    }
                }
                case "p" -> {
                    if (model.page > 0) {
                        model.page--;
                        service.findAll(model);
                    } else console.println("There is no previous page!");
                }
                case "b" -> {
                    console.println("Back to Admin Menu...");
                    return;
                }
                default -> console.println("Invalid input!");
            }
        }
    }

    public void showOneAccount() {
        serviceProvider.apply(new AdminAccountConsolePresenter(console))
                .find(new ShowOneModelByInt(console));
    }

}
