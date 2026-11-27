import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class CalculatorTest {
    private Calculator undertest;

    @BeforeEach
    void setup() {
        undertest = new Calculator();
    }

    @ParameterizedTest
    @MethodSource("testAddSource")
    void testAdd(int a, int b, int expected) {
        var result = undertest.add(a, b);
        assertEquals(expected, result);
    }

    static Stream<Arguments> testAddSource() {
        return IntStream.range(1, 11)
                .mapToObj(a -> arguments(a, a + 1, a + a + 1));
    }

    @Test
    void testAddNegative() {
        var result = undertest.add(-1, -2);
        assertEquals(-3, result);
    }

    @Test
    void testSubtract() {
        var result = undertest.subtract(5, 3);
        assertEquals(2, result);
    }

    @Test
    void testSubtractNegative() {
        var result = undertest.subtract(-5, -3);
        assertEquals(-2, result);
    }

    @Test
    void testMultiply() {
        var result = undertest.multiply(2, 3);
        assertEquals(6, result);
    }

    @Test
    void testMultiplyNegative() {
        var result = undertest.multiply(-2, -3);
        assertEquals(6, result);
    }

    @Test
    void testDivide() {
        var result = undertest.divide(6, 3);
        assertEquals(2, result);
    }

    @Test
    void testDivideNegative() {
        var result = undertest.divide(-6, -3);
        assertEquals(2, result);
    }

    @Test
    void testDivideByZero() {
        assertThrowsExactly(ArithmeticException.class, () -> undertest.divide(1, 0));
    }
}
