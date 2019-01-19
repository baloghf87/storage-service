package hu.bf.storageservice.service;

import hu.bf.storageservice.entity.IncomingFile;
import hu.bf.storageservice.entity.StoredFile;

public interface StorageService {

    String store(IncomingFile incomingFile);

    StoredFile get(String key);

}
