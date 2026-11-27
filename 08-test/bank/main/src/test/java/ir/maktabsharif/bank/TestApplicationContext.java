package ir.maktabsharif.bank;

import ir.maktabsharif.bank.account.*;
import ir.maktabsharif.bank.account.usecase.admin.AdminAccountPresenter;
import ir.maktabsharif.bank.account.usecase.admin.AdminAccountService;
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

public class TestApplicationContext {

    private static TestApplicationContext CTX;
    @Getter
    private final ApplicationProperties properties;

    private TestApplicationContext() {
        this.properties = new ApplicationProperties();
        initializingBean();
    }

    public TestApplicationContext(String propertiesFilename) {
        this.properties = new ApplicationProperties(propertiesFilename);
        initializingBean();
    }

    public static TestApplicationContext getInstance() {
        if (CTX == null) {
            CTX = new TestApplicationContext();
        }
        return CTX;
    }

    public static TestApplicationContext getInstance(String propertiesFilename) {
        if (CTX == null) {
            CTX = new TestApplicationContext(propertiesFilename);
        }
        return CTX;
    }


    private final Map<Class<?>, Object> beans = new HashMap<>();
    private final Map<Class<?>, Runnable> beanLoaders = new HashMap<>();

    private void initializingBean() {
        beanLoaders.put(EntityManagerFactory.class, () -> registerBean(EntityManagerFactory.class, createEntityManagerFactory()));
        beanLoaders.put(EntityManager.class, () -> registerBean(EntityManager.class, createEntityManager()));
        beanLoaders.put(TransactionManager.class, () -> registerBean(TransactionManager.class, createTransactionManager()));
        beanLoaders.put(TransactionRepository.class, () -> registerBean(TransactionRepository.class, createTransactionRepository()));
        beanLoaders.put(AccountRepository.class, () -> registerBean(AccountRepository.class, createAccountRepository()));
        beanLoaders.put(AccountFactory.class, () -> registerBean(AccountFactory.class, createAccountFactory()));
        beanLoaders.put(TransactionFactory.class, () -> registerBean(TransactionFactory.class, createTransactionFactory()));
        beanLoaders.put(AdminAccountService.class, () -> registerBean(AdminAccountService.class, createAdminAccountService(getBean(AdminAccountPresenter.class))));
        beanLoaders.put(OwnerAccountService.class, () -> registerBean(OwnerAccountService.class, createOwnerAccountService(getBean(OwnerAccountPresenter.class))));
        beanLoaders.put(OwnerTransactionService.class, () -> registerBean(OwnerTransactionService.class, createOwnerTransactionService(getBean(OwnerTransactionPresenter.class))));
        beanLoaders.put(Console.class, () -> registerBean(Console.class, createSystemConsole()));
        beanLoaders.put(AdminMenu.class, () -> registerBean(AdminMenu.class, createAdminMenu()));
        beanLoaders.put(OwnerMenu.class, () -> registerBean(OwnerMenu.class, createOwnerMenu()));
        beanLoaders.put(MainMenu.class, () -> registerBean(MainMenu.class, createMainMenu()));
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

    public <T> T getBean(Class<T> tClass) {
        Object bean = beans.get(tClass);
        if (Objects.isNull(bean)) {
            beanLoaders.get(tClass).run();
            bean = beans.get(tClass);
            if (Objects.isNull(bean)) {
                throw new RuntimeException("no bean found for class: " + tClass.getSimpleName());
            }
        }
        return tClass.cast(bean);
    }

    public void clear() {
        beans.clear();
    }

    public <T> void setBean(Class<T> type, T bean) {
        beans.put(type, bean);
    }
}
