package hu.bf.storageservice.service;

import hu.bf.storageservice.entity.FileMetaData;
import hu.bf.storageservice.entity.IncomingFile;
import hu.bf.storageservice.entity.StoredFile;
import hu.bf.storageservice.entity.StoredFileMetadata;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.*;

public abstract class StorageServiceIntegrationTest {

    @Autowired
    private StorageService storageService;

    @Test
    public void shouldStoreAndGet() throws IOException {
        //given
        byte[] data = getRandomBytes(1024 * 1024 * 10);
        String fileName = "test-file.bin";
        String fileType = "application/octet-stream";

        FileMetaData metaData = new FileMetaData(fileName, fileType);
        IncomingFile incomingFile = new IncomingFile(new ByteArrayInputStream(data), metaData);

        //when
        String key = storageService.store(incomingFile);

        //then
        StoredFile storedFile = storageService.get(key);
        assertNotNull(storedFile);

        StoredFileMetadata expectedMetadata = new StoredFileMetadata(metaData, key);
        assertEquals(expectedMetadata, storedFile.getMetaData());

        byte[] actualData = IOUtils.toByteArray(storedFile.getData());
        assertArrayEquals(data, actualData);
    }

    private byte[] getRandomBytes(int size) {
        byte[] bytes = new byte[size];
        new Random().nextBytes(bytes);
        return bytes;
    }

}