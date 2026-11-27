package ir.maktabsharif.bank.account;

import ir.maktabsharif.bank.base.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class JpaAccount extends BaseEntity<Integer> implements Account {
    String ownerName;


    BigInteger balance = new BigInteger("0");

    public JpaAccount(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public String getNumber() {
        return "ACC-" + getId();
    }
}
