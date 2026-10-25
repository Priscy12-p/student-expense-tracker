
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class budgetTest {

    @Test
    void testRemainingBudget() {
        budget budget = new budget(2000.0);

        assertEquals(1920.0, budget.getRemainingBudget(80.0));
    }

    @Test
    void testOverBudget() {
        budget budget = new budget(2000.0);

        assertTrue(budget.isOverBudget(2500.0));
        assertFalse(budget.isOverBudget(1500.0));
    }
}
