import java.util.Locale;

/**
 * A single savings goal: a name, a target amount and how much has been saved.
 *
 * Rules:
 *  - name must not be blank, target must be greater than zero
 *  - a contribution must be greater than zero
 *  - a contribution may not be more than what is still needed
 *  - once the goal is reached no more contributions are accepted
 *
 * All money is rounded to cents so repeated additions do not drift
 * (for example 0.1 + 0.2).
 */
public class savingsGoal {

    // Number of characters inside the [ ] of the progress bar.
    public static final int BAR_WIDTH = 20;

    private final String name;
    private final double targetAmount;
    private double savedAmount;

    public savingsGoal(String name, double targetAmount) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Goal name cannot be empty.");
        }
        double target = roundToCents(targetAmount);
        if (Double.isNaN(targetAmount) || Double.isInfinite(targetAmount) || target <= 0) {
            throw new IllegalArgumentException("Target amount must be greater than zero.");
        }
        this.name = name.trim();
        this.targetAmount = target;
        this.savedAmount = 0;
    }

    public String getName() {
        return name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public double getSavedAmount() {
        return savedAmount;
    }

    // How much is still needed. Never negative.
    public double getRemaining() {
        return Math.max(0, roundToCents(targetAmount - savedAmount));
    }

    // 0.0 to 100.0
    public double getProgressPercent() {
        double percent = savedAmount / targetAmount * 100.0;
        return Math.min(100.0, percent);
    }

    public boolean isReached() {
        return savedAmount >= targetAmount;
    }

    // Example: [#####---------------]
    // A full bar is only shown when the goal is really reached.
    public String getProgressBar() {
        int filled = (int) Math.floor(getProgressPercent() / 100.0 * BAR_WIDTH);
        if (isReached()) {
            filled = BAR_WIDTH;
        }
        return "[" + "#".repeat(filled) + "-".repeat(BAR_WIDTH - filled) + "]";
    }

    public void addContribution(double amount) {
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new IllegalArgumentException("Contribution must be a valid number.");
        }
        double contribution = roundToCents(amount);
        if (contribution <= 0) {
            throw new IllegalArgumentException("Contribution must be greater than zero.");
        }
        if (isReached()) {
            throw new IllegalStateException("Goal already reached: " + name);
        }
        if (contribution > getRemaining()) {
            throw new IllegalArgumentException(
                    "That is more than you still need (" + money(getRemaining()) + ")."
            );
        }
        savedAmount = roundToCents(savedAmount + contribution);
    }

    // Formats an amount as rand with two decimals, for example R250.50
    public static String money(double amount) {
        return String.format(Locale.US, "R%.2f", amount);
    }

    private static double roundToCents(double value) {
        return Math.round(value * 100) / 100.0;
    }
}