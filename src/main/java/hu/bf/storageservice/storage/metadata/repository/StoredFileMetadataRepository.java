package hu.bf.storageservice.storage.metadata.repository;

import hu.bf.storageservice.storage.metadata.entity.StoredFileMetadata;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoredFileMetadataRepository extends CrudRepository<StoredFileMetadata, Long> {
    StoredFileMetadata findByKeyEquals(String key);
}
