import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for expenseManager.
 *
 * No mocks are used. Real expense objects are added to a real manager,
 * real files are written to a JUnit-managed temporary folder, and the
 * console output is captured from System.out so it can be checked.
 *
 * QA techniques used:
 *  - Equivalence partitioning: empty list / one item / many items
 *  - Boundary value analysis: 0 items, 1 item, zero and cent amounts
 *  - Positive and negative scenarios (saving to a valid and invalid path)
 */
public class expenseManagerTest {

    private static final double DELTA = 0.0001;
    private static final String NL = System.lineSeparator();

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream captured;

    @BeforeEach
    void captureConsole() {
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreConsole() {
        System.setOut(originalOut);
    }

    private String console() {
        return captured.toString(StandardCharsets.UTF_8);
    }

    // ---------- addexpense + calculateTotalExpenses ----------

    @Test
    @DisplayName("Total of two expenses is their sum")
    void testAddExpenseAndCalculateTotal() {
        expenseManager manager = new expenseManager();

        manager.addexpense(new expense("Lunch", "Food", 50.0, "2026-10-25"));
        manager.addexpense(new expense("Transport", "Travel", 30.0, "2026-10-25"));

        assertEquals(80.0, manager.calculateTotalExpenses(), DELTA);
    }

    @Test
    @DisplayName("Boundary: a new manager with no expenses has a total of zero")
    void testTotalWithNoExpenses() {
        expenseManager manager = new expenseManager();

        assertEquals(0.0, manager.calculateTotalExpenses(), DELTA);
    }

    @Test
    @DisplayName("Boundary: a single expense gives a total equal to that expense")
    void testTotalWithOneExpense() {
        expenseManager manager = new expenseManager();

        manager.addexpense(new expense("Book", "Study", 199.99, "2026-10-25"));

        assertEquals(199.99, manager.calculateTotalExpenses(), DELTA);
    }

    @Test
    @DisplayName("Many expenses are all included in the total")
    void testTotalWithManyExpenses() {
        expenseManager manager = new expenseManager();

        for (int i = 1; i <= 100; i++) {
            manager.addexpense(new expense("Item " + i, "Test", 10.0, "2026-10-25"));
        }

        assertEquals(1000.0, manager.calculateTotalExpenses(), DELTA);
    }

    @Test
    @DisplayName("Cents add up correctly within a small tolerance")
    void testTotalWithCents() {
        expenseManager manager = new expenseManager();

        manager.addexpense(new expense("A", "X", 0.10, "2026-10-25"));
        manager.addexpense(new expense("B", "X", 0.20, "2026-10-25"));

        assertEquals(0.30, manager.calculateTotalExpenses(), DELTA);
    }

    @Test
    @DisplayName("Zero-amount expense does not change the total")
    void testZeroAmountExpenseDoesNotChangeTotal() {
        expenseManager manager = new expenseManager();

        manager.addexpense(new expense("Real", "X", 40.0, "2026-10-25"));
        manager.addexpense(new expense("Zero", "X", 0.0, "2026-10-25"));

        assertEquals(40.0, manager.calculateTotalExpenses(), DELTA);
    }

    @Test
    @DisplayName("Total keeps growing as expenses are added one by one")
    void testTotalGrowsStepByStep() {
        expenseManager manager = new expenseManager();

        manager.addexpense(new expense("One", "X", 10.0, "2026-10-25"));
        assertEquals(10.0, manager.calculateTotalExpenses(), DELTA);

        manager.addexpense(new expense("Two", "X", 15.5, "2026-10-25"));
        assertEquals(25.5, manager.calculateTotalExpenses(), DELTA);

        manager.addexpense(new expense("Three", "X", 4.5, "2026-10-25"));
        assertEquals(30.0, manager.calculateTotalExpenses(), DELTA);
    }

    @Test
    @DisplayName("Adding an expense prints a confirmation message")
    void testAddExpensePrintsConfirmation() {
        expenseManager manager = new expenseManager();

        manager.addexpense(new expense("Lunch", "Food", 50.0, "2026-10-25"));

        assertEquals("Expense added successfully!" + NL, console());
    }

    // ---------- displayexpenses ----------

    @Test
    @DisplayName("Display with no expenses shows the empty-state message")
    void testDisplayWhenEmpty() {
        expenseManager manager = new expenseManager();

        manager.displayexpenses();

        assertEquals("No expenses recorded yet." + NL, console());
    }

    @Test
    @DisplayName("Display shows every field of an expense")
    void testDisplayShowsExpenseDetails() {
        expenseManager manager = new expenseManager();
        manager.addexpense(new expense("Lunch", "Food", 50.0, "2026-10-25"));
        captured.reset(); // ignore the "added" message

        manager.displayexpenses();

        String out = console();
        assertTrue(out.contains("Description: Lunch"));
        assertTrue(out.contains("Category: Food"));
        assertTrue(out.contains("Amount: R50.0"));
        assertTrue(out.contains("Date: 2026-10-25"));
        assertFalse(out.contains("No expenses recorded yet."));
    }

    @Test
    @DisplayName("Display lists expenses in the order they were added")
    void testDisplayKeepsInsertionOrder() {
        expenseManager manager = new expenseManager();
        manager.addexpense(new expense("First", "A", 1.0, "2026-10-01"));
        manager.addexpense(new expense("Second", "B", 2.0, "2026-10-02"));
        captured.reset();

        manager.displayexpenses();

        String out = console();
        assertTrue(out.indexOf("Description: First") < out.indexOf("Description: Second"));
    }

    // ---------- createCsvReport ----------

    @Test
    @DisplayName("CSV report for one expense has a header and one row")
    void testCreateCsvReport() {
        expenseManager manager = new expenseManager();

        manager.addexpense(new expense("Lunch", "Food", 50.0, "2026-10-25"));

        String report = manager.createCsvReport();

        assertEquals(
                "Description,Category,Amount,Date\n"
                        + "Lunch,Food,50.0,2026-10-25\n",
                report
        );
    }

    @Test
    @DisplayName("Boundary: CSV report with no expenses contains only the header")
    void testCsvReportWhenEmpty() {
        expenseManager manager = new expenseManager();

        assertEquals("Description,Category,Amount,Date\n", manager.createCsvReport());
    }

    @Test
    @DisplayName("CSV report has one row per expense, in insertion order")
    void testCsvReportWithManyRows() {
        expenseManager manager = new expenseManager();

        manager.addexpense(new expense("Lunch", "Food", 50.0, "2026-10-25"));
        manager.addexpense(new expense("Bus", "Travel", 30.5, "2026-10-26"));
        manager.addexpense(new expense("Book", "Study", 199.99, "2026-10-27"));

        assertEquals(
                "Description,Category,Amount,Date\n"
                        + "Lunch,Food,50.0,2026-10-25\n"
                        + "Bus,Travel,30.5,2026-10-26\n"
                        + "Book,Study,199.99,2026-10-27\n",
                manager.createCsvReport()
        );
    }

    @Test
    @DisplayName("Creating the CSV report does not change the expenses or the total")
    void testCsvReportHasNoSideEffects() {
        expenseManager manager = new expenseManager();
        manager.addexpense(new expense("Lunch", "Food", 50.0, "2026-10-25"));

        String first = manager.createCsvReport();
        String second = manager.createCsvReport();

        assertEquals(first, second);
        assertEquals(50.0, manager.calculateTotalExpenses(), DELTA);
    }

    @Test
    @DisplayName("Known limitation: a comma inside a field is NOT escaped in the CSV")
    void testCsvDoesNotEscapeCommas() {
        // This documents CURRENT behaviour. A description containing a comma
        // produces an extra column, so the file is no longer valid 4-column CSV.
        expenseManager manager = new expenseManager();

        manager.addexpense(new expense("Pizza, large", "Food", 90.0, "2026-10-25"));

        String dataRow = manager.createCsvReport().split("\n")[1];

        assertEquals("Pizza, large,Food,90.0,2026-10-25", dataRow);
        assertEquals(5, dataRow.split(",").length,
                "Row has 5 fields instead of the expected 4");
    }

    // ---------- saveCsvReport (real files) ----------

    @Test
    @DisplayName("Positive: saving writes the CSV report to a real file")
    void testSaveCsvReportWritesFile(@TempDir Path tempDir) throws IOException {
        expenseManager manager = new expenseManager();
        manager.addexpense(new expense("Lunch", "Food", 50.0, "2026-10-25"));
        manager.addexpense(new expense("Bus", "Travel", 30.0, "2026-10-26"));
        Path file = tempDir.resolve("expenses.csv");

        manager.saveCsvReport(file.toString());

        assertTrue(Files.exists(file));
        assertEquals(manager.createCsvReport(), Files.readString(file));
    }

    @Test
    @DisplayName("Saved file has a header plus one line per expense")
    void testSavedFileLineCount(@TempDir Path tempDir) throws IOException {
        expenseManager manager = new expenseManager();
        manager.addexpense(new expense("A", "X", 1.0, "2026-10-25"));
        manager.addexpense(new expense("B", "X", 2.0, "2026-10-25"));
        manager.addexpense(new expense("C", "X", 3.0, "2026-10-25"));
        Path file = tempDir.resolve("report.csv");

        manager.saveCsvReport(file.toString());

        assertEquals(4, Files.readAllLines(file).size());
    }

    @Test
    @DisplayName("Boundary: saving with no expenses writes just the header")
    void testSaveEmptyReport(@TempDir Path tempDir) throws IOException {
        expenseManager manager = new expenseManager();
        Path file = tempDir.resolve("empty.csv");

        manager.saveCsvReport(file.toString());

        assertEquals("Description,Category,Amount,Date\n", Files.readString(file));
    }

    @Test
    @DisplayName("Positive: successful save prints a success message")
    void testSaveSuccessMessage(@TempDir Path tempDir) {
        expenseManager manager = new expenseManager();
        Path file = tempDir.resolve("ok.csv");

        manager.saveCsvReport(file.toString());

        assertEquals("CSV report saved successfully!" + NL, console());
    }

    @Test
    @DisplayName("Saving again overwrites the old file instead of appending")
    void testSaveOverwritesExistingFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("expenses.csv");
        Files.writeString(file, "old content that must disappear\n");
        expenseManager manager = new expenseManager();
        manager.addexpense(new expense("Lunch", "Food", 50.0, "2026-10-25"));

        manager.saveCsvReport(file.toString());

        String content = Files.readString(file);
        assertFalse(content.contains("old content"));
        assertEquals(manager.createCsvReport(), content);
    }

    @Test
    @DisplayName("Negative: an unwritable path does not crash and reports the problem")
    void testSaveToInvalidPath(@TempDir Path tempDir) {
        expenseManager manager = new expenseManager();
        manager.addexpense(new expense("Lunch", "Food", 50.0, "2026-10-25"));
        captured.reset();
        // The parent folder does not exist, so FileWriter must fail.
        Path badFile = tempDir.resolve("missing-folder").resolve("expenses.csv");

        assertDoesNotThrow(() -> manager.saveCsvReport(badFile.toString()));

        assertEquals("Could not save CSV report." + NL, console());
        assertFalse(Files.exists(badFile));
    }

    @Test
    @DisplayName("Negative: saving to a path that is a directory does not crash")
    void testSaveToDirectoryPath(@TempDir Path tempDir) {
        expenseManager manager = new expenseManager();
        captured.reset();

        assertDoesNotThrow(() -> manager.saveCsvReport(tempDir.toString()));

        assertEquals("Could not save CSV report." + NL, console());
    }
}