package tech.amg.fileConverter.controller.rest;


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

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileConverterEndPoint {

    @Value("${spring.mail.username}")
    private String email;

    @Autowired
    private final FileConverter fileConversionService;
    @Autowired
    private EmailService emailService;

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


    @CrossOrigin(origins = "http://localhost:63342")
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
}
