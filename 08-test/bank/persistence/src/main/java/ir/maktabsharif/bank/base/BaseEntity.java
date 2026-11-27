package ir.maktabsharif.bank.base;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass
public class BaseEntity<ID> implements BaseDomain<ID> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    ID id;
    LocalDateTime createdAt;

    @PrePersist
    void setup() {
        createdAt = LocalDateTime.now();
    }
}
