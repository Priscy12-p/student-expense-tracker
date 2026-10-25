

public class main {

    public static void main(String[] args) {

        expenseManager manager = new expenseManager();

        System.out.println("================================");
        System.out.println("   STUDENT EXPENSE TRACKER");
        System.out.println("================================");

        expense expense1 = new expense(
                "Lunch",
                "Food",
                50.00,
                "2026-10-25"
        );

        expense expense2 = new expense(
                "Taxi",
                "Transport",
                30.00,
                "2026-10-25"
        );

        manager.addexpense(expense1);
        manager.addexpense(expense2);

        System.out.println();
        System.out.println("YOUR EXPENSES");
        manager.displayexpenses();

        System.out.println();
        System.out.println("TOTAL EXPENSES: R" + manager.calculateTotalExpenses());
    }
}
