package hu.bf.storageservice.storage.metadata.service;

import hu.bf.storageservice.storage.metadata.entity.StoredFileMetadata;
import hu.bf.storageservice.storage.metadata.exception.MetaDataIsNotPresentException;
import hu.bf.storageservice.storage.metadata.exception.MetaDataKeyIsNotUniqueException;
import hu.bf.storageservice.storage.metadata.repository.StoredFileMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaMetadataStorageServiceImpl implements MetaDataStorageService {

    private static final Logger LOG = LoggerFactory.getLogger(JpaMetadataStorageServiceImpl.class);

    @Autowired
    private StoredFileMetadataRepository storedFileMetadataRepository;

    @Override
    public StoredFileMetadata store(StoredFileMetadata metadata) throws MetaDataKeyIsNotUniqueException {
        LOG.info("Storing metadata '{}'", metadata);
        StoredFileMetadata storedFileMetadata = storedFileMetadataRepository.findByKeyEquals(metadata.getKey());
        if (storedFileMetadata != null) {
            LOG.info("Metadata is already present with key '{}': '{}'", metadata.getKey(), storedFileMetadata);
            throw new MetaDataKeyIsNotUniqueException();
        }

        return storedFileMetadataRepository.save(metadata);
    }

    @Override
    public StoredFileMetadata update(StoredFileMetadata metadata) throws MetaDataIsNotPresentException {
        LOG.info("Updating metadata '{}'", metadata);
        Optional<StoredFileMetadata> storedFileMetadata = storedFileMetadataRepository.findById(metadata.getId());
        if (storedFileMetadata.isPresent()) {
            return storedFileMetadataRepository.save(metadata);
        } else {
            LOG.info("Metadata is not found with key '{}'", metadata.getKey());
            throw new MetaDataIsNotPresentException();
        }
    }

    @Override
    public StoredFileMetadata get(String key) {
        LOG.info("Getting metadata with key '{}'", key);
        return storedFileMetadataRepository.findByKeyEquals(key);
    }
}
