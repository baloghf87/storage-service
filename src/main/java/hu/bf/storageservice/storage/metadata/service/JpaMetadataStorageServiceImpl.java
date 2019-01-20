package hu.bf.storageservice.storage.metadata.service;

import hu.bf.storageservice.storage.metadata.entity.StoredFileMetadata;
import hu.bf.storageservice.storage.metadata.exception.MetaDataIsNotPresentException;
import hu.bf.storageservice.storage.metadata.exception.MetaDataKeyIsNotUniqueException;
import hu.bf.storageservice.storage.metadata.repository.StoredFileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class JpaMetadataStorageServiceImpl implements MetaDataStorageService {

    @Autowired
    private StoredFileMetadataRepository storedFileMetadataRepository;


    @Override
    public StoredFileMetadata store(StoredFileMetadata metadata) throws MetaDataKeyIsNotUniqueException {
        StoredFileMetadata storedFileMetadata = storedFileMetadataRepository.findByKeyEquals(metadata.getKey());
        if (storedFileMetadata != null) {
            throw new MetaDataKeyIsNotUniqueException();
        }

        return storedFileMetadataRepository.save(metadata);
    }

    @Override
    public StoredFileMetadata update(StoredFileMetadata metadata) throws MetaDataIsNotPresentException {
        Optional<StoredFileMetadata> storedFileMetadata = storedFileMetadataRepository.findById(metadata.getId());
        if (storedFileMetadata.isPresent()) {
            return storedFileMetadataRepository.save(metadata);
        } else {
            throw new MetaDataIsNotPresentException();
        }
    }

    @Override
    public StoredFileMetadata get(String key) {
        return storedFileMetadataRepository.findByKeyEquals(key);
    }
}
