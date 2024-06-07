package tech.amg.fileConverter.controller.rest;


import org.apache.pdfbox.tools.ImageToPDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.amg.fileConverter.controller.service.EmailService;
import tech.amg.fileConverter.controller.service.FileConverter;
import tech.amg.fileConverter.controller.service.converters.Converters;
import tech.amg.fileConverter.controller.service.converters.EPUB_PDF_Converter;
import tech.amg.fileConverter.controller.service.converters.ImageToTextConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class FileConverterEndPoint {

    @Autowired
    private ImageToTextConverter imageToTextConverter;

    @Autowired
    private EPUB_PDF_Converter epub_pdf_converter;


    @Value("${spring.mail.username}")
    private String email;

    @Autowired
    private final FileConverter fileConversionService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private Converters converters;

    public FileConverterEndPoint(FileConverter fileConversionService) {
        this.fileConversionService = fileConversionService;
    }


    @GetMapping("/")
    public ResponseEntity<Resource> index() throws MalformedURLException {
        Path filePath = Paths.get("src/main/resources/static/index.html").normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            return ResponseEntity.ok().body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getEmail")
    public String getEmail() {
        emailService.sendEmail("mohamadamgad09@gmail.com", "Logging", "");
        return "Email has been sent successfully , check your Inbox !!";
    }

    @PostMapping("/convertDocxToPdf")
    public ResponseEntity<byte[]> convertDocxToPdf(@RequestParam("file") MultipartFile docxFile, @RequestParam("filename") String filename) {
        byte[] pdfFile = null;
        try {
            pdfFile = fileConversionService.convertDocxToPdf(docxFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Change the extension of the original filename to .pdf
        String pdfFilename = filename.substring(0, filename.lastIndexOf('.')) + ".pdf";
        headers.setContentDispositionFormData("filename", pdfFilename);
        return new ResponseEntity<>(pdfFile, headers, HttpStatus.OK);
    }


    @PostMapping("/convertPdfToImages")
    public ResponseEntity<byte[]> convertPdfToImages(@RequestParam("file") MultipartFile pdfFile, @RequestParam("filename") String filename) {
        try {
            System.out.println("Convert pdf to images called");
            byte[] imagesZip = fileConversionService.convertPdfToImages(pdfFile.getBytes());
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + filename + ".zip")
                    .body(imagesZip);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/convertImagesToPdf")
    public ResponseEntity<byte[]> convertImagesToPdf(@RequestParam("files") List<MultipartFile> imageFiles) {
        try {
            System.out.println("Convert images to PDF called");
            // Convert each MultipartFile to a byte array
            List<byte[]> multipartFilesBytes = converters.convertMulyipartToBytes(imageFiles);
            // Call the service method with the list of byte arrays
            byte[] pdfBytes = fileConversionService.convertImagesToPdf(multipartFilesBytes);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=converted.pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    @PostMapping("/convertEPUBToPDF")
//    public ResponseEntity<byte[]> convertEPUBToPDF(@RequestParam("file") MultipartFile epubFile) {
//        try {
//            byte[] pdfOutput = epub_pdf_converter.convert(epubFile.getInputStream());
//            return ResponseEntity.ok(pdfOutput);
//        } catch (IOException e) {
//            // Log the exception for debugging
//            System.err.println("Error converting EPUB to PDF: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    @PostMapping(value = "/getTextFromImage")
    public ResponseEntity<String> getTextFromImage(@RequestParam("image") MultipartFile imageFile) {
        try {
            String imageTextContent = imageToTextConverter.convertImageToText(imageFile.getBytes());
            return ResponseEntity.ok(imageTextContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

