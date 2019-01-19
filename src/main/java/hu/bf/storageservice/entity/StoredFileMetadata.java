package hu.bf.storageservice.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
public class StoredFileMetadata extends FileMetaData {

    @Id
    @SequenceGenerator(name = "file_id_generator", sequenceName = "file_id_seq")
    @GeneratedValue(generator = "file_id_generator")
    private long id;

    private String key;

    public StoredFileMetadata() {
    }

    public StoredFileMetadata(FileMetaData fileMetaData, String key) {
        super(fileMetaData.getName(), fileMetaData.getType());
        this.key = key;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StoredFileMetadata that = (StoredFileMetadata) o;
        return id == that.id &&
                Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, key);
    }
}
