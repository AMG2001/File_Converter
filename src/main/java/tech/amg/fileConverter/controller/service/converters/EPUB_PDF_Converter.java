package tech.amg.fileConverter.controller.service.converters;


import io.documentnode.epub4j.domain.Book;
import io.documentnode.epub4j.domain.Resource;
import io.documentnode.epub4j.epub.EpubReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class EPUB_PDF_Converter {
    public byte[] convert(InputStream epubInputStream) throws IOException {
        // Initialize EpubReader
        EpubReader epubReader = new EpubReader();

        // Read EPUB file
        Book epubBook = epubReader.readEpub(epubInputStream);

        // Initialize PDF document
        PDDocument document = new PDDocument();
        PDFTextStripper pdfStripper = new PDFTextStripper();

        // Process each resource in the EPUB book
        List<String> textContent = new ArrayList<>();
        for (Resource resource : epubBook.getContents()) {
            InputStream resourceStream = resource.getInputStream();
            StringWriter writer = new StringWriter();
            pdfStripper.writeText(document, writer);
            String content = writer.toString();
            textContent.add(content);
        }

        // Save the PDF document to a ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();

        // Return the PDF content as a byte array
        return baos.toByteArray();
    }
}