import java.util.Scanner;

public class main {

    // One Scanner shared by the whole program.
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        expenseManager manager = new expenseManager();

        double monthlyBudget = readPositiveAmount(
                "Enter your monthly budget in rand (R): "
        );

        budget budget = new budget(monthlyBudget);

        // null means the user has not set a savings goal yet.
        savingsGoal goal = null;

        int choice = 0;

        while (choice != 9) {

            printHome(goal);
            printMenu();

            choice = readInt("Choose an option: ");

            if (choice == 1) {

                String description = readNonEmpty("Enter expense description: ");
                String category = readNonEmpty("Enter expense category: ");
                double amount = readPositiveAmount("Enter amount in rand (R): ");
                String date = readNonEmpty("Enter date (YYYY-MM-DD): ");

                expense expense = new expense(
                        description,
                        category,
                        amount,
                        date
                );

                manager.addexpense(expense);

            } else if (choice == 2) {

                manager.displayexpenses();

            } else if (choice == 3) {

                System.out.println(
                        "Total expenses: R" + manager.calculateTotalExpenses()
                );

            } else if (choice == 4) {

                double totalExpenses = manager.calculateTotalExpenses();
                double remainingBudget =
                        budget.getRemainingBudget(totalExpenses);

                System.out.println();
                System.out.println("======= BUDGET SUMMARY =======");
                System.out.println(
                        "Monthly budget: R" + budget.getMonthlyBudget()
                );
                System.out.println("Total expenses: R" + totalExpenses);

                if (budget.isOverBudget(totalExpenses)) {

                    System.out.println("Warning: You are over budget!");
                    System.out.println(
                            "Amount over budget: R" + Math.abs(remainingBudget)
                    );

                } else {

                    System.out.println(
                            "Remaining budget: R" + remainingBudget
                    );

                }

                System.out.println("==============================");

            } else if (choice == 5) {

                manager.saveCsvReport("expenses.csv");

            } else if (choice == 6) {

                goal = setSavingsGoal(goal);

            } else if (choice == 7) {

                addSavings(goal);

            } else if (choice == 8) {

                showSavingsProgress(goal);

            } else if (choice == 9) {

                System.out.println(
                        "Thank you for using Student Expense Tracker!"
                );

            } else {

                System.out.println("Invalid option. Please choose 1 to 9.");

            }
        }

