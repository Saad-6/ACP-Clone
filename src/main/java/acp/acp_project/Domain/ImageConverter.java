package acp.acp_project.Domain;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageConverter {
    public Response convertImage(String inputPath, String outputPath, String outputFormat) {
        Response response = new Response();
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
}