package acp.acp_project.Domain;

import com.aspose.pdf.*;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PDFHelper {

    Response response = new Response();
Watermark watermark = new Watermark();
    public Response mergePDFs(List<File> pdfFiles, String outputPath) {
        try {
            // Check for evaluation mode limitations
            if (pdfFiles.size() > 4) {
                response.success = false;
                response.Message = "In evaluation mode, you can only merge up to 4 PDF files at once.";
                return response;
            }

            // Create output directory if it doesn't exist
            Path outputDir = Paths.get(outputPath).getParent();
            if (outputDir != null && !Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            // Initialize merger with first PDF
            Document outputDoc = new Document(pdfFiles.get(0).getAbsolutePath());

            // Add remaining PDFs (up to 3 more in evaluation mode)
            for (int i = 1; i < pdfFiles.size(); i++) {
                Document doc = new Document(pdfFiles.get(i).getAbsolutePath());
                outputDoc.getPages().add(doc.getPages());
                doc.close();
            }

            outputDoc.save(outputPath);
            outputDoc.close();

            response.success = true;
            response.Message = "PDFs merged successfully to: " + outputPath;
        } catch (Exception e) {
            response.success = false;
            response.Message = e.getMessage();
        }
        return response;
    }

    public Response addWatermarkToPDF(File pdfFile, String imagePath, String outputPath) {
        try {

            response = watermark.addWaterMark(pdfFile.getAbsolutePath(),imagePath,outputPath);


        } catch (Exception e) {
            response.success = false;
            response.Message = "Error adding watermark: " + e.getMessage();
        }
        return response;
    }

    public Response convertPDFsToWord(List<File> pdfFiles, String outputDirectory) {
        try {
            // Create output directory if it doesn't exist
            Path outputDir = Paths.get(outputDirectory);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            for (File pdfFile : pdfFiles) {
                Document doc = new Document(pdfFile.getAbsolutePath());
                String outputPath = outputDirectory + File.separator +
                        pdfFile.getName().replace(".pdf", ".docx");

                doc.save(outputPath, SaveFormat.DocX);
                doc.close();

                response.success = true;
                response.Message = "Converted " + pdfFile.getName() + " to Word document: " + outputPath;
            }
        } catch (Exception e) {
            response.success = false;
            response.Message = "Error converting to Word: " + e.getMessage();
        }
        return response;
    }

    public Response convertPDFsToText(List<File> pdfFiles, String outputDirectory) {
        try {
            // Create output directory if it doesn't exist
            Path outputDir = Paths.get(outputDirectory);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            for (File pdfFile : pdfFiles) {
                // Load the PDF document
                Document doc = new Document(pdfFile.getAbsolutePath());

                // Configure text extraction
                TextAbsorber textAbsorber = new TextAbsorber();
                TextExtractionOptions options = new TextExtractionOptions(0);
                textAbsorber.setExtractionOptions(options);

                // Extract text from all pages
                doc.getPages().accept(textAbsorber);
                String extractedText = textAbsorber.getText();

                // Create output file path
                String outputPath = outputDirectory + File.separator +
                        pdfFile.getName().replace(".pdf", ".txt");

                // Write text to file with UTF-8 encoding
                Files.write(Paths.get(outputPath), extractedText.getBytes("UTF-8"));

                doc.close();

                if (extractedText.trim().isEmpty()) {
                    response.success = false;
                    response.Message = "Warning: Extracted text is empty for " + pdfFile.getName();
                    return response;
                }

                response.success = true;
                response.Message = "Converted " + pdfFile.getName() + " to text file: " + outputPath;
            }
        } catch (Exception e) {
            response.success = false;
            response.Message = "Error converting to text: " + e.getMessage();
        }
        return response;
    }

    public Response printPDFs(List<File> files) {
        try {
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            if (printerJob.printDialog()) {
                for (File file : files) {
                    if (file.getName().endsWith(".pdf")) {
                        Document document = new Document(file.getAbsolutePath());
                        //document.print();
                        document.close();
                    }
                }
                response.success = true;
                response.Message = "PDFs printed successfully";
            } else {
                response.success = false;
                response.Message = "Print operation was cancelled";
            }
        } catch (Exception e) {
            response.success = false;
            response.Message = "Error printing PDFs: " + e.getMessage();
        }
        return response;
    }
}

