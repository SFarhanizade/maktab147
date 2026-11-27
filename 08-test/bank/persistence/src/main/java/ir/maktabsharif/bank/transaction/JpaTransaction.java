package ir.maktabsharif.bank.transaction;

import ir.maktabsharif.bank.account.JpaAccount;
import ir.maktabsharif.bank.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class JpaTransaction extends BaseEntity<Long> implements Transaction {
    BigInteger amount;

    @ManyToOne
    JpaAccount to;

    @ManyToOne
    JpaAccount from;

    Type type;
}
