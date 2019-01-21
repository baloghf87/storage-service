package hu.bf.storageservice.storage.key.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public abstract class KeyCreatorServiceIntegrationTest {

    @Autowired
    private KeyCreatorService keyCreatorService;

    @Test
    public void shouldCreateKeys() {
        String key1 = keyCreatorService.createKey(1);
        assertNotNull(key1);

        String key1repeated = keyCreatorService.createKey(1);
        assertEquals(key1, key1repeated);

        String key2 = keyCreatorService.createKey(2);
        assertNotNull(key2);
        assertNotEquals(key1, key2);
    }

}