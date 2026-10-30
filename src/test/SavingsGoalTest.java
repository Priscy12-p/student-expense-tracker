import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for savingsGoal.
 *
 * QA techniques used:
 *  - Equivalence partitioning: valid / invalid names, targets and contributions
 *  - Boundary value analysis: zero, one cent, exactly the remaining amount,
 *    one cent more than remaining, 0% / 99% / 100% progress
 *  - State testing: not started -> in progress -> reached -> locked
 *  - Positive and negative scenarios
 */
public class savingsGoalTest {

    private static final double DELTA = 0.0001;

    // ---------- Construction ----------

    @Test
    @DisplayName("New goal starts with nothing saved and the full target remaining")
    void testNewGoalInitialState() {
        savingsGoal goal = new savingsGoal("Laptop", 10000.0);

        assertEquals("Laptop", goal.getName());
        assertEquals(10000.0, goal.getTargetAmount(), DELTA);
        assertEquals(0.0, goal.getSavedAmount(), DELTA);
        assertEquals(10000.0, goal.getRemaining(), DELTA);
        assertEquals(0.0, goal.getProgressPercent(), DELTA);
        assertFalse(goal.isReached());
    }

    @Test
    @DisplayName("Goal name is trimmed")
    void testNameIsTrimmed() {
        savingsGoal goal = new savingsGoal("  Holiday  ", 500.0);

        assertEquals("Holiday", goal.getName());
    }

    @Test
    @DisplayName("Boundary: smallest valid target (one cent) is accepted")
    void testSmallestValidTarget() {
        savingsGoal goal = new savingsGoal("Tiny", 0.01);

        assertEquals(0.01, goal.getTargetAmount(), DELTA);
    }

    @Test
    @DisplayName("Negative: empty name is rejected")
    void testEmptyNameRejected() {
        assertThrows(IllegalArgumentException.class, () -> new savingsGoal("", 100.0));
    }

    @Test
    @DisplayName("Negative: blank (spaces only) name is rejected")
    void testBlankNameRejected() {
        assertThrows(IllegalArgumentException.class, () -> new savingsGoal("   ", 100.0));
    }

    @Test
    @DisplayName("Negative: null name is rejected")
    void testNullNameRejected() {
        assertThrows(IllegalArgumentException.class, () -> new savingsGoal(null, 100.0));
    }

    @Test
    @DisplayName("Boundary: target of zero is rejected")
    void testZeroTargetRejected() {
        assertThrows(IllegalArgumentException.class, () -> new savingsGoal("Zero", 0.0));
    }

    @Test
    @DisplayName("Negative: negative target is rejected")
    void testNegativeTargetRejected() {
        assertThrows(IllegalArgumentException.class, () -> new savingsGoal("Neg", -50.0));
    }

    @Test
    @DisplayName("Boundary: target that rounds down to zero cents is rejected")
    void testTargetRoundingToZeroRejected() {
        assertThrows(IllegalArgumentException.class, () -> new savingsGoal("Tiny", 0.004));
    }

