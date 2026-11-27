package ir.maktabsharif.bank.console.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class FindAllModelTest {
    @Test
    void initializesPageZeroAndGivenSize() {
        var m = new FindAllModel(10);
        assertThat(m.getPage()).isZero();
        assertThat(m.getSize()).isEqualTo(10);
    }
}