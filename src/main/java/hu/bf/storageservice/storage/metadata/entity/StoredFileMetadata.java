package hu.bf.storageservice.storage.metadata.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
public class StoredFileMetadata extends FileMetaData {

    @Id
    @SequenceGenerator(name = "file_id_generator", sequenceName = "file_id_seq")
    @GeneratedValue(generator = "file_id_generator")
    private Long id;

    @Column(unique = true)
    private String key;

    public StoredFileMetadata() {
    }

    public StoredFileMetadata(FileMetaData fileMetaData, String key, Long id) {
        super(fileMetaData.getName(), fileMetaData.getType());
        this.key = key;
        this.id = id;
    }

    public StoredFileMetadata(@NotNull String name, @NotNull String type, @NotNull String key) {
        super(name, type);
        this.key = key;
    }

    public StoredFileMetadata(@NotNull String name, @NotNull String type, @NotNull String key, @NotNull Long id) {
        super(name, type);
        this.key = key;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @Override
    public String toString() {
        return "StoredFileMetadata{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", name='" + getName() + '\'' +
                ", type='" + getType() + '\'' +
                '}';
    }
}
