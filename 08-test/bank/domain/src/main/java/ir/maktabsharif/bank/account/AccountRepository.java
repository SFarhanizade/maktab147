package ir.maktabsharif.bank.account;

import ir.maktabsharif.bank.base.BasePagingAndSortingRepository;

import java.util.Optional;

public interface AccountRepository extends BasePagingAndSortingRepository<Account, Integer> {
    Optional<Account> findByNumber(String number);
}
