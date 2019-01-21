package hu.bf.storageservice.storage.file.service;

import hu.bf.storageservice.storage.data.exception.DataKeyIsNotUniqueException;
import hu.bf.storageservice.storage.file.entity.IncomingFile;
import hu.bf.storageservice.storage.file.entity.StoredFile;
import hu.bf.storageservice.storage.metadata.exception.MetaDataKeyIsNotUniqueException;

import java.io.IOException;

public interface FileStorageService {

    String store(IncomingFile incomingFile) throws MetaDataKeyIsNotUniqueException, DataKeyIsNotUniqueException, IOException, InvalidFileException;

    StoredFile get(String key) throws IOException;

}
