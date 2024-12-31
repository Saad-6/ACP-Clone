package acp.acp_project.Domain;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrintQuality;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageConverter {
        Response response = new Response();
    public Response convertImage(String inputPath, String outputPath, String outputFormat) {
        try {
            File inputFile = new File(inputPath);
            BufferedImage inputImage = ImageIO.read(inputFile);

            if (inputImage == null) {
                response.success = false;
                response.Message = "Failed to read input image: " + inputPath;
                return response;
            }

            BufferedImage outputImage;
            if (outputFormat.equalsIgnoreCase("png")) {
                outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            } else {
                outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            }

            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, null);
            g2d.dispose();

            File outputFile = new File(outputPath);
            boolean success = ImageIO.write(outputImage, outputFormat, outputFile);

            if (success) {
                response.success = true;
                response.Message = "Image converted successfully to " + outputFormat.toUpperCase() + ": " + outputPath;
            } else {
                response.success = false;
                response.Message = "Failed to write output image: " + outputPath;
            }
        } catch (IOException e) {
            response.success = false;
            response.Message = "Error converting image: " + e.getMessage();
        }

        return response;
    }

    public Response addWatermarkToImage(String inputPath, String watermarkPath, String outputPath) {
        Response response = new Response();
        try {
            File inputFile = new File(inputPath);
            BufferedImage originalImage = ImageIO.read(inputFile);
            if (originalImage == null) {
                response.success = false;
                response.Message = "Failed to read input image: " + inputPath;
                return response;
            }

            BufferedImage watermarkImage = ImageIO.read(new File(watermarkPath));
            if (watermarkImage == null) {
                response.success = false;
                response.Message = "Failed to read watermark image: " + watermarkPath;
                return response;
            }

            // Create a new image with the size of the original image
            BufferedImage watermarkedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

            // Draw the original image
            Graphics2D g2d = watermarkedImage.createGraphics();
            g2d.drawImage(originalImage, 0, 0, null);

            // Calculate the position to center the watermark
            int x = (originalImage.getWidth() - watermarkImage.getWidth()) / 2;
            int y = (originalImage.getHeight() - watermarkImage.getHeight()) / 2;

            // Draw the watermark image
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2d.drawImage(watermarkImage, x, y, null);

            g2d.dispose();

            // Determine the output format
            String inputFileName = inputFile.getName();
            String inputExtension = inputFileName.substring(inputFileName.lastIndexOf(".") + 1).toLowerCase();
            String outputFileName = "watermarked_" + inputFileName;
            File outputFile = new File(outputPath, outputFileName);

            // Ensure the output directory exists
            outputFile.getParentFile().mkdirs();

            System.out.println("Attempting to write image to: " + outputFile.getAbsolutePath());
            System.out.println("Using format: " + inputExtension);
            boolean writeSuccess = ImageIO.write(watermarkedImage, inputExtension, outputFile);

            if (writeSuccess) {
                response.success = true;
                response.Message = "Watermark added successfully: " + outputFile.getAbsolutePath();
                System.out.println("Write operation reported success");
            } else {
                response.success = false;
                response.Message = "Failed to write watermarked image: " + outputFile.getAbsolutePath();
                System.out.println("Write operation reported failure");
            }

// Add this to check if the file was actually created
            if (outputFile.exists()) {
                System.out.println("Output file exists on disk");
            } else {
                System.out.println("Output file does not exist on disk");
            }
        } catch (IOException e) {
            response.success = false;
            response.Message = "Error adding watermark: " + e.getMessage();
        }
        return response;
    }

    public Response printImage(String imagePath, String printQuality) {
        try {
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            pras.add(new Copies(1));

            // Set print quality based on the parameter
            if (printQuality.equalsIgnoreCase("Draft")) {
                pras.add(PrintQuality.DRAFT);
            } else if (printQuality.equalsIgnoreCase("High")) {
                pras.add(PrintQuality.HIGH);
            } else {
                pras.add(PrintQuality.NORMAL);
            }

            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
            PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();

            if (defaultService == null) {
                response.success = false;
                response.Message = "No default printer found";
                return response;
            }

            DocFlavor flavor = DocFlavor.INPUT_STREAM.PNG;
            DocPrintJob job = defaultService.createPrintJob();
            FileInputStream fis = new FileInputStream(imagePath);
            Doc doc = new SimpleDoc(fis, flavor, null);
            job.print(doc, pras);
            fis.close();

            response.success = true;
            response.Message = "Image sent to printer successfully";
        } catch (Exception e) {
            response.success = false;
            response.Message = "Error printing image: " + e.getMessage();
        }
        return response;
    }

}