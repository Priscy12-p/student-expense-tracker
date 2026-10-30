import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for the expense data class.
 *
 * QA techniques used:
 *  - Equivalence partitioning: typical, empty and unusual-but-valid inputs
 *  - Boundary value analysis: amount of 0, smallest cent, very large value
 */
public class expenseTest {

    private static final double DELTA = 0.0001;

    // ---------- Positive / typical ----------

    @Test
    @DisplayName("Constructor stores all four fields and getters return them")
    void testExpenseDetails() {
        expense expense = new expense("Lunch", "Food", 50.0, "2026-10-25");

        assertEquals("Lunch", expense.getDescription());
        assertEquals("Food", expense.getCategory());
        assertEquals(50.0, expense.getAmount(), DELTA);
        assertEquals("2026-10-25", expense.getDate());
    }

    @Test
    @DisplayName("Decimal amounts (cents) are kept exactly as given")
    void testDecimalAmount() {
        expense expense = new expense("Coffee", "Food", 24.95, "2026-10-26");

        assertEquals(24.95, expense.getAmount(), DELTA);
    }

    // ---------- Boundary values ----------

    @Test
    @DisplayName("Boundary: amount of zero is stored as zero")
    void testZeroAmountBoundary() {
        expense expense = new expense("Free sample", "Food", 0.0, "2026-10-26");

        assertEquals(0.0, expense.getAmount(), DELTA);
    }

    @Test
    @DisplayName("Boundary: smallest rand amount (one cent) is stored")
    void testSmallestCentBoundary() {
        expense expense = new expense("Sweet", "Food", 0.01, "2026-10-26");

        assertEquals(0.01, expense.getAmount(), DELTA);
    }

    @Test
    @DisplayName("Boundary: very large amount is stored without change")
    void testLargeAmountBoundary() {
        expense expense = new expense("Laptop", "Study", 1_000_000.99, "2026-10-26");

        assertEquals(1_000_000.99, expense.getAmount(), DELTA);
    }

    // ---------- Unusual-but-accepted inputs (documents current behaviour) ----------

    @Test
    @DisplayName("Class does not validate: negative amount is stored as given")
    void testNegativeAmountIsNotValidatedByClass() {
        // Validation happens in main.readPositiveAmount, not in this class.
        expense expense = new expense("Refund", "Other", -10.0, "2026-10-26");

        assertEquals(-10.0, expense.getAmount(), DELTA);
    }

    @Test
    @DisplayName("Empty strings are stored as empty strings")
    void testEmptyStrings() {
        expense expense = new expense("", "", 5.0, "");

        assertEquals("", expense.getDescription());
        assertEquals("", expense.getCategory());
        assertEquals("", expense.getDate());
    }

    @Test
    @DisplayName("Null text fields are returned as null (no hidden defaults)")
    void testNullFields() {
        expense expense = new expense(null, null, 5.0, null);

        assertNull(expense.getDescription());
        assertNull(expense.getCategory());
        assertNull(expense.getDate());
    }

    @Test
    @DisplayName("Date is kept as plain text, not parsed or reformatted")
    void testDateIsPlainText() {
        expense expense = new expense("Taxi", "Travel", 80.0, "not-a-date");

        assertEquals("not-a-date", expense.getDate());
    }

    @Test
    @DisplayName("Two expense objects do not share state")
    void testObjectsAreIndependent() {
        expense first = new expense("A", "X", 1.0, "2026-01-01");
        expense second = new expense("B", "Y", 2.0, "2026-01-02");

        assertEquals("A", first.getDescription());
        assertEquals("B", second.getDescription());
        assertEquals(1.0, first.getAmount(), DELTA);
        assertEquals(2.0, second.getAmount(), DELTA);
    }
}