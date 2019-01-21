package hu.bf.storageservice.storage.file.entity;

import hu.bf.storageservice.storage.metadata.entity.FileMetaData;

import java.io.InputStream;
import java.util.Objects;

public class IncomingFile {

    private InputStream data;

    private FileMetaData metaData;

    public IncomingFile() {
    }

    public IncomingFile(InputStream data, FileMetaData metaData) {
        this.data = data;
        this.metaData = metaData;
    }

    public InputStream getData() {
        return data;
    }

    public void setData(InputStream data) {
        this.data = data;
    }

    public FileMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(FileMetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IncomingFile that = (IncomingFile) o;
        return Objects.equals(data, that.data) &&
                Objects.equals(metaData, that.metaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, metaData);
    }

    @Override
    public String toString() {
        return "IncomingFile{" +
                "data=" + data +
                ", metaData=" + metaData +
                '}';
    }
}
