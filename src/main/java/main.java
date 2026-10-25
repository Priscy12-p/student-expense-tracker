
import java.util.Scanner;

public class main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        expenseManager manager = new expenseManager();

        System.out.print("Enter your monthly budget in rand (R): ");
        double monthlyBudget = scanner.nextDouble();
        scanner.nextLine();

        while (monthlyBudget <= 0) {
            System.out.println("Budget must be greater than zero.");
            System.out.print("Enter your monthly budget in rand (R): ");
            monthlyBudget = scanner.nextDouble();
            scanner.nextLine();
        }

        budget budget = new budget(monthlyBudget);

        int choice = 0;

        while (choice != 5) {

            System.out.println();
            System.out.println("================================");
            System.out.println("   STUDENT EXPENSE TRACKER");
            System.out.println("================================");
            System.out.println("1. Add an expense");
            System.out.println("2. View all expenses");
            System.out.println("3. View total expenses");
            System.out.println("4. View budget summary");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {

                System.out.print("Enter expense description: ");
                String description = scanner.nextLine();

                System.out.print("Enter expense category: ");
                String category = scanner.nextLine();

                double amount;

                do {
                    System.out.print("Enter amount in rand (R): ");
                    amount = scanner.nextDouble();
                    scanner.nextLine();

                    if (amount <= 0) {
                        System.out.println(
                                "Amount must be greater than zero. Try again."
                        );
                    }

                } while (amount <= 0);

                System.out.print("Enter date (YYYY-MM-DD): ");
                String date = scanner.nextLine();

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

                System.out.println(
                        "Thank you for using Student Expense Tracker!"
                );

            } else {

                System.out.println("Invalid option. Please choose 1 to 5.");

            }
        }

        scanner.close();
    }
}
