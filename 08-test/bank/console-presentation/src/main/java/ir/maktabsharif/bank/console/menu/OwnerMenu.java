package ir.maktabsharif.bank.console.menu;

import ir.maktabsharif.bank.account.usecase.owner.OwnerAccountPresenter;
import ir.maktabsharif.bank.account.usecase.owner.OwnerAccountService;
import ir.maktabsharif.bank.console.Console;
import ir.maktabsharif.bank.console.model.FindAllModel;
import ir.maktabsharif.bank.console.model.GetBalanceModel;
import ir.maktabsharif.bank.console.model.ShowOneModelByLong;
import ir.maktabsharif.bank.console.model.TransferMoneyModel;
import ir.maktabsharif.bank.console.presenter.OwnerAccountConsolePresenter;
import ir.maktabsharif.bank.transaction.usecase.owner.OwnerTransactionPresenter;
import ir.maktabsharif.bank.transaction.usecase.owner.OwnerTransactionService;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public class OwnerMenu {
    private final Console console;
    private final Function<OwnerAccountPresenter, OwnerAccountService> accountServiceProvider;
    private final Function<OwnerTransactionPresenter, OwnerTransactionService> transactionServiceProvider;

    public void show() {
        while (true) {
            console.print("""
                    ===== Owner Menu =====
                    
                    1. Get Balance
                    2. Transfer Money
                    3. Show All Transactions
                    4. Show One Transaction
                    5. Back
                    >\s""");

            var menuIndex = Integer.parseInt(console.readln());

            switch (menuIndex) {
                case 1 -> getBalance();
                case 2 -> transferMoney();
                case 3 -> showAllTransactions();
                case 4 -> showOneTransaction();
                case 5 -> {
                    console.println("Back to Main Menu...");
                    return;
                }
                default -> console.println("Invalid input!");
            }
        }
    }

    public void getBalance() {
        accountServiceProvider.apply(new OwnerAccountConsolePresenter(console))
                .getBalance(new GetBalanceModel(console));
    }

    public void transferMoney() {
        accountServiceProvider.apply(new OwnerAccountConsolePresenter(console))
                .transfer(new TransferMoneyModel(console));
    }

    public void showAllTransactions() {
        var model = new FindAllModel(10);
        var presenter = new OwnerAccountConsolePresenter(console);
        var service = transactionServiceProvider.apply(presenter);
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

    public void showOneTransaction() {
        transactionServiceProvider.apply(new OwnerAccountConsolePresenter(console))
                .findById(new ShowOneModelByLong(console));
    }
}
