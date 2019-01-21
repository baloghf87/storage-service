package hu.bf.storageservice.storage.file.service;

import hu.bf.storageservice.storage.data.exception.DataKeyIsNotUniqueException;
import hu.bf.storageservice.storage.data.service.DataStorageService;
import hu.bf.storageservice.storage.file.entity.IncomingFile;
import hu.bf.storageservice.storage.file.entity.StoredFile;
import hu.bf.storageservice.storage.key.service.KeyCreatorService;
import hu.bf.storageservice.storage.metadata.entity.FileMetaData;
import hu.bf.storageservice.storage.metadata.entity.StoredFileMetadata;
import hu.bf.storageservice.storage.metadata.exception.MetaDataIsNotPresentException;
import hu.bf.storageservice.storage.metadata.exception.MetaDataKeyIsNotUniqueException;
import hu.bf.storageservice.storage.metadata.service.MetaDataStorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@EntityScan(basePackageClasses = StoredFileMetadata.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ContextConfiguration(classes = {FileStorageServiceImpl.class, FileStorageServiceImplTest.MockConfiguration.class})
@PropertySource("classpath:application.properties")
public class FileStorageServiceImplTest {

    private static final long METADATA_ID = 123l;
    private static final String KEY = Long.toString(METADATA_ID);
    private static final ByteArrayInputStream DATA = new ByteArrayInputStream("DATA".getBytes());
    private static final FileMetaData METADATA = new FileMetaData("name", "type");
    private static final IncomingFile INCOMING_FILE = new IncomingFile(DATA, METADATA);
    private static final StoredFileMetadata STORED_FILE_METADATA = new StoredFileMetadata();

    @Autowired
    private FileStorageServiceImpl fileStorageService;

    @Autowired
    private DataStorageService dataStorageService;

    @Autowired
    private MetaDataStorageService metaDataStorageService;

    @Autowired
    private KeyCreatorService keyCreatorService;

    @Configuration
    public static class MockConfiguration {
        @Bean
        public DataStorageService dataStorageService() {
            return Mockito.mock(DataStorageService.class);
        }

        @Bean
        public MetaDataStorageService metaDataStorageService() {
            return Mockito.mock(MetaDataStorageService.class);
        }

        @Bean
        public KeyCreatorService keyCreatorService() {
            return Mockito.mock(KeyCreatorService.class);
        }
    }

    @Test
    public void storeShouldStoreFile() throws MetaDataKeyIsNotUniqueException, DataKeyIsNotUniqueException, IOException, MetaDataIsNotPresentException, InvalidFileException {
        //given
        setupMockKeyCreatorService();
        setupMockMetadataStorageServiceStore();

        //when
        fileStorageService.store(INCOMING_FILE);

        //then
        InOrder inOrderVerifier = Mockito.inOrder(keyCreatorService, metaDataStorageService, dataStorageService);
        inOrderVerifier.verify(metaDataStorageService).store(new StoredFileMetadata(INCOMING_FILE.getMetaData(), null, null));
        inOrderVerifier.verify(keyCreatorService).createKey(METADATA_ID);
        inOrderVerifier.verify(metaDataStorageService).update(new StoredFileMetadata(INCOMING_FILE.getMetaData(), KEY, METADATA_ID));
        inOrderVerifier.verify(dataStorageService).store(KEY, DATA);
    }

    @Test(expected = IllegalStateException.class)
    public void storeShouldHandleMetaDataIsNotPresentException() throws MetaDataKeyIsNotUniqueException, MetaDataIsNotPresentException, DataKeyIsNotUniqueException, IOException, InvalidFileException {
        //given
        setupMockKeyCreatorService();
        setupMockMetadataStorageServiceStore();
        setupMockMetadataStorageServiceUpdateShouldThrowMetaDataIsNotPresentException();

        //when
        fileStorageService.store(INCOMING_FILE);

        //then it should throw exception
    }

    @Test
    public void storeShouldNotAcceptFileWithoutData() throws MetaDataKeyIsNotUniqueException, DataKeyIsNotUniqueException, IOException {
        //given
        IncomingFile incomingFileWithoudData = new IncomingFile(null, new FileMetaData());

        //when
        try {
            fileStorageService.store(incomingFileWithoudData);
            fail("Should have been failed");
        } catch (InvalidFileException e) {
            //then
            assertEquals("Data not found", e.getMessage());
        }
    }

    @Test
    public void storeShouldNotAcceptFileWithoutMetadata() throws MetaDataKeyIsNotUniqueException, DataKeyIsNotUniqueException, IOException {
        //given
        IncomingFile incomingFileWithoudData = new IncomingFile(DATA, null);

        //when
        try {
            fileStorageService.store(incomingFileWithoudData);
            fail("Should have been failed");
        } catch (InvalidFileException e) {
            //then
            assertEquals("Metadata not found", e.getMessage());
        }
    }

    @Test
    public void storeShouldNotAcceptFileWithoutName() throws MetaDataKeyIsNotUniqueException, DataKeyIsNotUniqueException, IOException {
        //given
        IncomingFile incomingFileWithoudData = new IncomingFile(DATA, new FileMetaData(null, "type"));

        //when
        try {
            fileStorageService.store(incomingFileWithoudData);
            fail("Should have been failed");
        } catch (InvalidFileException e) {
            //then
            assertEquals("Filename not found", e.getMessage());
        }
    }

    @Test
    public void storeShouldNotAcceptFileWithoutType() throws MetaDataKeyIsNotUniqueException, DataKeyIsNotUniqueException, IOException {
        //given
        IncomingFile incomingFileWithoudData = new IncomingFile(DATA, new FileMetaData("name", null));

        //when
        try {
            fileStorageService.store(incomingFileWithoudData);
            fail("Should have been failed");
        } catch (InvalidFileException e) {
            //then
            assertEquals("Filetype not found", e.getMessage());
        }
    }

    @Test
    public void getShouldReturnFile() throws IOException {
        //given
        setupMockMetadataStorageServiceGet();
        setupMockDataStorageServiceGet();
        StoredFile expectedStoredFile = new StoredFile(DATA, STORED_FILE_METADATA);

        //when
        StoredFile storedFile = fileStorageService.get(KEY);

        //then
        assertEquals(expectedStoredFile, storedFile);
    }

    @Test
    public void getShouldReturnNullWhenMetadataIsNotFound() throws IOException {
        //given
        setupMockDataStorageServiceGet();

        //when
        StoredFile storedFile = fileStorageService.get(KEY);

        //then
        assertNull(storedFile);
    }

    @Test
    public void getShouldReturnNullWhenDataIsNotFound() throws IOException {
        //given
        setupMockMetadataStorageServiceGet();

        //when
        StoredFile storedFile = fileStorageService.get(KEY);

        //then
        assertNull(storedFile);
    }

    private void setupMockDataStorageServiceGet() throws IOException {
        Mockito.when(dataStorageService.get(Mockito.eq(KEY))).thenReturn(DATA);
    }

    private void setupMockMetadataStorageServiceGet() {
        Mockito.when(metaDataStorageService.get(Mockito.eq(KEY))).thenReturn(STORED_FILE_METADATA);
    }

    private void setupMockMetadataStorageServiceUpdateShouldThrowMetaDataIsNotPresentException() throws MetaDataIsNotPresentException, MetaDataKeyIsNotUniqueException {
        Mockito.when(metaDataStorageService.update(Mockito.any())).thenThrow(MetaDataIsNotPresentException.class);
    }

    private void setupMockMetadataStorageServiceStore() throws MetaDataKeyIsNotUniqueException {
        Mockito.when(metaDataStorageService.store(Mockito.any(StoredFileMetadata.class))).then(new Answer<StoredFileMetadata>() {
            @Override
            public StoredFileMetadata answer(InvocationOnMock invocation) {
                StoredFileMetadata metadataToStore = (StoredFileMetadata) invocation.getArguments()[0];

                StoredFileMetadata storedFileMetadata = new StoredFileMetadata();
                storedFileMetadata.setId(METADATA_ID);
                storedFileMetadata.setKey(metadataToStore.getKey());
                storedFileMetadata.setName(metadataToStore.getName());
                storedFileMetadata.setType(metadataToStore.getType());

                return storedFileMetadata;
            }
        });
    }

    private void setupMockKeyCreatorService() {
        Mockito.when(keyCreatorService.createKey(Mockito.anyLong())).then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) {
                return Long.toString((Long) invocation.getArguments()[0]);
            }
        });
    }
}