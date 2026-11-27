package ir.maktabsharif.bank.base;


import ir.maktabsharif.bank.page.Page;
import ir.maktabsharif.bank.page.Pageable;
import ir.maktabsharif.bank.page.Sort;

public interface BasePagingAndSortingRepository<E extends BaseDomain<Id>, Id> extends BaseCrudRepository<E, Id> {
    Page<E> findAll(Pageable pageable);

    Page<E> findAll(Pageable pageable, Sort... sorts);
}
