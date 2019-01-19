package hu.bf.storageservice.entity;

import java.io.InputStream;

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
}
