package ir.maktabsharif.bank.transaction;

import ir.maktabsharif.bank.base.SimpleJpaRepository;
import ir.maktabsharif.bank.base.TransactionManager;
import jakarta.persistence.EntityManager;

public class JpaTransactionRepostory extends SimpleJpaRepository<Long, JpaTransaction, Transaction> implements TransactionRepository {

    public JpaTransactionRepostory(EntityManager entityManager, TransactionManager transactionManager) {
        super(entityManager, transactionManager);
    }

    @Override
    protected Class<JpaTransaction> getEntityClass() {
        return JpaTransaction.class;
    }
}
