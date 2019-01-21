package hu.bf.storageservice.storage.file.entity;

import hu.bf.storageservice.storage.metadata.entity.StoredFileMetadata;

import java.io.InputStream;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoredFile that = (StoredFile) o;
        return Objects.equals(data, that.data) &&
                Objects.equals(metaData, that.metaData);
    }

    @Override
    public int hashCode() {

        return Objects.hash(data, metaData);
    }
}