        scanner.close();
    }

    // ------------------------------------------------------------------
    // Screens
    // ------------------------------------------------------------------

    // The "home screen": shows the selected savings goal above the menu.
    private static void printHome(savingsGoal goal) {

        System.out.println();
        System.out.println("================================");
        System.out.println("   STUDENT EXPENSE TRACKER");
        System.out.println("================================");

        if (goal == null) {

            System.out.println("Savings goal: none set yet (choose option 6)");

        } else {

            System.out.println("Savings goal: " + goal.getName());
            System.out.println(
                    "Saved "
                            + savingsGoal.money(goal.getSavedAmount())
                            + " of "
                            + savingsGoal.money(goal.getTargetAmount())
                            + String.format(
                            " (%.1f%%)", goal.getProgressPercent()
                    )
            );
            System.out.println(goal.getProgressBar());

            if (goal.isReached()) {
                System.out.println("Goal reached!");
            }
        }

        System.out.println("--------------------------------");
    }

    private static void printMenu() {

        System.out.println("1. Add an expense");
        System.out.println("2. View all expenses");
        System.out.println("3. View total expenses");
        System.out.println("4. View budget summary");
        System.out.println("5. Save expense report");
        System.out.println("6. Set a savings goal");
        System.out.println("7. Add savings");
        System.out.println("8. View savings progress");
        System.out.println("9. Exit");
    }

    // ------------------------------------------------------------------
    // Savings actions
    // ------------------------------------------------------------------

    // Task 1: set a savings goal. Returns the goal that should be used next.
    private static savingsGoal setSavingsGoal(savingsGoal current) {

        // Do not throw away saved money by accident.
        if (current != null && current.getSavedAmount() > 0) {

            String answer = readNonEmpty(
                    "You already have a goal with savings in it. Replace it? (y/n): "
            );

            if (!answer.equalsIgnoreCase("y")) {
                System.out.println("Keeping your current goal.");
                return current;
            }
        }

        String name = readNonEmpty("Enter goal name: ");
        double target = readPositiveAmount("Enter target amount in rand (R): ");

        savingsGoal goal = new savingsGoal(name, target);

        System.out.println(
                "Savings goal set: "
                        + goal.getName()
                        + " (target "
                        + savingsGoal.money(goal.getTargetAmount())
                        + ")"
        );

        return goal;
    }

    // Task 2: record a contribution and show the updated progress.
    private static void addSavings(savingsGoal goal) {

        if (goal == null) {
            System.out.println("Set a savings goal first (option 6).");
            return;
        }

        // Task 3: handle a goal that has already been reached.
        if (goal.isReached()) {
            System.out.println(
                    "You have already reached your goal: " + goal.getName()
            );
            System.out.println("Set a new goal with option 6 to keep saving.");
            return;
        }

        System.out.println(
                "You still need " + savingsGoal.money(goal.getRemaining())
        );

        double amount = readPositiveAmount(
                "Enter contribution in rand (R): "
        );

        try {

            goal.addContribution(amount);

            System.out.println(
                    "Added " + savingsGoal.money(amount) + " to " + goal.getName()
            );

            showSavingsProgress(goal);

            if (goal.isReached()) {
                System.out.println();
                System.out.println(
                        "Congratulations! You reached your goal: "
                                + goal.getName()
                );
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            // For example: the contribution was more than what is still needed.
            System.out.println(e.getMessage());
        }
    }

    private static void showSavingsProgress(savingsGoal goal) {

        if (goal == null) {
            System.out.println("No savings goal set yet (choose option 6).");
            return;
        }

        System.out.println();
        System.out.println("===== SAVINGS PROGRESS =====");
        System.out.println("Goal: " + goal.getName());
        System.out.println(
                "Target: " + savingsGoal.money(goal.getTargetAmount())
        );
        System.out.println(
                "Saved: " + savingsGoal.money(goal.getSavedAmount())
        );
        System.out.println(
                "Remaining: " + savingsGoal.money(goal.getRemaining())
        );
        System.out.println(
                String.format("Progress: %.1f%%", goal.getProgressPercent())
        );
        System.out.println(goal.getProgressBar());
        System.out.println("============================");
    }

    // ------------------------------------------------------------------
    // Safe input helpers (Task 3)
    //
    // Everything is read as a line of text first and checked ourselves,
    // so typing letters can never crash the program.
    // ------------------------------------------------------------------

    // Keeps asking until the user types something that is not empty.
    private static String readNonEmpty(String prompt) {

        while (true) {

            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    // Keeps asking until the user types a whole number.
    private static int readInt(String prompt) {

        while (true) {

            String input = readNonEmpty(prompt);

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number, for example 6.");
            }
        }
    }

    // Keeps asking until the user types an amount above zero.
    // Accepts "250", "250.50", "250,50" and "R250".
    private static double readPositiveAmount(String prompt) {

        while (true) {

            String input = readNonEmpty(prompt);

            // Allow "R250" and a comma as the decimal separator.
            input = input.replaceFirst("^[Rr]\\s*", "").replace(',', '.');

            // Digits with an optional minus sign and decimal part only.
            // This also blocks things like "1e5", "5d", "NaN" and "Infinity".
            if (!input.matches("-?\\d*\\.?\\d+")) {
                System.out.println(
                        "That is not a valid amount. Use numbers only, "
                                + "for example 250.50."
                );
                continue;
            }

            // Round to cents.
            double value = Math.round(Double.parseDouble(input) * 100) / 100.0;

            if (value < 0) {
                System.out.println("Amount cannot be negative.");
            } else if (value == 0) {
                System.out.println("Amount must be greater than zero.");
            } else {
                return value;
            }
        }
    }
}