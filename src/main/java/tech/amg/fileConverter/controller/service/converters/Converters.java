package tech.amg.fileConverter.controller.service.converters;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class Converters {

    public List<byte[]> convertMulyipartToBytes(List<MultipartFile> multipartFiles) {
        List<byte[]> imagesBytes = new ArrayList<>();
        for (MultipartFile imageFile : multipartFiles) {
            try {
                imagesBytes.add(imageFile.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return imagesBytes;
    }

}
