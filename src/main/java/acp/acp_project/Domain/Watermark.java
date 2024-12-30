package acp.acp_project.Domain;

import com.ironsoftware.ironpdf.stamp.*;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.element.Image;


import com.itextpdf.io.image.ImageDataFactory;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;


public class Watermark {

    Response response = new Response();

    public Response addWaterMark(String pdfPath, String imagePath, String outputPath) {
        try {
            // Open existing PDF
            PdfReader pdfReader = new PdfReader(pdfPath);
            File inputFile = new File(pdfPath);
            String originalFileName = inputFile.getName(); // Get the filename with extension
            String fileBaseName = originalFileName.substring(0, originalFileName.lastIndexOf(".")); // Remove extension
            String outputFileName = fileBaseName + "-watermark.pdf";  // Specify the name of the file
            // Create the full output file path
            String fullOutputPath = outputPath + File.separator + outputFileName;

            PdfWriter pdfWriter = new PdfWriter(fullOutputPath);

            // Open the existing PDF and create a new one for output
            PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter);

            // Load the watermark image
            ImageData image = ImageDataFactory.create(imagePath);
            Image watermarkImage = new Image(image);
            watermarkImage.setOpacity(0.2f);

            // Get the dimensions of the first page to calculate center position
            Rectangle pageSize = pdfDocument.getFirstPage().getPageSize();
            float x = (pageSize.getWidth() - watermarkImage.getImageWidth()) / 2;
            float y = (pageSize.getHeight() - watermarkImage.getImageHeight()) / 2;

            // Create watermark template
            PdfFormXObject watermarkTemplate = new PdfFormXObject(new Rectangle(pageSize.getWidth(), pageSize.getHeight()));
            Canvas canvas = new Canvas(watermarkTemplate, pdfDocument);

            // Position the image in the center
            watermarkImage.setFixedPosition(x, y);
            canvas.add(watermarkImage);

            // Add text watermark if needed
            canvas.setFontColor(DeviceGray.GRAY)
                    .setFontSize(60)
                    .showTextAligned("WaTeRmArK",
                            pageSize.getWidth() / 2,
                            pageSize.getHeight() / 2,
                            TextAlignment.CENTER,
                            0);

            // Apply watermark to all pages
            int numberOfPages = pdfDocument.getNumberOfPages();
            for (int i = 1; i <= numberOfPages; i++) {
                PdfPage page = pdfDocument.getPage(i);
                PdfCanvas pdfCanvas = new PdfCanvas(page);
                pdfCanvas.addXObjectAt(watermarkTemplate, 0, 0);
            }

            // Close the document
            pdfDocument.close();

            response.Message = "Applied the Water Mark successfully to " + numberOfPages + " pages";

        } catch (Exception e) {
            response.Message = e.getMessage();
        }
        return response;
    }




}
