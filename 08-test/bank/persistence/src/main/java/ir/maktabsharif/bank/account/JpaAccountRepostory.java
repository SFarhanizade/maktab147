package ir.maktabsharif.bank.account;

import ir.maktabsharif.bank.base.SimpleJpaRepository;
import ir.maktabsharif.bank.base.TransactionManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Optional;

public class JpaAccountRepostory extends SimpleJpaRepository<Integer, JpaAccount, Account> implements AccountRepository {

    public JpaAccountRepostory(EntityManager entityManager, TransactionManager transactionManager) {
        super(entityManager, transactionManager);
    }

    @Override
    protected Class<JpaAccount> getEntityClass() {
        return JpaAccount.class;
    }

    @Override
    public Optional<Account> findByNumber(String number) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<JpaAccount> cq = cb.createQuery(JpaAccount.class);
        Root<JpaAccount> from = cq.from(JpaAccount.class);
        cq.where(cb.equal(from.get(JpaAccount_.ID), number.replace("ACC-", "")));
        return Optional.ofNullable(
                entityManager.createQuery(cq).getSingleResultOrNull()
        );
    }
}
