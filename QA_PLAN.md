# QA Plan - Student Expense Tracker

## 1. Purpose and scope

Make sure the application records expenses, checks them against a monthly
budget, tracks a savings goal and exports a CSV report **correctly and
safely**, even when the user types bad input.

In scope: `expense`, `budget`, `expenseManager`, `savingsGoal`, console menu in `main`.
Out of scope: cloud/AWS integration, performance and security testing.

## 2. Quality objectives

| Objective | How it is checked |
|---|---|
| Calculations are correct (totals, remaining budget, progress) | Unit tests with boundary values |
| Bad input never crashes the program | `mainTest` end-to-end session, input validation tests |
| Saved CSV matches what the user entered | `expenseManagerTest` writes and reads real files |
| Defects are found, recorded and traced to a cause | Section 6 and `ROOT_CAUSE_ANALYSIS.md` |
| Risks are rated and reduced | `FMEA.md` |

## 3. Test levels and techniques

| Level | Where | Techniques |
|---|---|---|
| Unit | `expenseTest`, `budgetTest`, `expenseManagerTest`, `savingsGoalTest` | Equivalence partitioning, boundary value analysis, positive and negative testing, state testing |
| System / end-to-end | `mainTest` | Scripted user session, negative input testing |

No mocks are used: tests run real objects, real temp files and real console output.

## 3a. Entry and exit criteria

- **Entry:** code compiles with `mvn compile`.
- **Exit:** all tests pass with `mvn test`; every defect in section 6 is either fixed (with a test) or documented as a known limitation.

## 4. Quality metrics (KPIs)

| KPI | Target | Current |
|---|---|---|
| Tests passing | 100% | 79 of 79 (run `mvn test` to confirm on your machine) |
| Line coverage | 80%+ | Not measured yet (add JaCoCo) |
| Open defects | 0 critical | 5 open, none critical (section 6) |
| Defects found by tests vs. by users | Most found by tests | All 5 found by test design |

Update the "Current" column after each run. Do not record a number you have not measured.

## 5. Risks

See `FMEA.md` for the rated list of failure modes.

## 6. Defect log

| ID | Defect | Found by | Severity | Status |
|---|---|---|---|---|
| D1 | Commas in text fields are not escaped, so the CSV gets extra columns | `testCsvDoesNotEscapeCommas` | Medium | Open |
| D2 | `expense` accepts negative amounts and empty text (validation only in `main`) | `testNegativeAmountIsNotValidatedByClass` | Low | Open |
| D3 | Date is free text, not checked as a real `YYYY-MM-DD` date | `testDateIsPlainText` | Medium | Open |
| D4 | Percentages use the machine locale (`40,0%` vs `40.0%`) while `money()` uses a dot | Found while writing `mainTest` | Low | Open |
| D5 | Totals print as `R1000.5` while savings print as `R1000.00` | Reading `mainTest` output | Low | Open |

## 7. Continuous improvement

Every fixed defect gets a regression test first (write the failing test, fix
the code, see it pass), then the status above is updated.
