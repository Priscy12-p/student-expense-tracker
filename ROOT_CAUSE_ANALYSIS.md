# Root Cause Analysis

## Defect D1: commas in text break the CSV

**Problem statement:** A description such as `Pizza, large` produces a row with
5 fields instead of 4 (found by `testCsvDoesNotEscapeCommas`).

### 5 Whys

1. **Why does the row have 5 fields?** The comma inside the description is read as a separator.
2. **Why is it read as a separator?** `createCsvReport()` joins the fields with commas without protecting them.
3. **Why does it not protect them?** The method builds the text by simple string joining and never treats the data as something that may contain separators.
4. **Why was that not noticed earlier?** Early tests used simple values such as `Lunch` and `Food`.
5. **Why were only simple values tested?** Test cases were chosen from typical data, not from systematic input classes (text with commas, quotes, line breaks).

**Root cause:** the test design lacked data-based equivalence classes, and the
report builder has no escaping rule.

### Fishbone (cause categories)

| Category | Contributing cause |
|---|---|
| Method | CSV built by string joining, no escaping rule |
| Measurement (tests) | No test for special characters until boundary analysis was applied |
| Input | Free text allowed in description and category |
| Requirements | No stated rule for what characters are allowed in a field |
| Tools | No CSV library used |

### Corrective and preventive actions

| Type | Action |
|---|---|
| Corrective | Wrap fields containing `,`, `"` or line breaks in quotes and double any quotes |
| Corrective | Update `testCsvDoesNotEscapeCommas` into a passing regression test |
| Preventive | Add special-character cases to the equivalence classes for every text field |
| Preventive | Add a "round trip" test: write a CSV and read it back into the same values |

## Defect D3: dates are not validated

1. **Why can a bad date be saved?** The date is stored as plain text.
2. **Why plain text?** The data class only holds values; validation was left to `main`.
3. **Why does `main` not check the format?** It only checks that the date is not empty.
4. **Why only non-empty?** The requirement "date in YYYY-MM-DD format" was shown in the prompt but never turned into a rule or a test.

**Root cause:** a requirement existed only as prompt text, not as a testable rule.

**Actions:** validate with `LocalDate.parse`, add boundary tests (31 Feb, month 13, wrong separators).
