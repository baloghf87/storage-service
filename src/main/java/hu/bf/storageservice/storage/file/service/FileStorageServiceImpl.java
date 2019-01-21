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
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;

public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    private DataStorageService dataStorageService;

    @Autowired
    private MetaDataStorageService metaDataStorageService;

    @Autowired
    private KeyCreatorService keyCreatorService;

    @Override
    public String store(IncomingFile incomingFile) throws MetaDataKeyIsNotUniqueException, DataKeyIsNotUniqueException, IOException, InvalidFileException {
        verify(incomingFile);
        String key = storeMetadata(incomingFile);
        dataStorageService.store(key, incomingFile.getData());
        return key;
    }

    private void verify(IncomingFile incomingFile) throws InvalidFileException {
        verifyData(incomingFile);
        verifyMetadata(incomingFile);
    }

    private void verifyMetadata(IncomingFile incomingFile) throws InvalidFileException {
        FileMetaData metaData = incomingFile.getMetaData();
        if (metaData == null) {
            throw new InvalidFileException("Metadata not found");
        }
        if (metaData.getName() == null) {
            throw new InvalidFileException("Filename not found");
        }
        if (metaData.getType() == null) {
            throw new InvalidFileException("Filetype not found");
        }
    }

    private void verifyData(IncomingFile incomingFile) throws InvalidFileException {
        if (incomingFile.getData() == null) {
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
        return key;
    }

    private void updateKey(StoredFileMetadata storedFileMetadata, String key) throws MetaDataKeyIsNotUniqueException, MetaDataIsNotPresentException {
        storedFileMetadata.setKey(key);
        metaDataStorageService.update(storedFileMetadata);
    }

    private StoredFileMetadata storeMetadataWithoutKey(IncomingFile incomingFile) throws MetaDataKeyIsNotUniqueException {
        StoredFileMetadata storedFileMetadata = new StoredFileMetadata(incomingFile.getMetaData(), null, null);
        return metaDataStorageService.store(storedFileMetadata);
    }

    @Override
    public StoredFile get(String key) throws IOException {
        StoredFileMetadata storedFileMetadata = metaDataStorageService.get(key);
        if (storedFileMetadata != null) {
            InputStream data = dataStorageService.get(key);
            if (data != null) {
                return new StoredFile(data, storedFileMetadata);
            }
        }

        return null;
    }
}
