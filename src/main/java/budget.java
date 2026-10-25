
public class budget {

    private double monthlyBudget;

    public budget(double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    public boolean isOverBudget(double totalExpenses) {
        return totalExpenses > monthlyBudget;
    }

    public double getRemainingBudget(double totalExpenses) {
        return monthlyBudget - totalExpenses;
    }
}
