package hu.bf.storageservice.rest;

import hu.bf.storageservice.storage.file.entity.IncomingFile;
import hu.bf.storageservice.storage.file.entity.StoredFile;
import hu.bf.storageservice.storage.file.service.FileStorageService;
import hu.bf.storageservice.storage.metadata.entity.FileMetaData;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class RestApiController {

    private static final Logger LOG = LoggerFactory.getLogger(RestApiController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/{key}/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable("key") String key, @PathVariable("filename") String filename) {
        LOG.info("Request to download file with key '{}'", key);

        try {
            StoredFile storedFile = fileStorageService.get(key);
            if (storedFile == null) {
                LOG.info("File with key '{}' not found", key);
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            if (!storedFile.getMetaData().getName().equals(filename)) {
                LOG.info("File with key '{}' is not called '{}'", key, filename);
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            LOG.info("File with key '{}' and name '{}' found, sending response", key, filename);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.valueOf(storedFile.getMetaData().getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.builder("inline")
                            .filename(storedFile.getMetaData().getName())
                            .build()
                            .toString())
                    .body(IOUtils.toByteArray(storedFile.getData()));
        } catch (IOException e) {
            LOG.error("An error happened during serving file {}", e);
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity upload(@RequestParam("file") MultipartFile file) {
        LOG.info("Receiving file '{}'", file.getOriginalFilename());
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (fileName.contains("..")) {
            LOG.info("Filename '{}' is illegal", fileName);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        try {
            LOG.info("Storing file '{}'", fileName);
            String key = fileStorageService.store(new IncomingFile(file.getInputStream(), new FileMetaData(fileName, file.getContentType())));
            LOG.info("Successfully stored file '{}'", fileName);
            String path = key + "/" + fileName;
            return new ResponseEntity(path, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("An error happened during receiving file '{}': {}", fileName, e);
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