    @Test
    @DisplayName("Negative: NaN and infinite targets are rejected")
    void testNaNAndInfiniteTargetsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new savingsGoal("Bad", Double.NaN));
        assertThrows(IllegalArgumentException.class,
                () -> new savingsGoal("Bad", Double.POSITIVE_INFINITY));
    }

    // ---------- addContribution: valid ----------

    @Test
    @DisplayName("A contribution increases saved and reduces remaining")
    void testAddContribution() {
        savingsGoal goal = new savingsGoal("Laptop", 1000.0);

        goal.addContribution(250.0);

        assertEquals(250.0, goal.getSavedAmount(), DELTA);
        assertEquals(750.0, goal.getRemaining(), DELTA);
        assertFalse(goal.isReached());
    }

    @Test
    @DisplayName("Several contributions add up")
    void testMultipleContributions() {
        savingsGoal goal = new savingsGoal("Laptop", 1000.0);

        goal.addContribution(100.0);
        goal.addContribution(200.5);
        goal.addContribution(49.5);

        assertEquals(350.0, goal.getSavedAmount(), DELTA);
        assertEquals(650.0, goal.getRemaining(), DELTA);
    }

    @Test
    @DisplayName("Cents do not drift when adding 0.10 ten times")
    void testNoFloatingPointDrift() {
        savingsGoal goal = new savingsGoal("Pennies", 1.0);

        for (int i = 0; i < 10; i++) {
            goal.addContribution(0.10);
        }

        assertEquals(1.0, goal.getSavedAmount(), DELTA);
        assertTrue(goal.isReached(), "Ten times 10c must reach exactly R1.00");
    }

    @Test
    @DisplayName("Contribution is rounded to cents")
    void testContributionRoundedToCents() {
        savingsGoal goal = new savingsGoal("Round", 100.0);

        goal.addContribution(10.126);

        assertEquals(10.13, goal.getSavedAmount(), DELTA);
    }

    // ---------- addContribution: boundaries and invalid ----------

    @Test
    @DisplayName("Boundary: contribution of exactly the remaining amount reaches the goal")
    void testExactRemainingReachesGoal() {
        savingsGoal goal = new savingsGoal("Laptop", 1000.0);
        goal.addContribution(400.0);

        goal.addContribution(600.0);

        assertTrue(goal.isReached());
        assertEquals(0.0, goal.getRemaining(), DELTA);
        assertEquals(100.0, goal.getProgressPercent(), DELTA);
    }

    @Test
    @DisplayName("Boundary: one cent more than remaining is rejected and changes nothing")
    void testOneCentOverRemainingRejected() {
        savingsGoal goal = new savingsGoal("Laptop", 1000.0);
        goal.addContribution(400.0);

        assertThrows(IllegalArgumentException.class, () -> goal.addContribution(600.01));

        assertEquals(400.0, goal.getSavedAmount(), DELTA);
        assertFalse(goal.isReached());
    }

    @Test
    @DisplayName("Boundary: one cent less than remaining leaves the goal not reached")
    void testOneCentUnderRemainingNotReached() {
        savingsGoal goal = new savingsGoal("Laptop", 1000.0);

        goal.addContribution(999.99);

        assertFalse(goal.isReached());
        assertEquals(0.01, goal.getRemaining(), DELTA);
    }

    @Test
    @DisplayName("Error message for too-large contribution mentions what is still needed")
    void testOverContributionMessage() {
        savingsGoal goal = new savingsGoal("Laptop", 100.0);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, () -> goal.addContribution(150.0));

        assertTrue(error.getMessage().contains("R100.00"),
                "Message should show the remaining amount, was: " + error.getMessage());
    }

    @Test
    @DisplayName("Boundary: zero contribution is rejected")
    void testZeroContributionRejected() {
        savingsGoal goal = new savingsGoal("Laptop", 100.0);

        assertThrows(IllegalArgumentException.class, () -> goal.addContribution(0.0));
    }

    @Test
    @DisplayName("Negative: negative contribution is rejected and changes nothing")
    void testNegativeContributionRejected() {
        savingsGoal goal = new savingsGoal("Laptop", 100.0);
        goal.addContribution(40.0);

        assertThrows(IllegalArgumentException.class, () -> goal.addContribution(-10.0));

        assertEquals(40.0, goal.getSavedAmount(), DELTA);
    }

    @Test
    @DisplayName("Boundary: contribution that rounds to zero cents is rejected")
    void testContributionRoundingToZeroRejected() {
        savingsGoal goal = new savingsGoal("Laptop", 100.0);

        assertThrows(IllegalArgumentException.class, () -> goal.addContribution(0.004));
    }

    @Test
    @DisplayName("Negative: NaN and infinite contributions are rejected")
    void testNaNAndInfiniteContributionsRejected() {
        savingsGoal goal = new savingsGoal("Laptop", 100.0);

        assertThrows(IllegalArgumentException.class, () -> goal.addContribution(Double.NaN));
        assertThrows(IllegalArgumentException.class,
                () -> goal.addContribution(Double.POSITIVE_INFINITY));
        assertEquals(0.0, goal.getSavedAmount(), DELTA);
    }

    @Test
    @DisplayName("State: a reached goal refuses further contributions")
    void testReachedGoalRefusesContributions() {
        savingsGoal goal = new savingsGoal("Laptop", 100.0);
        goal.addContribution(100.0);

        assertThrows(IllegalStateException.class, () -> goal.addContribution(1.0));

        assertEquals(100.0, goal.getSavedAmount(), DELTA);
    }

    // ---------- Progress percent ----------

    @Test
    @DisplayName("Progress percent is saved divided by target")
    void testProgressPercent() {
        savingsGoal goal = new savingsGoal("Laptop", 200.0);

        goal.addContribution(50.0);

        assertEquals(25.0, goal.getProgressPercent(), DELTA);
    }

    @Test
    @DisplayName("Boundary: 0% at start, 100% when reached, never above 100%")
    void testProgressPercentBoundaries() {
        savingsGoal goal = new savingsGoal("Laptop", 300.0);
        assertEquals(0.0, goal.getProgressPercent(), DELTA);

        goal.addContribution(300.0);

        assertEquals(100.0, goal.getProgressPercent(), DELTA);
    }

    // ---------- Progress bar ----------

    @Test
    @DisplayName("Progress bar is empty at 0%")
    void testProgressBarEmpty() {
        savingsGoal goal = new savingsGoal("Laptop", 100.0);

        assertEquals("[" + "-".repeat(20) + "]", goal.getProgressBar());
    }

    @Test
    @DisplayName("Progress bar is half full at 50%")
    void testProgressBarHalf() {
        savingsGoal goal = new savingsGoal("Laptop", 100.0);

        goal.addContribution(50.0);

        assertEquals("[" + "#".repeat(10) + "-".repeat(10) + "]", goal.getProgressBar());
    }

    @Test
    @DisplayName("Progress bar is full only when the goal is reached")
    void testProgressBarFullOnlyWhenReached() {
        savingsGoal goal = new savingsGoal("Laptop", 100.0);

        goal.addContribution(99.0);
        assertFalse(goal.getProgressBar().equals("[" + "#".repeat(20) + "]"),
                "99% must not show a full bar");

        goal.addContribution(1.0);
        assertEquals("[" + "#".repeat(20) + "]", goal.getProgressBar());
    }

    @Test
    @DisplayName("Progress bar always has the same length")
    void testProgressBarConstantLength() {
        savingsGoal goal = new savingsGoal("Laptop", 1000.0);
        double[] steps = {1.0, 99.0, 150.0, 250.0, 500.0};

        assertEquals(22, goal.getProgressBar().length());
        for (double step : steps) {
            goal.addContribution(step);
            assertEquals(22, goal.getProgressBar().length());
        }
    }

    // ---------- money() formatting ----------

    @Test
    @DisplayName("money formats rand with two decimals")
    void testMoneyFormat() {
        assertEquals("R250.00", savingsGoal.money(250));
        assertEquals("R250.50", savingsGoal.money(250.5));
        assertEquals("R0.00", savingsGoal.money(0));
        assertEquals("R0.01", savingsGoal.money(0.01));
    }

    @Test
    @DisplayName("money rounds to cents and uses a dot as decimal separator")
    void testMoneyRoundingAndSeparator() {
        assertEquals("R10.13", savingsGoal.money(10.126));
        assertEquals("R1000000.99", savingsGoal.money(1_000_000.99));
    }
}