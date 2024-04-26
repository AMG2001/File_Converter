package tech.amg.fileConverter.controller.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.amg.fileConverter.controller.service.converters.Docx_PdfConverterService;
import tech.amg.fileConverter.controller.service.converters.PDF_ImagesConverterService;

import java.io.IOException;
import java.util.List;

@Service
public class FileConverter {

    @Autowired
    private PDF_ImagesConverterService pdf_imagesConverterService;

    @Autowired
    private Docx_PdfConverterService docx_pdfConverterService;

    // TODO there is an error !!
    public byte[] convertDocxToPdf(byte[] docxBytes) throws IOException {
        return docx_pdfConverterService.convertDocxToPdf(docxBytes);
    }

    public byte[] convertPdfToImages(byte[] pdfBytes) throws IOException {
        return pdf_imagesConverterService.convertPdfToImages(pdfBytes);
    }

    public byte[] convertImagesToPdf(List<byte[]> imagesBytes) throws IOException {
        return pdf_imagesConverterService.convertImagesToPdf(imagesBytes);
    }

}

