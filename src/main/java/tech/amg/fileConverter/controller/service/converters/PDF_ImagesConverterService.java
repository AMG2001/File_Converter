package tech.amg.fileConverter.controller.service.converters;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
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
public class PDF_ImagesConverterService {
    public byte[] convertPdfToImages(byte[] pdfBytes) throws IOException {
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();
            List<BufferedImage> images = new ArrayList<>();
            for (int page = 0; page < pageCount; ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 80, ImageType.RGB);
                System.out.println("Page Number: " + (page + 1) + "Processed");
                images.add(bim);
                // Dispose of the image after adding it to the list
                bim.flush();
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


    public byte[] convertImagesToPdf(List<byte[]> imagesBytes) throws IOException {
        try (PDDocument document = new PDDocument()) {
            float pageWidth = PDRectangle.A4.getWidth();
            float pageHeight = PDRectangle.A4.getHeight();
            int imagesPerPage = 2;
            float spacing = 24; // Spacing between images

            for (int i = 0; i < imagesBytes.size(); i += imagesPerPage) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    // Calculate image size based on page size and spacing
                    float imageWidth = pageWidth - (2 * spacing);
                    float imageHeight = (pageHeight - ((imagesPerPage + 1) * spacing)) / imagesPerPage;

                    for (int j = 0; j < imagesPerPage && (i + j) < imagesBytes.size(); j++) {
                        byte[] imageBytes = imagesBytes.get(i + j);
                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageBytes, "image");

                        // Calculate position for the current image
                        float x = spacing;
                        float y = pageHeight - ((j + 1) * (imageHeight + spacing));

                        // Draw the image with the calculated position and scale
                        contentStream.drawImage(pdImage, x, y, imageWidth, imageHeight);
                    }
                }
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }
}
