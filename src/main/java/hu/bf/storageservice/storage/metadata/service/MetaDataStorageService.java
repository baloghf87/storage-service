package hu.bf.storageservice.storage.metadata.service;

import hu.bf.storageservice.storage.metadata.entity.StoredFileMetadata;
import hu.bf.storageservice.storage.metadata.exception.MetaDataIsNotPresentException;
import hu.bf.storageservice.storage.metadata.exception.MetaDataKeyIsNotUniqueException;

public interface MetaDataStorageService {
    StoredFileMetadata store(StoredFileMetadata metadata) throws MetaDataKeyIsNotUniqueException;

    StoredFileMetadata update(StoredFileMetadata storedFileMetadata) throws MetaDataKeyIsNotUniqueException, MetaDataIsNotPresentException;

    StoredFileMetadata get(String key);
}
