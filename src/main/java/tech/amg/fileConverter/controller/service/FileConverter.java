package tech.amg.fileConverter.controller.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileConverter {

    public byte[] convertDocxToPdf(byte[] docxFile) {
        try {
            // Load the .docx file with Docx4j
            WordprocessingMLPackage wordMLPackage = Docx4J.load(new ByteArrayInputStream(docxFile));
            // Prepare to convert to PDF
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Convert .docx to PDF
            Docx4J.toPDF(wordMLPackage, out);
            // Return the resulting PDF as byte array
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] convertPdfToImages(byte[] pdfBytes) throws IOException {
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();
            List<BufferedImage> images = new ArrayList<>();

            for (int page = 0; page < pageCount; ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                images.add(bim);
            }

            // Convert images to byte array and zip them
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);

            for (int i = 0; i < images.size(); i++) {
                BufferedImage image = images.get(i);
                ByteArrayOutputStream imageBaos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", imageBaos);
                byte[] imageBytes = imageBaos.toByteArray();

                ZipEntry zipEntry = new ZipEntry(String.format("image_%03d.jpg", i + 1));
                zos.putNextEntry(zipEntry);
                zos.write(imageBytes);
                zos.closeEntry();
            }

            zos.close();
            return baos.toByteArray();
        }
    }


}

