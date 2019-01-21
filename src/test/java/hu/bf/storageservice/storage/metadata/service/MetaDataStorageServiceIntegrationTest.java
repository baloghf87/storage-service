package hu.bf.storageservice.storage.metadata.service;

import hu.bf.storageservice.storage.metadata.entity.StoredFileMetadata;
import hu.bf.storageservice.storage.metadata.exception.MetaDataIsNotPresentException;
import hu.bf.storageservice.storage.metadata.exception.MetaDataKeyIsNotUniqueException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public abstract class MetaDataStorageServiceIntegrationTest {

    @Autowired
    private MetaDataStorageService metaDataStorageService;

    @Test
    public void shouldStoreAndGet() throws MetaDataKeyIsNotUniqueException {
        //given
        String key1 = "test-key-1";
        assertNull(metaDataStorageService.get(key1));
        StoredFileMetadata storedFileMetadata1 = new StoredFileMetadata("name1", "type1", key1);
        metaDataStorageService.store(storedFileMetadata1);

        String key2 = "test-key-2";
        assertNull(metaDataStorageService.get(key2));
        StoredFileMetadata storedFileMetadata2 = new StoredFileMetadata("name2", "type2", key2);
        metaDataStorageService.store(storedFileMetadata2);

        //when
        StoredFileMetadata actualStoredFileMetadata1 = metaDataStorageService.get(key1);
        StoredFileMetadata actualStoredFileMetadata2 = metaDataStorageService.get(key2);

        //then
        assertEquals(storedFileMetadata1, actualStoredFileMetadata1);
        assertEquals(storedFileMetadata2, actualStoredFileMetadata2);
    }

    @Test
    public void shouldThrowExceptionWhenKeyIsNotUnique() throws MetaDataKeyIsNotUniqueException {
        //given
        String key = "test-key-not-unique";
        assertNull(metaDataStorageService.get(key));
        StoredFileMetadata storedFileMetadata = new StoredFileMetadata("name", "type", key);
        metaDataStorageService.store(storedFileMetadata);

        //when
        try {
            metaDataStorageService.store(storedFileMetadata);
            fail("It should have been failed");
        } catch (MetaDataKeyIsNotUniqueException e) {
            //then it should throw exception
        }
    }

    @Test
    public void shouldUpdate() throws MetaDataKeyIsNotUniqueException, MetaDataIsNotPresentException {
        //given
        String key1 = "test-key-543";
        assertNull(metaDataStorageService.get(key1));
        StoredFileMetadata storedFileMetadata1 = new StoredFileMetadata("name1", "type1", key1);
        metaDataStorageService.store(storedFileMetadata1);

        String key2 = "test-key-654";
        assertNull(metaDataStorageService.get(key2));
        StoredFileMetadata storedFileMetadata2 = new StoredFileMetadata("name2", "type2", key2);
        metaDataStorageService.store(storedFileMetadata2);

        assertEquals(storedFileMetadata1, metaDataStorageService.get(storedFileMetadata1.getKey()));
        assertEquals(storedFileMetadata2, metaDataStorageService.get(storedFileMetadata2.getKey()));

        //when
        storedFileMetadata1.setKey("key-1");
        storedFileMetadata1.setName("name-1");
        storedFileMetadata1.setType("type-1");
        metaDataStorageService.update(storedFileMetadata1);

        storedFileMetadata2.setKey("key-2");
        storedFileMetadata2.setName("name-2");
        storedFileMetadata2.setType("type-2");
        metaDataStorageService.update(storedFileMetadata2);

        //then
        assertEquals(storedFileMetadata1, metaDataStorageService.get(storedFileMetadata1.getKey()));
        assertEquals(storedFileMetadata2, metaDataStorageService.get(storedFileMetadata2.getKey()));
    }


    @Test
    public void shouldThrowExceptionWhenUpdatingNotExistingMetaData() throws MetaDataKeyIsNotUniqueException {
        //given
        StoredFileMetadata storedFileMetadata = new StoredFileMetadata("name", "type", "key");
        storedFileMetadata.setId(123l);

        //when
        try {
            metaDataStorageService.update(storedFileMetadata);
            fail("It should have been failed");
        } catch (MetaDataIsNotPresentException e) {
            //then it should fail
        }
    }
}