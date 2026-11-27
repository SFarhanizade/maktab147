package ir.maktabsharif.bank.base;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class JpaTransactionManager implements TransactionManager {

    private final EntityManager entityManager;

    @Override
    public <T> T execute(Supplier<T> logic) {
        EntityTransaction transaction = entityManager.getTransaction();
        if (transaction.isActive()) {
            return logic.get();
        }
        T t;
        transaction.begin();
        try {
            t = logic.get();
            transaction.commit();
        } catch (Throwable throwable) {
            transaction.rollback();
            throw throwable;
        }
        return t;
    }

    @Override
    public void execute(Runnable logic) {
        execute(() -> {
            logic.run();
            return null;
        });
    }
}
