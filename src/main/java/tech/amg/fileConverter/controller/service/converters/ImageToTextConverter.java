package tech.amg.fileConverter.controller.service.converters;


import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@Service
public class ImageToTextConverter {
    public String convertImageToText(byte[] imageBytes) throws IOException {
        ITesseract iTesseract = new Tesseract();
        iTesseract.setDatapath("/tessdata");
        iTesseract.setLanguage("eng");
        // Convert the byte array to a ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        // Create a temporary file to store the image bytes
        File tempFile = File.createTempFile("temp", ".png"); // Assuming the image is in PNG format
        try {
            // Write the byte array to the temporary file
            FileUtils.writeByteArrayToFile(tempFile, imageBytes);
        } catch (IOException e) {
            throw new IOException("Error writing image bytes to file", e);
        }
        // Use the temporary file with Tesseract
        final String parsedData;
        try {
            parsedData = iTesseract.doOCR(tempFile);
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
        // Optionally, delete the temporary file if it's no longer needed
        tempFile.delete();
        return parsedData;
    }
}
