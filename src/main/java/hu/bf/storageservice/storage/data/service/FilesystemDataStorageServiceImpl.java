package hu.bf.storageservice.storage.data.service;

import hu.bf.storageservice.storage.data.exception.DataKeyIsNotUniqueException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class FilesystemDataStorageServiceImpl implements DataStorageService {

    private static final Logger LOG = LoggerFactory.getLogger(FilesystemDataStorageServiceImpl.class);

    private String storageDirectory;

    public FilesystemDataStorageServiceImpl(@Value("${storage.filesystem.path}") String storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

    @Override
    public void store(String key, InputStream data) throws DataKeyIsNotUniqueException, IOException {
        LOG.info("Storing file '{}'", key);
        File file = new File(storageDirectory + File.separator + key);
        if (file.exists()) {
            LOG.error("File '{}' already exists", key);
            throw new DataKeyIsNotUniqueException();
        }

        IOUtils.copy(data, new FileOutputStream(file));
        LOG.info("Successfully stored file '{}'", key);
    }

    @Override
    public InputStream get(String key) throws IOException {
        LOG.info("Getting file '{}'", key);
        File file = new File(storageDirectory + File.separator + key);
        if (file.exists()) {
            return new FileInputStream(file);
        } else {
            LOG.info("File '{}' does not exists", key);
            return null;
        }
    }
}
