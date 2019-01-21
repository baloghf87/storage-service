package hu.bf.storageservice.rest;

import hu.bf.storageservice.storage.data.exception.DataKeyIsNotUniqueException;
import hu.bf.storageservice.storage.file.entity.IncomingFile;
import hu.bf.storageservice.storage.file.entity.StoredFile;
import hu.bf.storageservice.storage.file.service.FileStorageService;
import hu.bf.storageservice.storage.file.service.InvalidFileException;
import hu.bf.storageservice.storage.metadata.entity.FileMetaData;
import hu.bf.storageservice.storage.metadata.entity.StoredFileMetadata;
import hu.bf.storageservice.storage.metadata.exception.MetaDataKeyIsNotUniqueException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource("classpath:application.properties")
@EntityScan(basePackageClasses = StoredFileMetadata.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ContextConfiguration(classes = {RestApiController.class, RestApiControllerTest.MockConfiguration.class})
public class RestApiControllerTest {

    @Autowired
    private RestApiController restApiController;

    @Autowired
    private FileStorageService fileStorageService;

    @Configuration
    public static class MockConfiguration {
        @Bean
        public FileStorageService fileStorageService() {
            return Mockito.mock(FileStorageService.class);
        }
    }

    @Test
    public void downloadShouldReturnNotFoundWhenFileIsNotFound() throws IOException {
        //given
        String key = "key";
        Mockito.when(fileStorageService.get(Mockito.eq(key))).thenReturn(null);

        //when
        ResponseEntity response = restApiController.download(key, "any");

        //then
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
    }

    @Test
    public void downloadShouldReturnInternalServerErrorOnIoException() throws IOException {
        //given
        String key = "key";
        Mockito.when(fileStorageService.get(Mockito.eq(key))).thenThrow(IOException.class);

        //when
        ResponseEntity response = restApiController.download(key, "any");

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
    }

    @Test
    public void downloadShouldReturnFile() throws IOException {
        //given
        String key = "key";
        byte[] data = "test data".getBytes();
        StoredFileMetadata metadata = new StoredFileMetadata("name", "application/octet-stream", key, 1l);
        StoredFile storedFile = new StoredFile(new ByteArrayInputStream(data), metadata);

        Mockito.when(fileStorageService.get(Mockito.eq(key))).thenReturn(storedFile);

        //when
        ResponseEntity<byte[]> response = restApiController.download(key, metadata.getName());

        //then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(metadata.getName(), response.getHeaders().getContentDisposition().getFilename());
        assertEquals(MediaType.valueOf(metadata.getType()), response.getHeaders().getContentType());
        assertArrayEquals(data, response.getBody());
    }

    @Test
    public void uploadShouldReturnBadRequestWhenFilenameIsIllegal() {
        //given
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockFile.getOriginalFilename()).thenReturn("../../../../../etc/passwd");

        //when
        ResponseEntity response = restApiController.upload(mockFile);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void uploadShouldStoreFile() throws IOException, MetaDataKeyIsNotUniqueException, DataKeyIsNotUniqueException, InvalidFileException {
        //given
        byte[] data = "test data".getBytes();
        ByteArrayInputStream dataInputStream = new ByteArrayInputStream(data);
        String filename = "test-data.bin";
        String contentType = "application/octet-stream";
        String key = "key";
        String expectedPath = key + "/" + filename;

        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockFile.getOriginalFilename()).thenReturn(filename);
        Mockito.when(mockFile.getContentType()).thenReturn(contentType);
        Mockito.when(mockFile.getInputStream()).thenReturn(dataInputStream);

        IncomingFile expectedIncomingFile = new IncomingFile(dataInputStream, new FileMetaData(filename, contentType));

        Mockito.when(fileStorageService.store(Mockito.eq(expectedIncomingFile))).thenReturn(key);

        //when
        ResponseEntity response = restApiController.upload(mockFile);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPath, response.getBody());
        Mockito.verify(fileStorageService).store(expectedIncomingFile);
    }

    @Test
    public void uploadShouldReturnInternalServerErrorOnException() throws MetaDataKeyIsNotUniqueException, DataKeyIsNotUniqueException, InvalidFileException, IOException {
        //given
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockFile.getOriginalFilename()).thenReturn("test");
        Mockito.when(fileStorageService.store(Mockito.any())).thenThrow(IOException.class);

        //when
        ResponseEntity response = restApiController.upload(mockFile);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}