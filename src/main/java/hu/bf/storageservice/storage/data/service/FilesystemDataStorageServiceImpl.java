package hu.bf.storageservice.storage.data.service;

import hu.bf.storageservice.storage.data.exception.DataKeyIsNotUniqueException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;

public class FilesystemDataStorageServiceImpl implements DataStorageService {

    private String storageDirectory;

    public FilesystemDataStorageServiceImpl(@Value("${storage.filesystem.path}") String storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

    @Override
    public void store(String key, InputStream data) throws DataKeyIsNotUniqueException, IOException {
        File file = new File(storageDirectory + File.separator + key);
        if (file.exists()) {
            throw new DataKeyIsNotUniqueException();
        }

        IOUtils.copy(data, new FileOutputStream(file));
    }

    @Override
    public InputStream get(String key) throws IOException {
        File file = new File(storageDirectory + File.separator + key);
        if (file.exists()) {
            return new FileInputStream(file);
        } else {
            return null;
        }
    }
}
