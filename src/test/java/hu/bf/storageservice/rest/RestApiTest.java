package hu.bf.storageservice.rest;

import hu.bf.storageservice.Util;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletContext;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@PropertySource("classpath:application.properties")
public class RestApiTest {

    @Autowired
    private ServletContext servletContext;

    @Value("${storage.filesystem.path}")
    private String directory;

    private RestTemplate restTemplate = new RestTemplate();

    private String baseUrl;

    @LocalServerPort
    private void createBaseUrl(int port) {
        baseUrl = "http://localhost:" + port + servletContext.getContextPath();
    }

    @Before
    public void initializeStorageDirectory() throws IOException {
        Util.deleteDirectory(directory);
        Util.createDirectory(directory);
    }

    @After
    public void removeStorageDirectory() throws IOException {
        Util.deleteDirectory(directory);
    }

    @Test
    public void shouldUploadAndDownloadFiles() {
        //given
        byte[] data1 = Util.getRandomBytes(1024 * 1024 * 10);
        String name1 = "file1.bin";
        String type1 = "application/octet-stream";

        byte[] data2 = Util.getRandomBytes(1024 * 1024 * 10);
        String name2 = "file2.bin";
        String type2 = "application/x-binary";

        //when
        String path1 = uploadFile(data1, name1, type1);
        String path2 = uploadFile(data2, name2, type2);

        //then
        verifyDownloadedData(path1, data1, name1, type1);
        verifyDownloadedData(path2, data2, name2, type2);
        downloadingNotExistingFileShouldReturnNotFound();
    }

    private void downloadingNotExistingFileShouldReturnNotFound() {
        try {
            assertEquals(HttpStatus.NOT_FOUND, download("no-key/no-file").getStatusCode());
            fail("Should have been failed");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpClientErrorException.NotFound.class, e.getClass());
            //OK
        }
    }

    private void verifyDownloadedData(String path, byte[] expectedData, String expectedFilename, String expectedContentType) {
        ResponseEntity<byte[]> responseEntity = download(path);
        assertTrue(HttpStatus.OK.is2xxSuccessful());
        assertEquals(expectedFilename, responseEntity.getHeaders().getContentDisposition().getFilename());
        assertEquals(MediaType.valueOf(expectedContentType), responseEntity.getHeaders().getContentType());
        assertArrayEquals(expectedData, responseEntity.getBody());
    }

    private ResponseEntity<byte[]> download(String path) {
        return restTemplate.getForEntity(baseUrl + "/" + path, byte[].class);
    }

    private String uploadFile(byte[] data, String name, String type) {
        ByteArrayResource resource = new ByteArrayResource(data) {
            @Override
            public String getFilename() {
                return name;
            }
        };

        HttpHeaders dataHeaders = new HttpHeaders();
        dataHeaders.setContentType(MediaType.valueOf(type));

        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        multipartRequest.add("file", new HttpEntity<>(resource, dataHeaders));

        HttpHeaders multipartHeaders = new HttpHeaders();
        multipartHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest, multipartHeaders);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.PUT, requestEntity, String.class);
        return response.getBody();
    }

}
