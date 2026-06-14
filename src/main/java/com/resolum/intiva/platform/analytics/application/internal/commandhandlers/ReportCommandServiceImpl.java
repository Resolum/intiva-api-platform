package com.resolum.intiva.platform.analytics.application.internal.commandhandlers;

import com.resolum.intiva.platform.analytics.application.internal.outboundservices.acl.AnalyticsExternalTransactionService;
import com.resolum.intiva.platform.analytics.domain.model.aggregates.AnalyticsReport;
import com.resolum.intiva.platform.analytics.domain.model.commands.GenerateReportCommand;
import com.resolum.intiva.platform.analytics.domain.model.exceptions.ReportGenerationException;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.ReportFilter;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.ReportFormat;
import com.resolum.intiva.platform.analytics.domain.services.ReportCommandService;
import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link ReportCommandService} interface that generates
 * downloadable report files in CSV and PDF formats.
 *
 * <p>This service fetches transaction data through the ACL layer, renders it into
 * the requested format, and returns an {@link AnalyticsReport} aggregate containing
 * the binary content and file metadata suitable for an HTTP download response.</p>
 *
 * <p>CSV generation uses Apache Commons CSV with configurable headers and a
 * summary footer showing total expenses, total income, and net balance. PDF
 * generation uses iText 8 with A4 page size, creating a structured document that
 * includes a summary section, a top categories table with progress bars, and a
 * full transaction detail table with color-coded transaction types.</p>
 */
@Slf4j
@Service
public class ReportCommandServiceImpl implements ReportCommandService {

    /**
     * Formatter used to produce the date segment of the output file name.
     */
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * ACL service used to access transaction data from the finances bounded context.
     */
    private final AnalyticsExternalTransactionService externalService;

    /**
     * Creates the report command service with its required ACL dependency.
     *
     * @param externalService ACL service for accessing transaction data
     */
    public ReportCommandServiceImpl(AnalyticsExternalTransactionService externalService) {
        this.externalService = externalService;
    }

    /**
     * Generates a downloadable report file based on the provided command parameters.
     *
     * <p>The generation process follows these steps:
     * <ol>
     *   <li>Transactions matching the owner and period are fetched via the ACL layer.</li>
     *   <li>If a {@code categoryId} is specified, the list is filtered accordingly.</li>
     *   <li>Based on the requested format, either {@link #generateCsv} or
     *       {@link #generatePdf} is invoked to produce the binary content.</li>
     *   <li>The file name is constructed using the pattern
     *       {@code report_{ownerType}_{ownerId}_{periodStart}_{periodEnd}.{ext}}.</li>
     * </ol></p>
     *
     * @param command the generation parameters (owner, period, optional category, format)
     * @return an {@link AnalyticsReport} containing the binary content and file metadata
     * @throws ReportGenerationException if an I/O error occurs during file generation
     */
    @Override
    public AnalyticsReport generateReport(GenerateReportCommand command) {
        log.info("Generating {} report for ownerId={}, ownerType={}, period=[{}, {}]",
                command.format(), command.ownerId(), command.ownerType(),
                command.periodStart(), command.periodEnd());

        var ownerId = Long.parseLong(command.ownerId());
        var transactions = externalService.getTransactionsByOwnerAndPeriod(
                ownerId, command.ownerType(), command.periodStart(), command.periodEnd());

        if (command.categoryId() != null && !command.categoryId().isBlank()) {
            var catIdLong = Long.parseLong(command.categoryId());
            transactions = transactions.stream()
                    .filter(tx -> tx.getCategoryId() != null && catIdLong == tx.getCategoryId().getValue())
                    .toList();
        }

        var filter = new ReportFilter(
                command.ownerId(), command.ownerType(),
                command.periodStart(), command.periodEnd(),
                command.categoryId(), command.format());

        var ext = command.format() == ReportFormat.CSV ? "csv" : "pdf";
        var fileName = String.format("report_%s_%s_%s_%s.%s",
                command.ownerType().name().toLowerCase(),
                command.ownerId(),
                command.periodStart().format(FILE_DATE_FORMATTER),
                command.periodEnd().format(FILE_DATE_FORMATTER),
                ext);

        byte[] content;
        if (command.format() == ReportFormat.CSV) {
            content = generateCsv(transactions, filter);
        } else {
            content = generatePdf(transactions, filter);
        }

        return new AnalyticsReport(filter, fileName, content);
    }

