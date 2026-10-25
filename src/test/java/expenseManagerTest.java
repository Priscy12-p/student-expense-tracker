
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class expenseManagerTest {

    @Test
    void testAddExpenseAndCalculateTotal() {
        expenseManager manager = new expenseManager();

        manager.addexpense(new expense("Lunch", "Food", 50.0, "2026-10-25"));
        manager.addexpense(new expense("Transport", "Travel", 30.0, "2026-10-25"));

        assertEquals(80.0, manager.calculateTotalExpenses(), 0.001);
    }
}
