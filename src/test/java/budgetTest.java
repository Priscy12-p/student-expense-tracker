import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the budget class.
 *
 * QA techniques used:
 *  - Equivalence partitioning: under budget / at budget / over budget
 *  - Boundary value analysis: exactly on the limit, one cent either side
 *  - Positive and negative scenarios
 */
public class budgetTest {

    private static final double DELTA = 0.0001;

    // ---------- Getter ----------

    @Test
    @DisplayName("getMonthlyBudget returns the value given to the constructor")
    void testGetMonthlyBudget() {
        budget budget = new budget(2000.0);

        assertEquals(2000.0, budget.getMonthlyBudget(), DELTA);
    }

    // ---------- Remaining budget ----------

    @Test
    @DisplayName("Remaining budget = budget minus expenses")
    void testRemainingBudget() {
        budget budget = new budget(2000.0);

        assertEquals(1920.0, budget.getRemainingBudget(80.0), DELTA);
    }

    @Test
    @DisplayName("No expenses: the whole budget remains")
    void testRemainingWithNoExpenses() {
        budget budget = new budget(1500.0);

        assertEquals(1500.0, budget.getRemainingBudget(0.0), DELTA);
    }

    @Test
    @DisplayName("Expenses equal to budget: nothing remains")
    void testRemainingWhenExactlyOnBudget() {
        budget budget = new budget(1500.0);

        assertEquals(0.0, budget.getRemainingBudget(1500.0), DELTA);
    }

    @Test
    @DisplayName("Negative scenario: overspending gives a negative remaining amount")
    void testRemainingIsNegativeWhenOverBudget() {
        budget budget = new budget(1000.0);

        assertEquals(-250.5, budget.getRemainingBudget(1250.5), DELTA);
    }

    @Test
    @DisplayName("Cents are handled correctly in the remaining calculation")
    void testRemainingWithCents() {
        budget budget = new budget(100.50);

        assertEquals(75.25, budget.getRemainingBudget(25.25), DELTA);
    }

    // ---------- Over budget: equivalence classes ----------

    @Test
    @DisplayName("Over budget: spending above the limit is over budget")
    void testOverBudget() {
        budget budget = new budget(2000.0);

        assertTrue(budget.isOverBudget(2500.0));
    }

    @Test
    @DisplayName("Under budget: spending below the limit is not over budget")
    void testUnderBudget() {
        budget budget = new budget(2000.0);

        assertFalse(budget.isOverBudget(1500.0));
    }

    // ---------- Over budget: boundary values ----------

    @Test
    @DisplayName("Boundary: spending exactly the budget is NOT over budget")
    void testExactlyOnBudgetIsNotOver() {
        budget budget = new budget(2000.0);

        assertFalse(budget.isOverBudget(2000.0));
    }

    @Test
    @DisplayName("Boundary: one cent over the budget IS over budget")
    void testOneCentOverIsOver() {
        budget budget = new budget(2000.0);

        assertTrue(budget.isOverBudget(2000.01));
    }

    @Test
    @DisplayName("Boundary: one cent under the budget is NOT over budget")
    void testOneCentUnderIsNotOver() {
        budget budget = new budget(2000.0);

        assertFalse(budget.isOverBudget(1999.99));
    }

    @Test
    @DisplayName("Boundary: zero expenses is never over budget")
    void testZeroExpensesNotOver() {
        budget budget = new budget(2000.0);

        assertFalse(budget.isOverBudget(0.0));
    }

    // ---------- Edge cases ----------

    @Test
    @DisplayName("Edge: a zero budget with any spending is over budget")
    void testZeroBudgetWithSpending() {
        budget budget = new budget(0.0);

        assertTrue(budget.isOverBudget(0.01));
        assertFalse(budget.isOverBudget(0.0));
    }

    @Test
    @DisplayName("isOverBudget and getRemainingBudget agree with each other")
    void testOverBudgetMatchesRemainingSign() {
        budget budget = new budget(800.0);
        double[] spends = {0.0, 799.99, 800.0, 800.01, 5000.0};

        for (double spent : spends) {
            boolean over = budget.isOverBudget(spent);
            boolean negativeRemaining = budget.getRemainingBudget(spent) < 0;

            assertEquals(negativeRemaining, over,
                    "Mismatch for spent amount " + spent);
        }
    }
}