    /**
     * Generates a CSV byte array from the filtered transactions using Apache Commons CSV.
     *
     * <p>The CSV includes the following columns: Date, Description, Type, Amount,
     * Currency, Category. Transactions are sorted by creation date in descending
     * order. A summary footer with three rows is appended at the end: TOTAL
     * (expenses), TOTAL INCOME, and NET BALANCE.</p>
     *
     * @param transactions the list of transactions to include in the report
     * @param filter       the filter parameters (used for currency resolution)
     * @return the CSV content as a UTF-8 encoded byte array
     * @throws ReportGenerationException if an I/O error occurs during writing
     */
    private byte[] generateCsv(List<Transaction> transactions, ReportFilter filter) {
        var currency = resolveCurrency(transactions);
        var zero = new Money(BigDecimal.ZERO, currency);

        var totalIncome = transactions.stream()
                .filter(tx -> tx.getTransactionType() == TransactionTypes.INCOME)
                .map(Transaction::getAmount)
                .reduce(Money::add)
                .orElse(zero);

        var totalExpenses = transactions.stream()
                .filter(tx -> tx.getTransactionType() == TransactionTypes.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(Money::add)
                .orElse(zero);

        var netAmount = totalIncome.getAmount().subtract(totalExpenses.getAmount());

        var sortedTransactions = transactions.stream()
                .sorted(Comparator.comparing((Transaction tx) ->
                        tx.getCreatedAt() != null ? tx.getCreatedAt() : Instant.MIN).reversed())
                .toList();

        var out = new ByteArrayOutputStream();
        try (var writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             var printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader("Date", "Description", "Type", "Amount", "Currency", "Category")
                     .build())) {

            for (var tx : sortedTransactions) {
                var date = tx.getCreatedAt() != null
                        ? tx.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate().toString()
                        : "";
                var categoryName = getCategoryName(tx);
                printer.printRecord(
                        date,
                        tx.getDescription(),
                        tx.getTransactionType().name(),
                        tx.getAmount().getAmount(),
                        tx.getAmount().getCurrencyCode(),
                        categoryName);
            }

            printer.printRecord("TOTAL", "", "", totalExpenses.getAmount(), "", "");
            printer.printRecord("TOTAL INCOME", "", "", totalIncome.getAmount(), "", "");
            printer.printRecord("NET BALANCE", "", "", netAmount, "", "");

        } catch (IOException e) {
            log.error("Error generating CSV report", e);
            throw new ReportGenerationException("Error generating CSV report", e);
        }

        return out.toByteArray();
    }

