import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end (system) test of the console program, with no mocks.
 *
 * A complete user session is typed into System.in as text and the real
 * main.main() runs from start to finish. We then check the console output.
 *
 * IMPORTANT: main keeps ONE static Scanner that is created the first time
 * the class is used and closed when the program exits. That is why the whole
 * session is a single test method, and why no other test class may use main.
 *
 * Option 5 (save report to expenses.csv) is intentionally not typed in here:
 * it writes into the working directory. Saving is covered with temp files in
 * expenseManagerTest.
 *
 * QA techniques used: system/end-to-end testing, negative input testing,
 * state testing (savings goal lifecycle), boundary values on input.
 */
public class mainTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private final Locale originalLocale = Locale.getDefault();

    private ByteArrayOutputStream captured;

    @BeforeEach
    void setUp() {
        // main formats percentages with the default locale; use a fixed one
        // so "40.0%" is not printed as "40,0%" on some machines.
        Locale.setDefault(Locale.US);
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
        Locale.setDefault(originalLocale);
    }

    private static String session(String... lines) {
        return String.join("\n", lines) + "\n";
    }

    // Checks that every piece of text appears, in this order, in the output.
    private static void assertInOrder(String output, String... expectedParts) {
        int position = 0;
        for (String part : expectedParts) {
            int found = output.indexOf(part, position);
            assertTrue(found >= 0,
                    "Expected to find (after position " + position + "): \"" + part
                            + "\"\n--- full output ---\n" + output);
            position = found + part.length();
        }
    }

    @Test
    @DisplayName("Full session: bad input is handled, expenses, budget and savings all work")
    void testFullUserSession() {
        String typed = session(
                // --- monthly budget: invalid, negative, zero, then valid "R1000,50"
                "abc", "-5", "0", "R1000,50",

                // --- menu: non-number, out-of-range number
                "abc", "42",

                // --- screens with nothing set up yet
                "2",            // view expenses (none)
                "7",            // add savings with no goal
                "8",            // savings progress with no goal

                // --- add expense 1: empty description, bad amounts, then valid
                "1", "", "Lunch", "Food", "abc", "0", "R50", "2026-10-25",

                // --- add expense 2 (pushes spending over the budget)
                "1", "Bus", "Travel", "1200,25", "2026-10-26",

                "2",            // view expenses
                "3",            // total
                "4",            // budget summary (over budget)

                // --- savings goal lifecycle
                "6", "Laptop", "1000",      // set goal
                "7", "2000",                // too much: rejected
                "7", "400",                 // valid contribution
                "6", "n",                   // do not replace goal that has savings
                "7", "600",                 // exactly the remaining amount: goal reached
                "7",                        // goal already reached
                "8",                        // progress at 100%
                "6", "y", "Phone", "R300,50", // replace goal after confirming

                // --- exit
                "9"
        );

        System.setIn(new ByteArrayInputStream(typed.getBytes(StandardCharsets.UTF_8)));

        assertDoesNotThrow(() -> main.main(new String[0]));

        String out = captured.toString(StandardCharsets.UTF_8);

        assertInOrder(out,
                // budget input validation
                "Enter your monthly budget in rand (R): ",
                "That is not a valid amount",
                "Amount cannot be negative.",
                "Amount must be greater than zero.",

                // home screen and menu input validation
                "STUDENT EXPENSE TRACKER",
                "Savings goal: none set yet (choose option 6)",
                "Please enter a whole number",
                "Invalid option. Please choose 1 to 9.",

                // nothing set up yet
                "No expenses recorded yet.",
                "Set a savings goal first (option 6).",
                "No savings goal set yet (choose option 6).",

                // adding an expense with bad input first
                "Input cannot be empty. Please try again.",
                "That is not a valid amount",
                "Amount must be greater than zero.",
                "Expense added successfully!",
                "Expense added successfully!",

                // view expenses
                "Description: Lunch", "Category: Food", "Amount: R50.0", "Date: 2026-10-25",
                "Description: Bus", "Category: Travel", "Amount: R1200.25", "Date: 2026-10-26",

                // total and budget summary (1000.50 budget, 1250.25 spent)
                "Total expenses: R1250.25",
                "BUDGET SUMMARY",
                "Monthly budget: R1000.5",
                "Warning: You are over budget!",
                "Amount over budget: R249.75",

                // savings lifecycle
                "Savings goal set: Laptop (target R1000.00)",
                "You still need R1000.00",
                "That is more than you still need (R1000.00).",
                "Added R400.00 to Laptop",
                "SAVINGS PROGRESS",
                "Progress: 40.0%",
                "Keeping your current goal.",
                "Added R600.00 to Laptop",
                "Congratulations! You reached your goal: Laptop",
                "Goal reached!",
                "You have already reached your goal: Laptop",
                "Progress: 100.0%",
                "Savings goal set: Phone (target R300.50)",

                // clean exit
                "Thank you for using Student Expense Tracker!"
        );

        // Bad input must never crash the program or leak a stack trace.
        assertFalse(out.contains("Exception"), "Unexpected exception text in output");
        assertFalse(out.contains("\tat "), "Unexpected stack trace in output");
    }
}