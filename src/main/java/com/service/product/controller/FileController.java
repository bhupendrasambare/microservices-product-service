/**
 * author @bhupendrasambare
 * Date   :01/09/24
 * Time   :3:21 am
 * Project:product service
 **/
package com.service.product.controller;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/product/files")
public class FileController {

    private static final String UPLOAD_DIR = "/external-images";

    @Value("${custom.server-ip}")
    private String SERVER_IP;

    @Value("${custom.local-storage}")
    private String LOCAL_STORAGE;

    @Value("${server.port}")
    private String SERVER_PORT;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@ModelAttribute  MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        try {
            String storageLocation = "";
            if(SERVER_IP.equalsIgnoreCase("localhost")){
                storageLocation = LOCAL_STORAGE;
            }else{
                storageLocation = UPLOAD_DIR;
            }
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.lastIndexOf(".") == -1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file format");
            }

            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

            // Generate the new file name
            String newFileName = generateNewFileName(fileExtension);

            // Define the path where the file will be saved
            Path filePath = Paths.get(storageLocation, newFileName);

            // Write the file to the target location
            Files.write(filePath, file.getBytes());

            return ResponseEntity.ok("http://"+SERVER_IP+":"+SERVER_PORT+"/product/files/retrieve/"+newFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not upload the file: " + e.getMessage());
        }
    }

    private String generateNewFileName(String fileExtension) {
        // Generate the timestamp in format YYYYMMDDhhmmss
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // Generate a random 3-digit number
        int randomNumber = new Random().nextInt(900) + 100; // ensures a 3-digit number

        // Concatenate to form the new file name
        return "ms_" + timestamp + randomNumber + fileExtension;
    }

    @GetMapping("/retrieve/{filename}")
    public ResponseEntity<Resource> retrieveFile(@PathVariable String filename) {
        try {
            String StorageLocation = "";
            if(SERVER_IP.equalsIgnoreCase("localhost")){
                StorageLocation = LOCAL_STORAGE;
            }else{
                StorageLocation = UPLOAD_DIR;
            }
            Path filePath = Paths.get(StorageLocation, filename);
            File file = new File(filePath.toString());

            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Load the file as a resource
            Resource resource = new FileSystemResource(file);

            // Determine content type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Fallback to generic binary type if type is unknown
            }

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDispositionFormData("attachment", filename);

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