    /**
     * Generates a PDF byte array from the filtered transactions using iText 8.
     *
     * <p>The PDF document is structured as follows:
     * <ul>
     *   <li><b>Header:</b> "Financial Report" title, period range, and owner identification.</li>
     *   <li><b>Summary table:</b> Total Income, Total Expenses, Net Balance, and Transaction Count.</li>
     *   <li><b>Top Categories table:</b> Category name, amount, percentage with a text-based progress bar.</li>
     *   <li><b>Transaction Detail table:</b> Date, Description, Type (color-coded: green for income,
     *       red for expense), Amount, and Category.</li>
     *   <li><b>Footer:</b> Generation timestamp.</li>
     * </ul></p>
     *
     * @param transactions the list of transactions to include in the report
     * @param filter       the filter parameters for header display and currency resolution
     * @return the PDF content as a byte array
     * @throws ReportGenerationException if an I/O error occurs during PDF creation
     */
    private byte[] generatePdf(List<Transaction> transactions, ReportFilter filter) {
        var currency = resolveCurrency(transactions);
        var zero = new Money(BigDecimal.ZERO, currency);

        var totalIncome = transactions.stream()
                .filter(tx -> tx.getTransactionType() == TransactionTypes.INCOME)
                .map(Transaction::getAmount)
                .reduce(Money::add)
                .orElse(zero);

        var totalExpenses = transactions.stream()
                .filter(tx -> tx.getTransactionType() == TransactionTypes.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(Money::add)
                .orElse(zero);

        var netAmount = totalIncome.getAmount().subtract(totalExpenses.getAmount());

        var expenseTotal = totalExpenses;

        var sortedTransactions = transactions.stream()
                .sorted(Comparator.comparing((Transaction tx) ->
                        tx.getCreatedAt() != null ? tx.getCreatedAt() : Instant.MIN).reversed())
                .toList();

        var out = new ByteArrayOutputStream();
        try {
            var pdfWriter = new com.itextpdf.kernel.pdf.PdfWriter(out);
            var pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(pdfWriter);
            var document = new com.itextpdf.layout.Document(pdfDoc, com.itextpdf.kernel.geom.PageSize.A4);
            document.setMargins(40, 40, 40, 40);

            var font = com.itextpdf.kernel.font.PdfFontFactory.createFont(
                    com.itextpdf.io.font.constants.StandardFonts.HELVETICA);
            var boldFont = com.itextpdf.kernel.font.PdfFontFactory.createFont(
                    com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);

            document.add(new com.itextpdf.layout.element.Paragraph("Financial Report")
                    .setFont(boldFont).setFontSize(18));

            var periodText = String.format("From %s to %s", filter.periodStart(), filter.periodEnd());
            document.add(new com.itextpdf.layout.element.Paragraph(periodText).setFont(font).setFontSize(11));

            var ownerText = String.format("%s: %s",
                    filter.ownerType().name(), filter.ownerId());
            document.add(new com.itextpdf.layout.element.Paragraph(ownerText).setFont(font).setFontSize(11));
            document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font).setFontSize(8));

            document.add(new com.itextpdf.layout.element.Paragraph("Summary")
                    .setFont(boldFont).setFontSize(14));
            document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font).setFontSize(6));

            var summaryTable = new com.itextpdf.layout.element.Table(
                    com.itextpdf.layout.properties.UnitValue.createPercentArray(new float[]{2, 1}));
            summaryTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

            addSummaryRow(summaryTable, "Total Income", totalIncome.getAmount(), boldFont, font);
            addSummaryRow(summaryTable, "Total Expenses", totalExpenses.getAmount(), boldFont, font);
            addSummaryRow(summaryTable, "Net Balance", netAmount, boldFont, font);
            addSummaryRow(summaryTable, "Transactions",
                    new BigDecimal(transactions.size()), boldFont, font);

            document.add(summaryTable);
            document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font).setFontSize(8));

            var categoriesSection = transactions.stream()
                    .filter(tx -> tx.getTransactionType() == TransactionTypes.EXPENSE)
                    .filter(tx -> tx.getCategoryId() != null)
                    .toList();

            if (!categoriesSection.isEmpty()) {
                document.add(new com.itextpdf.layout.element.Paragraph("Top Categories")
                        .setFont(boldFont).setFontSize(14));
                document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font).setFontSize(6));

                var catTable = new com.itextpdf.layout.element.Table(
                        com.itextpdf.layout.properties.UnitValue.createPercentArray(new float[]{3, 2, 2}));
                catTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

                addHeaderRow(catTable, boldFont, "Category", "Amount", "% of total");

                var groupedCategories = categoriesSection.stream()
                        .collect(Collectors.groupingBy(
                                tx -> tx.getCategoryId().getValue(),
                                Collectors.summingDouble(
                                        tx -> tx.getAmount().getAmount().doubleValue())));

                var sortedCategories = groupedCategories.entrySet().stream()
                        .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                        .limit(5)
                        .toList();

                for (var entry : sortedCategories) {
                    var catId = entry.getKey();
                    var catTotal = BigDecimal.valueOf(entry.getValue());
                    var catPercentage = expenseTotal.getAmount().compareTo(BigDecimal.ZERO) > 0
                            ? catTotal.divide(expenseTotal.getAmount(), 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100))
                                    .setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                    var categoryName = externalService.getCategoryNameById(catId);
                    var bar = createTextBar(catPercentage);

                    addDataRow(catTable, font, categoryName,
                            String.format("%.2f %s", catTotal, currency.name()),
                            String.format("%s %% %s", catPercentage, bar));
                }

                document.add(catTable);
                document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font).setFontSize(8));
            }

            document.add(new com.itextpdf.layout.element.Paragraph("Transaction Detail")
                    .setFont(boldFont).setFontSize(14));
            document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font).setFontSize(6));

            var detailTable = new com.itextpdf.layout.element.Table(
                    com.itextpdf.layout.properties.UnitValue.createPercentArray(new float[]{2, 3, 1, 2, 2}));
            detailTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

            addHeaderRow(detailTable, boldFont, "Date", "Description", "Type", "Amount", "Category");

            for (var tx : sortedTransactions) {
                var date = tx.getCreatedAt() != null
                        ? tx.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate().toString()
                        : "";
                var categoryName = getCategoryName(tx);
                var typeStr = tx.getTransactionType().name();
                var typeColor = tx.getTransactionType() == TransactionTypes.INCOME
                        ? new com.itextpdf.kernel.colors.DeviceRgb(0, 128, 0)
                        : new com.itextpdf.kernel.colors.DeviceRgb(200, 0, 0);

                addDataRow(detailTable, font, typeColor, date, tx.getDescription(), typeStr,
                        String.format("%.2f %s", tx.getAmount().getAmount(), tx.getAmount().getCurrencyCode()),
                        categoryName);
            }

            document.add(detailTable);
            document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font).setFontSize(8));

            var footer = String.format("Generated at %s", Instant.now());
            document.add(new com.itextpdf.layout.element.Paragraph(footer)
                    .setFont(font).setFontSize(8));

            document.close();
        } catch (IOException e) {
            log.error("Error generating PDF report", e);
            throw new ReportGenerationException("Error generating PDF report", e);
        }

        return out.toByteArray();
    }

    /**
     * Adds a row to the summary table with a label and its corresponding value.
     *
     * @param table    the PDF table to add the row to
     * @param label    the metric label (e.g. "Total Income")
     * @param value    the numeric value to display
     * @param boldFont the bold font for the label column
     * @param font     the regular font for the value column
     */
    private void addSummaryRow(com.itextpdf.layout.element.Table table, String label,
                                BigDecimal value, com.itextpdf.kernel.font.PdfFont boldFont,
                                com.itextpdf.kernel.font.PdfFont font) {
        table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new com.itextpdf.layout.element.Paragraph(label).setFont(boldFont).setFontSize(10)));
        table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new com.itextpdf.layout.element.Paragraph(
                        String.format("$ %.2f", value)).setFont(font).setFontSize(10)));
    }

    /**
     * Adds a header row to a PDF table with a light gray background.
     *
     * @param table    the PDF table to add the row to
     * @param boldFont the bold font for header text
     * @param headers  the header column values to display
     */
    private void addHeaderRow(com.itextpdf.layout.element.Table table,
                               com.itextpdf.kernel.font.PdfFont boldFont, String... headers) {
        for (var header : headers) {
            table.addCell(new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(header).setFont(boldFont).setFontSize(9))
                    .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(230, 230, 230)));
        }
    }

    /**
     * Adds a data row to a PDF table with regular font.
     *
     * @param table  the PDF table to add the row to
     * @param font   the font for the cell text
     * @param values the cell values to display
     */
    private void addDataRow(com.itextpdf.layout.element.Table table,
                             com.itextpdf.kernel.font.PdfFont font, String... values) {
        for (var value : values) {
            table.addCell(new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(value).setFont(font).setFontSize(8)));
        }
    }

    /**
     * Adds a data row to a PDF table with color-coded text for the type column.
     *
     * <p>The third column (index 2, the transaction type) is rendered in the
     * specified color — green for income, red for expense.</p>
     *
     * @param table    the PDF table to add the row to
     * @param font     the font for the cell text
     * @param typeColor the color to apply to the type column text
     * @param values   the cell values to display
     */
    private void addDataRow(com.itextpdf.layout.element.Table table,
                             com.itextpdf.kernel.font.PdfFont font,
                             com.itextpdf.kernel.colors.Color typeColor, String... values) {
        for (int i = 0; i < values.length; i++) {
            var cell = new com.itextpdf.layout.element.Cell()
                    .add(new com.itextpdf.layout.element.Paragraph(values[i]).setFont(font).setFontSize(8));
            if (i == 2) {
                cell.setFontColor(typeColor);
            }
            table.addCell(cell);
        }
    }

    /**
     * Creates a simple text-based progress bar representation.
     *
     * <p>The bar consists of 10 characters: filled characters ("|") for the
     * filled portion and dots (".") for the remaining portion. For example, a
     * percentage of 50% produces {@code |||||.....}.</p>
     *
     * @param percentage the percentage value (0–100)
     * @return a text bar string of length 10
     */
    private String createTextBar(BigDecimal percentage) {
        var barLength = 10;
        var filled = percentage.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(barLength)).intValue();
        var sb = new StringBuilder();
        for (int i = 0; i < barLength; i++) {
            sb.append(i < filled ? "|" : ".");
        }
        return sb.toString();
    }

    /**
     * Resolves the display name of the category associated with a transaction.
     *
     * <p>Returns an empty string if the transaction has no category. Falls back to
     * the raw category id if the name lookup fails.</p>
     *
     * @param tx the transaction whose category name to resolve
     * @return the category display name, or an empty string if no category is set
     */
    private String getCategoryName(Transaction tx) {
        if (tx.getCategoryId() == null) return "";
        try {
            return externalService.getCategoryNameById(tx.getCategoryId().getValue());
        } catch (Exception e) {
            log.warn("Could not fetch category name for id={}", tx.getCategoryId().getValue());
            return String.valueOf(tx.getCategoryId().getValue());
        }
    }

    /**
     * Resolves the currency code from the first available transaction, defaulting to
     * {@link CurrencyCodes#PEN} when the list is empty.
     *
     * @param transactions list of transactions to inspect
     * @return the currency code of the first transaction, or PEN if none exist
     */
    private CurrencyCodes resolveCurrency(List<Transaction> transactions) {
        return transactions.stream()
                .findFirst()
                .map(tx -> tx.getAmount().currencyCode())
                .orElse(CurrencyCodes.PEN);
    }
}
