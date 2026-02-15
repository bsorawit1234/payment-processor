# Payment Transaction Processor

Processes `transactions.json` and produces a summary report with:
- Validation (with invalid reason breakdown)
- Duplicate detection (TXID and MERCHANT_AMOUNT_DAY rules)
- Summary totals and status counts
- SUCCESS amount stats (min/max/avg)

## How To Run

### Prerequisites
- Java 25
- Maven 3.9+

### Run tests
```bash
mvn test
```

### Run the app
Run `org.paymentprocessor.Application` with arguments:
```bash
--input transactions.json --output report.json
```

Current implementation resolves both files under the `data/` directory.
Example:
- input: `data/transactions.json`
- output: `data/report.json`

After run, report is written as pretty JSON.

### Summary
Report includes:
- `totalRecords`, `validRecords`, `invalidRecords`
- `invalidReasons` breakdown
- `statusCounts`
- `successAmountStats` (`min`, `max`, `avg`)
- `duplicateCount`
- `duplicateGroups`

## Assumptions
- Duplicate rules are independent, so the same records can appear in multiple duplicate groups.
- UTC day for Rule 2 is computed from `createdAtUtc` in UTC.
- `statusCounts.SUCCESS` counts each `transactionId` at most once.
- `successAmountStats` uses SUCCESS amounts counted once per `transactionId`.
- Average is rounded to 2 decimal places (`HALF_UP`).

## Trade-offs
- Prioritized correctness and readability over advanced abstractions.
- Validation parses raw JSON first to avoid enum/date parse failures stopping the entire file.
- Duplicate group keys are strings for simple output and easy debugging.
- Report includes full records inside duplicate groups, which can increase file size for large inputs.

## Tests
Test coverage includes:
- Validation failures
- Duplicate detection for both rules
- Summary calculations (including idempotent SUCCESS behavior)

## AI Tools Used
**Codex**
- Generate README.md
- Used for debugging support and generating/refactoring some code.
- Helped on:
- JSON read/write flow
- Unit test report scaffolding (test case ideas/logic created by me)
- `PaymentProcessor` (`toDuplicateGroupItems` method)
- `PaymentValidator` (`validateRecord` and small helper functions)
- `PaymentDuplicateDetector` (partial help and refactor)
- Refactoring types/files into cleaner separated directories

**Gemini**
- Used to ask how to set up a clean Java project structure.
