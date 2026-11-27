package ir.maktabsharif.bank;

import ir.maktabsharif.bank.account.*;
import ir.maktabsharif.bank.account.usecase.admin.AdminAccountPresenter;
import ir.maktabsharif.bank.account.usecase.admin.AdminAccountServiceImpl;
import ir.maktabsharif.bank.account.usecase.owner.OwnerAccountPresenter;
import ir.maktabsharif.bank.account.usecase.owner.OwnerAccountService;
import ir.maktabsharif.bank.account.usecase.owner.OwnerAccountServiceImpl;
import ir.maktabsharif.bank.base.JpaTransactionManager;
import ir.maktabsharif.bank.base.TransactionManager;
import ir.maktabsharif.bank.console.Console;
import ir.maktabsharif.bank.console.SystemConsole;
import ir.maktabsharif.bank.console.menu.AdminMenu;
import ir.maktabsharif.bank.console.menu.MainMenu;
import ir.maktabsharif.bank.console.menu.OwnerMenu;
import ir.maktabsharif.bank.transaction.*;
import ir.maktabsharif.bank.transaction.usecase.owner.OwnerTransactionPresenter;
import ir.maktabsharif.bank.transaction.usecase.owner.OwnerTransactionService;
import ir.maktabsharif.bank.transaction.usecase.owner.OwnerTransactionServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceConfiguration;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ApplicationContext {

    private static ApplicationContext CTX;
    @Getter
    private final ApplicationProperties properties;

    public ApplicationContext(String propertiesFilename) {
        this(propertiesFilename, false);
    }

    private ApplicationContext() {
        this.properties = new ApplicationProperties();
        initializingBean(false);
    }

    public ApplicationContext(String propertiesFilename, boolean lazy) {
        this.properties = new ApplicationProperties(propertiesFilename);
        initializingBean(lazy);
    }

    public static ApplicationContext getInstance() {
        if (CTX == null) {
            CTX = new ApplicationContext();
        }
        return CTX;
    }

    public static ApplicationContext getInstance(String propertiesFilename) {
        if (CTX == null) {
            CTX = new ApplicationContext(propertiesFilename);
        }
        return CTX;
    }

    public static ApplicationContext getInstance(String propertiesFilename, boolean lazy) {
        if (CTX == null) {
            CTX = new ApplicationContext(propertiesFilename, lazy);
        }
        return CTX;
    }

    private final Map<Class<?>, Object> beans = new HashMap<>();

    private Runnable loader;
    private boolean loaded;

    private void initializingBean(boolean lazy) {
        Runnable loader = () -> {
            if(loaded)
                return;
            registerBean(EntityManagerFactory.class, createEntityManagerFactory());
            registerBean(EntityManager.class, createEntityManager());
            registerBean(TransactionManager.class, createTransactionManager());
            registerBean(TransactionRepository.class, createTransactionRepository());
            registerBean(AccountRepository.class, createAccountRepository());
            registerBean(AccountFactory.class, createAccountFactory());
            registerBean(TransactionFactory.class, createTransactionFactory());
            registerBean(Console.class, createSystemConsole());
            registerBean(AdminMenu.class, createAdminMenu());
            registerBean(OwnerMenu.class, createOwnerMenu());
            registerBean(MainMenu.class, createMainMenu());
            loaded = true;
        };
        if (lazy) {
            this.loader = loader;
        } else {
            loader.run();
            this.loader = loader;
        }
    }

    private SystemConsole createSystemConsole() {
        return new SystemConsole();
    }

    private MainMenu createMainMenu() {
        return new MainMenu(getBean(Console.class), getBean(AdminMenu.class), getBean(OwnerMenu.class));
    }

    private OwnerAccountService createOwnerAccountService(OwnerAccountPresenter presenter) {
        return new OwnerAccountServiceImpl(
                getBean(AccountRepository.class),
                getBean(TransactionManager.class),
                presenter,
                getBean(TransactionFactory.class),
                getBean(TransactionRepository.class)
        );
    }

    private OwnerTransactionService createOwnerTransactionService(OwnerTransactionPresenter presenter) {
        return new OwnerTransactionServiceImpl(
                getBean(TransactionRepository.class),
                presenter
        );
    }

    private OwnerMenu createOwnerMenu() {
        return new OwnerMenu(
                getBean(Console.class),
                this::createOwnerAccountService,
                this::createOwnerTransactionService
        );
    }

    private AdminMenu createAdminMenu() {
        return new AdminMenu(getBean(Console.class), this::createAdminAccountService);
    }

    private AdminAccountServiceImpl createAdminAccountService(AdminAccountPresenter presenter) {
        return new AdminAccountServiceImpl(
                getBean(AccountFactory.class),
                getBean(AccountRepository.class),
                getBean(TransactionManager.class),
                presenter);
    }

    private TransactionFactory createTransactionFactory() {
        return new JpaTransactionFactory();
    }

    private JpaAccountFactory createAccountFactory() {
        return new JpaAccountFactory();
    }

    private TransactionRepository createTransactionRepository() {
        return new JpaTransactionRepostory(getBean(EntityManager.class), getBean(TransactionManager.class));
    }

    private TransactionManager createTransactionManager() {
        return new JpaTransactionManager(getBean(EntityManager.class));
    }

    private AccountRepository createAccountRepository() {
        return new JpaAccountRepostory(getBean(EntityManager.class), getBean(TransactionManager.class));
    }

    private <T> void registerBean(Class<T> tClass, T t) {
        beans.put(tClass, t);
    }

    public <T> T getBean(Class<T> tClass) {
//        if (!loaded)
//            loader.run();
        Object bean = beans.get(tClass);
        if (Objects.isNull(bean)) {
            throw new RuntimeException("no bean found for class: " + tClass.getSimpleName());
        }
        return tClass.cast(bean);
    }

    private EntityManagerFactory createEntityManagerFactory() {
        var persistenceConfiguration = new PersistenceConfiguration("default");
        persistenceConfiguration.properties(properties.getProperties("jpa"));
        persistenceConfiguration.managedClass(JpaAccount.class);
        persistenceConfiguration.managedClass(JpaTransaction.class);
        return Persistence.createEntityManagerFactory(persistenceConfiguration);
    }

    private EntityManager createEntityManager() {
        return getBean(EntityManagerFactory.class).createEntityManager();
    }
}
