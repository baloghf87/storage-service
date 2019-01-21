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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger LOG = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    @Autowired
    private DataStorageService dataStorageService;

    @Autowired
    private MetaDataStorageService metaDataStorageService;

    @Autowired
    private KeyCreatorService keyCreatorService;

    @Override
    public String store(IncomingFile incomingFile) throws MetaDataKeyIsNotUniqueException, DataKeyIsNotUniqueException, IOException, InvalidFileException {
        LOG.info("Storing file '{}'", incomingFile);
        verify(incomingFile);
        String key = storeMetadata(incomingFile);
        dataStorageService.store(key, incomingFile.getData());
        LOG.info("File '{}' stored successfully", incomingFile);
        return key;
    }

    private void verify(IncomingFile incomingFile) throws InvalidFileException {
        LOG.info("Verifying file '{}'", incomingFile);
        verifyData(incomingFile);
        verifyMetadata(incomingFile);
    }

    private void verifyMetadata(IncomingFile incomingFile) throws InvalidFileException {
        LOG.info("Verifying metadata of incoming file '{}'", incomingFile);
        FileMetaData metaData = incomingFile.getMetaData();
        if (metaData == null) {
            LOG.warn("Metadata not found");
            throw new InvalidFileException("Metadata not found");
        }
        if (metaData.getName() == null) {
            LOG.warn("Filename not found");
            throw new InvalidFileException("Filename not found");
        }
        if (metaData.getType() == null) {
            LOG.warn("Filetype not found");
            throw new InvalidFileException("Filetype not found");
        }
    }

    private void verifyData(IncomingFile incomingFile) throws InvalidFileException {
        LOG.info("Verifying content of incoming file '{}'", incomingFile);
        if (incomingFile.getData() == null) {
            LOG.warn("Content not found");
            throw new InvalidFileException("Data not found");
        }
    }

    private String storeMetadata(IncomingFile incomingFile) throws MetaDataKeyIsNotUniqueException {
        StoredFileMetadata storedFileMetadata = storeMetadataWithoutKey(incomingFile);
        String key = keyCreatorService.createKey(storedFileMetadata.getId());
        try {
            updateKey(storedFileMetadata, key);
        } catch (MetaDataIsNotPresentException e) {
            throw new IllegalStateException("Metadata with id " + storedFileMetadata.getId() + " is not present");
        }
        LOG.info("Successfully stored metadata", storedFileMetadata);
        return key;
    }

    private void updateKey(StoredFileMetadata storedFileMetadata, String key) throws MetaDataKeyIsNotUniqueException, MetaDataIsNotPresentException {
        LOG.info("Updating key of metadata '{}' to '{}'", storedFileMetadata, key);
        storedFileMetadata.setKey(key);
        metaDataStorageService.update(storedFileMetadata);
    }

    private StoredFileMetadata storeMetadataWithoutKey(IncomingFile incomingFile) throws MetaDataKeyIsNotUniqueException {
        LOG.info("Storing metadata of incoming file without key '{}' ", incomingFile);
        StoredFileMetadata storedFileMetadata = new StoredFileMetadata(incomingFile.getMetaData(), null, null);
        return metaDataStorageService.store(storedFileMetadata);
    }

    @Override
    public StoredFile get(String key) throws IOException {
        LOG.info("Getting file with key '{}'", key);
        StoredFileMetadata storedFileMetadata = metaDataStorageService.get(key);
        if (storedFileMetadata != null) {
            InputStream data = dataStorageService.get(key);
            if (data != null) {
                return new StoredFile(data, storedFileMetadata);
            } else {
                LOG.error("Data with key '{}' not found", key);
            }
        } else {
            LOG.info("Metadata with key '{}' not found", key);
        }

        return null;
    }
}
