package hu.bf.storageservice.storage.metadata.service;

import hu.bf.storageservice.storage.metadata.entity.StoredFileMetadata;
import hu.bf.storageservice.storage.metadata.exception.MetaDataIsNotPresentException;
import hu.bf.storageservice.storage.metadata.exception.MetaDataKeyIsNotUniqueException;
import hu.bf.storageservice.storage.metadata.repository.StoredFileMetadataRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@EntityScan(basePackageClasses = StoredFileMetadata.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ContextConfiguration(classes = {JpaMetadataStorageServiceImpl.class, JpaMetadataStorageServiceImplTest.MockConfiguration.class})
@PropertySource("classpath:application.properties")
public class JpaMetadataStorageServiceImplTest {

    @Autowired
    private JpaMetadataStorageServiceImpl jpaMetadataStorageService;

    @Autowired
    private StoredFileMetadataRepository storedFileMetadataRepository;

    @Configuration
    public static class MockConfiguration {
        @Bean
        public StoredFileMetadataRepository storedFileMetadataRepository() {
            return Mockito.mock(StoredFileMetadataRepository.class);
        }
    }

    @Test
    public void store() throws MetaDataKeyIsNotUniqueException {
        //given
        StoredFileMetadata metadata = new StoredFileMetadata("name", "type", "key", 1l);

        //when
        jpaMetadataStorageService.store(metadata);

        //then
        Mockito.verify(storedFileMetadataRepository).save(metadata);
    }

    @Test(expected = MetaDataKeyIsNotUniqueException.class)
    public void storeShouldThrowExceptionWhenKeyIsAlreadyPresent() throws MetaDataKeyIsNotUniqueException {
        //given
        StoredFileMetadata metadata = new StoredFileMetadata("name", "type", "key", 1l);
        Mockito.when(storedFileMetadataRepository.findByKeyEquals(Mockito.eq(metadata.getKey()))).thenReturn(metadata);

        //when
        jpaMetadataStorageService.store(metadata);

        //then it should throw exception
    }

    @Test
    public void update() throws MetaDataIsNotPresentException {
        //given
        StoredFileMetadata metadata = new StoredFileMetadata("name", "type", "key", 1l);
        Mockito.when(storedFileMetadataRepository.findById(Mockito.eq(metadata.getId()))).thenReturn(Optional.of(metadata));

        //when
        StoredFileMetadata metadataToUpdate = new StoredFileMetadata("name2", "type2", "key2", 1l);
        jpaMetadataStorageService.update(metadataToUpdate);

        //then
        Mockito.verify(storedFileMetadataRepository).save(metadataToUpdate);
    }

    @Test(expected = MetaDataIsNotPresentException.class)
    public void updateShouldThrowExceptionWhenMetadataIsNotPresent() throws MetaDataIsNotPresentException {
        //given
        Mockito.when(storedFileMetadataRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        //when
        jpaMetadataStorageService.update(new StoredFileMetadata());

        //then it should throw exception
    }

    @Test
    public void get() {
        //given
        String key = "key";
        StoredFileMetadata metadata = new StoredFileMetadata();
        Mockito.when(storedFileMetadataRepository.findByKeyEquals(Mockito.eq(key))).thenReturn(metadata);

        //when
        StoredFileMetadata actualMetadata = jpaMetadataStorageService.get(key);

        //then
        Mockito.verify(storedFileMetadataRepository).findByKeyEquals(key);
        assertTrue(actualMetadata == metadata);
    }
}