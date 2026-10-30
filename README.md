# student-expense-tracker
A Java student budgeting application demonstrating cloud computing concepts and AWS integration.
# Student Expense Tracker

A Java console application that helps a student record expenses, stay within a
monthly budget and save towards a goal. The project is also a **Quality
Assurance (QA) showcase**: it is covered by automated tests and documented with
a QA plan, FMEA, root cause analysis and a defect log.

> Demo video (5-10 min): **TODO: paste your YouTube link here**

---

## Table of contents

1. [Features](#features)
2. [Requirements](#requirements)
3. [Build and run](#build-and-run)
4. [Using the app](#using-the-app)
5. [Project structure](#project-structure)
6. [Testing](#testing)
7. [QA documentation](#qa-documentation)
8. [Known issues](#known-issues)
9. [Roadmap](#roadmap)
10. [QA elective and courses](#qa-elective-and-courses)
11. [AI assistance](#ai-assistance)

---

## Features

- Set a monthly budget in rand (R)
- Add, list and total expenses (description, category, amount, date)
- Budget summary that shows the remaining amount or a warning when over budget
- Save an expense report as `expenses.csv`
- Savings goal: set a goal, add contributions, see progress as a percentage and
  a progress bar, and get a message when the goal is reached
- Safe input: letters, empty text, negative or zero amounts and out-of-range menu
  choices are rejected with a clear message instead of crashing the program

## Requirements

- Java 17 or newer
- Maven 3.x

## Build and run

```
mvn compile
java -cp target/classes main
```

Run the tests:

```
mvn test
```

## Using the app

On start, enter your monthly budget. Then pick an option from the menu:

| Option | Action |
|---|---|
| 1 | Add an expense |
| 2 | View all expenses |
| 3 | View total expenses |
| 4 | View budget summary |
| 5 | Save expense report (`expenses.csv`) |
| 6 | Set a savings goal |
| 7 | Add savings |
| 8 | View savings progress |
| 9 | Exit |

**Amounts** can be typed as `250`, `250.50`, `250,50` or `R250`. They are rounded
to cents and must be greater than zero.

**Savings rules:** a contribution cannot be more than what is still needed, a
reached goal accepts no more savings, and replacing a goal that already has
savings asks for confirmation first.

`expenses.csv` is listed in `.gitignore` so personal data is not committed.

## Project structure

```
student-expense-tracker/
├── pom.xml
├── README.md
├── TESTING.md
├── docs/
│   ├── QA_PLAN.md
│   ├── FMEA.md
│   ├── ROOT_CAUSE_ANALYSIS.md
│   └── COURSE_NOTES.md
└── src/
    ├── main/java/
    │   ├── main.java             console menu and safe input helpers
    │   ├── expense.java          one expense (data class)
    │   ├── expenseManager.java   list, total, display and CSV report
    │   ├── budget.java           monthly budget and over-budget check
    │   └── savingsGoal.java      savings goal rules and progress
    └── test/java/
        ├── expenseTest.java
        ├── budgetTest.java
        ├── expenseManagerTest.java
        ├── savingsGoalTest.java
        └── mainTest.java
```

## Testing

Tests use **JUnit 5**. No mocks are used: tests run real objects, write real
files to a temporary folder and read the real console output.

| Test file | Covers | Tests |
|---|---|---|
| `expenseTest.java` | `expense` | 10 |
| `budgetTest.java` | `budget` | 14 |
| `expenseManagerTest.java` | `expenseManager` | 23 |
| `savingsGoalTest.java` | `savingsGoal` | 31 |
| `mainTest.java` | `main` (full scripted user session) | 1 |

Techniques used: equivalence partitioning, boundary value analysis, positive and
negative testing, state testing, and end-to-end testing.

`mainTest` must stay the only test that uses `main`, because the program keeps
one static `Scanner`. See [TESTING.md](TESTING.md) for details.

## QA documentation

| Document | Content |
|---|---|
| [TESTING.md](TESTING.md) | Test approach, techniques, findings |
| [docs/QA_PLAN.md](docs/QA_PLAN.md) | Scope, objectives, test levels, entry/exit criteria, KPIs, defect log |
| [docs/FMEA.md](docs/FMEA.md) | Failure modes rated by severity, occurrence and detection (risk priority) |
| [docs/ROOT_CAUSE_ANALYSIS.md](docs/ROOT_CAUSE_ANALYSIS.md) | 5 Whys and fishbone analysis of the defects |
| [docs/COURSE_NOTES.md](docs/COURSE_NOTES.md) | My notes from the QA courses and where I applied them |

## Known issues

Found through test design. Details and status are in
[docs/QA_PLAN.md](docs/QA_PLAN.md).

| ID | Issue | Severity |
|---|---|---|
| D1 | Commas in text are not escaped in the CSV report | Medium |
| D2 | `expense` accepts negative amounts and empty text (validation is only in `main`) | Low |
| D3 | Dates are free text, not checked as real `YYYY-MM-DD` dates | Medium |
| D4 | Percentages use the machine locale (`40,0%` or `40.0%`) | Low |
| D5 | Totals print as `R1000.5` while savings print as `R1000.00` | Low |

## Roadmap

- Fix D1 to D5, each with a regression test first
- Measure coverage with JaCoCo
- Run `mvn test` automatically on every push with GitHub Actions
- Load expenses back from `expenses.csv` when the app starts
- Delete or edit an expense, and totals per category
- AWS integration, for example uploading the CSV report to cloud storage

