package ir.maktabsharif.bank.base;

import java.util.function.Supplier;

public interface TransactionManager {
    <T> T execute(Supplier<T> logic);

    void execute(Runnable logic);
}
