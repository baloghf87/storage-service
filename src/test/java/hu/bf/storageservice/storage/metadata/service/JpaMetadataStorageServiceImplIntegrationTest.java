package hu.bf.storageservice.storage.metadata.service;


import hu.bf.storageservice.storage.metadata.entity.StoredFileMetadata;
import hu.bf.storageservice.storage.metadata.repository.StoredFileMetadataRepository;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@EntityScan(basePackageClasses = StoredFileMetadata.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ContextConfiguration(classes = {JpaMetadataStorageServiceImpl.class})
@PropertySource("classpath:application.properties")
@EnableJpaRepositories(basePackageClasses = {StoredFileMetadataRepository.class})
@DataJpaTest
public class JpaMetadataStorageServiceImplIntegrationTest extends MetaDataStorageServiceIntegrationTest {

}