# FMEA - Failure Mode and Effects Analysis

Severity (S), Occurrence (O) and Detection (D) are rated 1-10.
**RPN = S x O x D.** A higher RPN means fix it sooner.

For Detection: 1 = almost certain to be caught, 10 = almost certain to be missed.

> The ratings below are my first judgement of this small project. Review and
> adjust them using your own reasoning and the scales from the course.

| # | Failure mode | Effect on user | Cause | S | O | D | RPN | Current control | Action |
|---|---|---|---|---|---|---|---|---|---|
| 1 | CSV row has extra columns | Report opens wrongly in a spreadsheet, data in wrong columns | Commas not escaped (D1) | 6 | 5 | 3 | 90 | Test documents the issue | Escape fields, add regression test |
| 2 | Invalid date saved | Wrong or unsortable data in the report | Date is free text (D3) | 5 | 6 | 7 | 210 | None | Validate `YYYY-MM-DD` |
| 3 | Negative or zero amount saved | Wrong totals | No validation inside `expense` (D2) | 7 | 2 | 4 | 56 | `main` input checks, tested end to end | Also validate in the class |
| 4 | Program crashes on bad menu input | Session lost | Unchecked number parsing | 8 | 2 | 2 | 32 | Safe input helpers, `mainTest` | Keep tests |
| 5 | Report not saved (bad path, no permission) | User thinks data is saved | File error | 7 | 3 | 3 | 63 | Error message shown, tested | None |
| 6 | Rounding drift in money totals | Savings goal never "reached" by a cent | Floating point | 6 | 4 | 3 | 72 | Amounts rounded to cents, drift test | None |
| 7 | Savings contribution above what is needed | Saved is larger than target | Missing rule | 5 | 3 | 2 | 30 | Rule enforced and tested | None |
| 8 | Wrong number format shown | Confusing percentages | Locale differences (D4) | 3 | 4 | 6 | 72 | `mainTest` fixes the locale | Use `Locale.US` in `main` |

## Priority order (highest RPN first)

1. Invalid dates (210)
2. Extra CSV columns (90)
3. Rounding / locale formatting (72)
4. Report save failures (63)
5. Missing validation in `expense` (56)

After an action is done, re-rate O and D and record the new RPN.
