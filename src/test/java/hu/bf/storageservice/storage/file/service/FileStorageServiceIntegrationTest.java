package hu.bf.storageservice.storage.file.service;

import hu.bf.storageservice.Util;
import hu.bf.storageservice.storage.data.exception.DataKeyIsNotUniqueException;
import hu.bf.storageservice.storage.file.entity.IncomingFile;
import hu.bf.storageservice.storage.file.entity.StoredFile;
import hu.bf.storageservice.storage.metadata.entity.FileMetaData;
import hu.bf.storageservice.storage.metadata.entity.StoredFileMetadata;
import hu.bf.storageservice.storage.metadata.exception.MetaDataKeyIsNotUniqueException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public abstract class FileStorageServiceIntegrationTest {

    @Autowired
    private FileStorageService storageService;

    @Test
    public void shouldStoreAndGet() throws IOException, MetaDataKeyIsNotUniqueException, DataKeyIsNotUniqueException, InvalidFileException {
        //given
        byte[] data1 = Util.getRandomBytes(1024 * 1024 * 10);
        String fileName1 = "test-file.bin";
        String fileType1 = "application/octet-stream";

        byte[] data2 = Util.getRandomBytes(1024 * 1024 * 10);
        String fileName2 = "test-file-2.bin";
        String fileType2 = "application/octet-stream";

        FileMetaData metaData1 = new FileMetaData(fileName1, fileType1);
        IncomingFile incomingFile1 = new IncomingFile(new ByteArrayInputStream(data1), metaData1);

        FileMetaData metaData2 = new FileMetaData(fileName2, fileType2);
        IncomingFile incomingFile2 = new IncomingFile(new ByteArrayInputStream(data2), metaData2);

        //when
        String key1 = storageService.store(incomingFile1);
        assertNotNull(key1);
        String key2 = storageService.store(incomingFile2);
        assertNotNull(key2);
        assertNotEquals(key1, key2);

        //then
        StoredFile storedFile1 = storageService.get(key1);
        assertNotNull(storedFile1);
        assertNotNull(storedFile1.getMetaData().getId());

        StoredFile storedFile2 = storageService.get(key2);
        assertNotNull(storedFile2);
        assertNotNull(storedFile2.getMetaData().getId());

        assertNotEquals(storedFile1.getMetaData().getId(), storedFile2.getMetaData().getId());

        StoredFileMetadata storedFile1MetaData = storedFile1.getMetaData();
        StoredFileMetadata expectedMetadata1 = new StoredFileMetadata(metaData1, key1, storedFile1MetaData.getId());
        assertEquals(expectedMetadata1, storedFile1MetaData);

        StoredFileMetadata storedFile2MetaData = storedFile2.getMetaData();
        StoredFileMetadata expectedMetadata2 = new StoredFileMetadata(metaData2, key2, storedFile2MetaData.getId());
        assertEquals(expectedMetadata2, storedFile2MetaData);

        byte[] actualData1 = IOUtils.toByteArray(storedFile1.getData());
        assertArrayEquals(data1, actualData1);

        byte[] actualData2 = IOUtils.toByteArray(storedFile2.getData());
        assertArrayEquals(data2, actualData2);
    }
}