package com.automation.support.evidence;

import com.automation.support.evidence.EvidenceRecorder.EvidenceItem;
import com.automation.support.evidence.EvidenceRecorder.TestEvidence;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Locale;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Renders the evidence of one test as a PDF document: header table, failure details and numbered steps. */
final class EvidencePdfWriter {

    private static final Logger LOG = LoggerFactory.getLogger(EvidencePdfWriter.class);

    private static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_TEXT_LENGTH = 4000;
    private static final float MAX_IMAGE_WIDTH = 515;
    private static final float MAX_IMAGE_HEIGHT = 330;
    private static final float JPEG_QUALITY = 0.8f;

    private static final Color PASSED = new Color(0x2E7D32);
    private static final Color FAILED = new Color(0xC62828);
    private static final Color NEUTRAL = new Color(0x616161);
    private static final Color LABEL_BACKGROUND = new Color(0xEEEEEE);

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD);
    private static final Font SECTION_FONT = new Font(Font.HELVETICA, 13, Font.BOLD);
    private static final Font LABEL_FONT = new Font(Font.HELVETICA, 9, Font.BOLD);
    private static final Font VALUE_FONT = new Font(Font.HELVETICA, 9, Font.NORMAL);
    private static final Font STEP_FONT = new Font(Font.HELVETICA, 10, Font.BOLD);
    private static final Font CODE_FONT = new Font(Font.COURIER, 7, Font.NORMAL);

    private EvidencePdfWriter() {
    }

    static Optional<Path> write(TestEvidence evidence, String status, String failureMessage,
                                Duration duration, Path directory) {
        Path file = directory.resolve(fileName(evidence, status));
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try {
            Files.createDirectories(directory);
            try (OutputStream out = Files.newOutputStream(file)) {
                PdfWriter.getInstance(document, out);
                document.open();
                writeHeader(document, evidence, status, duration);
                if (failureMessage != null && !failureMessage.isBlank()) {
                    writeFailure(document, failureMessage);
                }
                writeItems(document, evidence);
                document.close();
            }
            return Optional.of(file);
        } catch (Exception e) {
            // Evidence must never change the outcome of a test.
            LOG.warn("Could not write evidence PDF {}: {}", file, e.getMessage());
            return Optional.empty();
        }
    }

    private static void writeHeader(Document document, TestEvidence evidence, String status, Duration duration) {
        Paragraph title = new Paragraph("Test Evidence", TITLE_FONT);
        title.setSpacingAfter(12);
        document.add(title);

        PdfPTable table = new PdfPTable(new float[] {1, 3});
        table.setWidthPercentage(100);
        addRow(table, "Test", evidence.testName(), VALUE_FONT);
        addRow(table, "Suite", evidence.suite(), VALUE_FONT);
        addRow(table, "Status", status.toUpperCase(),
                new Font(Font.HELVETICA, 9, Font.BOLD, statusColor(status)));
        addRow(table, "Started", DISPLAY_TIME.format(evidence.startedAt()), VALUE_FONT);
        addRow(table, "Duration", String.format(Locale.ROOT, "%.1f s", duration.toMillis() / 1000.0), VALUE_FONT);
        for (Map.Entry<String, String> entry : evidence.execution().entrySet()) {
            addRow(table, entry.getKey(), entry.getValue(), VALUE_FONT);
        }
        document.add(table);
    }

    private static void writeFailure(Document document, String failureMessage) {
        Paragraph section = new Paragraph("Failure", new Font(Font.HELVETICA, 13, Font.BOLD, FAILED));
        section.setSpacingBefore(14);
        section.setSpacingAfter(4);
        document.add(section);
        document.add(new Paragraph(truncate(failureMessage), CODE_FONT));
    }

    private static void writeItems(Document document, TestEvidence evidence) throws Exception {
        if (evidence.items().isEmpty()) {
            return;
        }
        Paragraph section = new Paragraph("Steps and evidence", SECTION_FONT);
        section.setSpacingBefore(14);
        document.add(section);

        int number = 1;
        for (EvidenceItem item : evidence.items()) {
            String title = number++ + ". " + item.title();
            if (item.png() != null) {
                document.add(screenshotBlock(title, item.png()));
            } else {
                Paragraph step = new Paragraph(title, STEP_FONT);
                step.setSpacingBefore(10);
                step.setSpacingAfter(4);
                document.add(step);
                document.add(new Paragraph(truncate(item.text()), CODE_FONT));
            }
        }
    }

    /** Title and screenshot in one unbreakable table, so a page break never separates them. */
    private static PdfPTable screenshotBlock(String title, byte[] png) throws Exception {
        PdfPTable block = new PdfPTable(1);
        block.setWidthPercentage(100);
        block.setKeepTogether(true);
        block.setSpacingBefore(10);

        PdfPCell titleCell = new PdfPCell(new Phrase(title, STEP_FONT));
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPaddingBottom(4);
        block.addCell(titleCell);

        Image screenshot = Image.getInstance(compress(png));
        screenshot.scaleToFit(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT);
        PdfPCell imageCell = new PdfPCell(screenshot, false);
        imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        imageCell.setBorderColor(NEUTRAL);
        imageCell.setBorderWidth(0.5f);
        block.addCell(imageCell);
        return block;
    }

    /** PNG screenshots are 5-10x larger than a good-quality JPEG; keep PDFs small for CI artifacts. */
    private static byte[] compress(byte[] png) {
        try {
            BufferedImage source = ImageIO.read(new ByteArrayInputStream(png));
            BufferedImage rgb = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = rgb.createGraphics();
            graphics.drawImage(source, 0, 0, Color.WHITE, null);
            graphics.dispose();

            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageWriteParam params = writer.getDefaultWriteParam();
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(JPEG_QUALITY);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (ImageOutputStream imageOut = ImageIO.createImageOutputStream(out)) {
                writer.setOutput(imageOut);
                writer.write(null, new IIOImage(rgb, null, null), params);
            } finally {
                writer.dispose();
            }
            return out.toByteArray();
        } catch (Exception e) {
            return png; // fall back to the original image
        }
    }

    private static void addRow(PdfPTable table, String label, String value, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, LABEL_FONT));
        labelCell.setBackgroundColor(LABEL_BACKGROUND);
        labelCell.setPadding(5);
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(5);
        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private static Color statusColor(String status) {
        return switch (status.toUpperCase()) {
            case "PASSED" -> PASSED;
            case "FAILED" -> FAILED;
            default -> NEUTRAL;
        };
    }

    private static String fileName(TestEvidence evidence, String status) {
        String name = sanitize(evidence.suite() + "_" + evidence.testName());
        return "%s_%s_%s.pdf".formatted(status.toUpperCase(), name, FILE_TIME.format(LocalDateTime.now()));
    }

    private static String sanitize(String value) {
        String ascii = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String safe = ascii.replaceAll("[^A-Za-z0-9-]+", "_").replaceAll("^_+|_+$", "");
        return safe.length() > MAX_NAME_LENGTH ? safe.substring(0, MAX_NAME_LENGTH) : safe;
    }

    private static String truncate(String text) {
        return text.length() > MAX_TEXT_LENGTH ? text.substring(0, MAX_TEXT_LENGTH) + "\n... (truncated)" : text;
    }
}
