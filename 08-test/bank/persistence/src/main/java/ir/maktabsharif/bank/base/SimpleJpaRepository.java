package ir.maktabsharif.bank.base;

import ir.maktabsharif.bank.page.Page;
import ir.maktabsharif.bank.page.Pageable;
import ir.maktabsharif.bank.page.Sort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class SimpleJpaRepository<Id, E extends T, T extends BaseDomain<Id>>
        implements BasePagingAndSortingRepository<T, Id> {

    protected final EntityManager entityManager;

    protected final TransactionManager transactionManager;

    @Override
    public Optional<T> findById(Id id) {
        return Optional.ofNullable(
                entityManager.find(getEntityClass(), id)
        );
    }

    @Override
    public T save(T t) {
        return transactionManager.execute(
                () -> {
                    if (Objects.isNull(t.getId())) {
                        entityManager.persist(t);
                    } else {
                        return entityManager.merge(t);
                    }
                    return t;
                }
        );
    }

    @Override
    public T deleteById(Id id) {
        T reference = entityManager.getReference(getEntityClass(), id);
        delete(reference);
        return reference;
    }

    @Override
    public void delete(T t) {
        transactionManager.execute(
                () -> entityManager.remove(t)
        );
    }

    @Override
    public List<T> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(getEntityClass());
        cq.from(getEntityClass());
        return (List<T>) entityManager.createQuery(cq).getResultList();
    }

    @Override
    public long count() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<E> entityRoot = cq.from(getEntityClass());
        cq.select(cb.count(entityRoot));
        return entityManager.createQuery(cq).getSingleResult();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        long count = count();
        if (count == 0) {
            return Page.empty(pageable);
        }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(getEntityClass());
        cq.from(getEntityClass());
        List<E> resultList = entityManager.createQuery(cq)
                .setFirstResult(pageable.offset())
                .setMaxResults(pageable.size())
                .getResultList();
        return Page.of(resultList, pageable, count);
    }

    @Override
    public Page<T> findAll(Pageable pageable, Sort... sorts) {
        if (sorts == null || sorts.length == 0) {
            return findAll(pageable);
        }

        long count = count();
        if (count == 0) {
            return Page.empty(pageable);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(getEntityClass());
        var from = cq.from(getEntityClass());

        var orders = Arrays.stream(sorts)
                .map(sort -> switch (sort.direction()) {
                    case ASC -> cb.asc(from.get(sort.property()));
                    case DESC -> cb.desc(from.get(sort.property()));
                })
                .toList();
        cq.orderBy(orders);


        List<E> resultList = entityManager.createQuery(cq)
                .setFirstResult(pageable.offset())
                .setMaxResults(pageable.size())
                .getResultList();
        return Page.of(resultList, pageable, count);
    }

    protected abstract Class<E> getEntityClass();
}
