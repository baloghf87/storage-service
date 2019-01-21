package hu.bf.storageservice.storage.file.service;

import hu.bf.storageservice.Util;
import hu.bf.storageservice.storage.data.service.FilesystemDataStorageServiceImpl;
import hu.bf.storageservice.storage.key.service.KeyCreatorServiceHashidsImpl;
import hu.bf.storageservice.storage.metadata.entity.StoredFileMetadata;
import hu.bf.storageservice.storage.metadata.repository.StoredFileMetadataRepository;
import hu.bf.storageservice.storage.metadata.service.JpaMetadataStorageServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@EntityScan(basePackageClasses = StoredFileMetadata.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ContextConfiguration(classes = {JpaMetadataStorageServiceImpl.class, FilesystemDataStorageServiceImpl.class,
        KeyCreatorServiceHashidsImpl.class, FileStorageServiceImpl.class})
@PropertySource("classpath:application.properties")
@EnableJpaRepositories(basePackageClasses = {StoredFileMetadataRepository.class})
@DataJpaTest
public class JpaMetadataStorageAndFilesystemFileStorageAndHashidsKeyCreatorServiceIntegrationTest extends FileStorageServiceIntegrationTest {

    @Value("${storage.filesystem.path}")
    private String directory;

    @Before
    public void initializeStorageDirectory() throws IOException {
        Util.deleteDirectory(directory);
        Util.createDirectory(directory);
    }

    @After
    public void removeStorageDirectory() throws IOException {
        Util.deleteDirectory(directory);
    }

}