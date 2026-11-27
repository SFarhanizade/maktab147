package ir.maktabsharif.bank.account;

public class JpaAccountFactory implements AccountFactory {
    @Override
    public Account createAccount(String ownerName) {
        return new JpaAccount(ownerName);
    }
}
