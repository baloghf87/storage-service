package hu.bf.storageservice.rest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletContext;

public class RestApiTest {

    @Autowired
    private ServletContext servletContext;

    protected RestTemplate restTemplate = new RestTemplate();

    private String baseUrl;

    @Test
    public void shouldReturnNotFoundForInvalidKey(){

    }

    @Test
    public void should(){

    }

    @LocalServerPort
    private void createBaseUrl(int port) {
        String baseUrl = "http://localhost:" + port + servletContext.getContextPath();
    }


}
