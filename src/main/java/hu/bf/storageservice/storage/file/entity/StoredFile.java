package hu.bf.storageservice.storage.file.entity;

import hu.bf.storageservice.storage.metadata.entity.StoredFileMetadata;

import java.io.InputStream;

public class StoredFile {
    private InputStream data;
    private StoredFileMetadata metaData;

    public StoredFile() {
    }

    public StoredFile(InputStream data, StoredFileMetadata metaData) {
        this.data = data;
        this.metaData = metaData;
    }

    public InputStream getData() {
        return data;
    }

    public void setData(InputStream data) {
        this.data = data;
    }

    public StoredFileMetadata getMetaData() {
        return metaData;
    }

    public void setMetaData(StoredFileMetadata metaData) {
        this.metaData = metaData;
    }
}
