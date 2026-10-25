
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class expenseManager {

    private ArrayList<expense> expenses;

    public expenseManager() {
        expenses = new ArrayList<>();
    }

    public void addexpense(expense expense) {
        expenses.add(expense);
        System.out.println("Expense added successfully!");
    }

    public void displayexpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }

        for (expense expense : expenses) {
            System.out.println("----------------------------");
            System.out.println("Description: " + expense.getDescription());
            System.out.println("Category: " + expense.getCategory());
            System.out.println("Amount: R" + expense.getAmount());
            System.out.println("Date: " + expense.getDate());
        }

        System.out.println("----------------------------");
    }

    public double calculateTotalExpenses() {
        double total = 0;

        for (expense expense : expenses) {
            total = total + expense.getAmount();
        }

        return total;
    }

    public String createCsvReport() {
        String report = "Description,Category,Amount,Date\n";

        for (expense expense : expenses) {
            report = report
                    + expense.getDescription() + ","
                    + expense.getCategory() + ","
                    + expense.getAmount() + ","
                    + expense.getDate() + "\n";
        }

        return report;
    }


    public void saveCsvReport(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(createCsvReport());
            System.out.println("CSV report saved successfully!");
        } catch (IOException e) {
            System.out.println("Could not save CSV report.");
        }
    }


}
