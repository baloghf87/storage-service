package hu.bf.storageservice.storage.data.service;

import hu.bf.storageservice.Util;
import hu.bf.storageservice.storage.data.exception.DataKeyIsNotUniqueException;
import hu.bf.storageservice.storage.data.service.DataStorageService;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public abstract class DataStorageServiceIntegrationTest {

    @Autowired
    private DataStorageService dataStorageService;

    @Test
    public void shouldStoreAndGet() throws IOException, DataKeyIsNotUniqueException {
        //given
        String key1 = "test-key-123";
        assertNull(dataStorageService.get(key1));
        byte[] data1 = Util.getRandomBytes(1024 * 1024 * 10);

        String key2 = "test-key-234";
        assertNull(dataStorageService.get(key2));
        byte[] data2 = Util.getRandomBytes(1024 * 1024 * 10);

        //when
        dataStorageService.store(key1, new ByteArrayInputStream(data1));
        dataStorageService.store(key2, new ByteArrayInputStream(data2));

        //then
        InputStream inputStream1 = dataStorageService.get(key1);
        assertNotNull(inputStream1);
        byte[] actualData1 = IOUtils.toByteArray(inputStream1);
        assertArrayEquals(data1, actualData1);

        InputStream inputStream2 = dataStorageService.get(key2);
        assertNotNull(inputStream2);
        byte[] actualData2 = IOUtils.toByteArray(inputStream2);
        assertArrayEquals(data2, actualData2);
    }

    @Test
    public void shouldThrowExceptionWhenKeyIsNotUnique() throws DataKeyIsNotUniqueException, IOException {
        //given
        String key = "test-key-1";
        assertNull(dataStorageService.get(key));

        byte[] data = Util.getRandomBytes(1024);
        dataStorageService.store(key, new ByteArrayInputStream(data));

        //when
        try {
            dataStorageService.store(key, new ByteArrayInputStream(data));
            fail("It should have been failed");
        } catch (DataKeyIsNotUniqueException e) {
            //then it should throw exception
        }
    }

}