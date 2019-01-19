package hu.bf.storageservice.entity;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class FileMetaData {

    @NotNull
    private String name;

    @NotNull
    private String type;

    public FileMetaData() {
    }

    public FileMetaData(@NotNull String name, @NotNull String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileMetaData that = (FileMetaData) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, type);
    }
}
