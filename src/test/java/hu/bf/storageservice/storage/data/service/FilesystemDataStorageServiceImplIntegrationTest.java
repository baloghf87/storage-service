package hu.bf.storageservice.storage.data.service;

import hu.bf.storageservice.Util;
import hu.bf.storageservice.storage.metadata.entity.StoredFileMetadata;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource("classpath:application.properties")
@EntityScan(basePackageClasses = StoredFileMetadata.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ContextConfiguration(classes = {FilesystemDataStorageServiceImpl.class})
public class FilesystemDataStorageServiceImplIntegrationTest extends DataStorageServiceIntegrationTest {

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