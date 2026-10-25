
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class expenseTest {

    @Test
    void testExpenseDetails() {
        expense expense = new expense("Lunch", "Food", 50.0, "2026-10-25");

        assertEquals("Lunch", expense.getDescription());
        assertEquals("Food", expense.getCategory());
        assertEquals(50.0, expense.getAmount());
        assertEquals("2026-10-25", expense.getDate());
    }
}

