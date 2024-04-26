package tech.amg.fileConverter.controller.service.converters;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class Docx_PdfConverterService {
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

}
