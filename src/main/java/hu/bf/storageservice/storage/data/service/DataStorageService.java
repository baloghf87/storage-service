package hu.bf.storageservice.storage.data.service;

import hu.bf.storageservice.storage.data.exception.DataKeyIsNotUniqueException;

import java.io.IOException;
import java.io.InputStream;

public interface DataStorageService {

    void store(String key, InputStream data) throws DataKeyIsNotUniqueException, IOException;

    InputStream get(String key) throws IOException;
}
