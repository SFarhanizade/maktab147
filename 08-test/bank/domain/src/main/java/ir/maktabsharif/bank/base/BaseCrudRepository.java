package ir.maktabsharif.bank.base;


import java.util.Collection;
import java.util.Optional;

public interface BaseCrudRepository<E extends BaseDomain<Id>, Id> {
    E save(E e);

    Optional<E> findById(Id id);

    Collection<? extends E> findAll();

    long count();

    E deleteById(Id id);

    void delete(E e);
}
