package acp.acp_project.Domain;
// Java code for watermarking an image

// For setting color of the watermark text
import java.awt.Color;

// For setting font of the watermark text
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageWatermark {

    Response response = new Response();

    public Response addWatermarkToImage(String inputPath, String watermarkPath, String outputPath) {
        BufferedImage img = null;
        File inputFile = null;
        File outputFile = null;

        try {
            // Read the input image
            inputFile = new File(inputPath);
            img = ImageIO.read(inputFile);

            if (img == null) {
                response.success = false;
                response.Message = "Failed to read the image file: " + inputPath;
                return response;
            }

            // Create a new BufferedImage with the same dimensions
            BufferedImage temp = new BufferedImage(
                    img.getWidth(), img.getHeight(),
                    BufferedImage.TYPE_INT_RGB);

            // Create graphics context and add the original image
            Graphics graphics = temp.getGraphics();
            graphics.drawImage(img, 0, 0, null);

            // Set the watermark properties
            graphics.setFont(new Font("Arial", Font.BOLD, 80));
            graphics.setColor(new Color(255, 0, 0, 40)); // Red with transparency

            // Add the watermark text
            String watermark = "WaterMark";
            graphics.drawString(watermark, img.getWidth() / 5, img.getHeight() / 3);

            // Release graphics resources
            graphics.dispose();

            // Ensure the output file has the correct extension
            String extension = "png"; // Use "jpeg" or "jpg" if needed
            if (!outputPath.endsWith("." + extension)) {
                outputPath += "." + extension;
            }

            outputFile = new File(outputPath);

            // Write the image to the output file
            ImageIO.write(temp, extension, outputFile);

            // Set success response
            response.success = true;
            response.Message = "Watermark added successfully to: " + outputPath;
        } catch (IOException e) {
            response.success = false;
            response.Message = "Error processing the image: " + e.getMessage();
        }

        return response;
    }

}
