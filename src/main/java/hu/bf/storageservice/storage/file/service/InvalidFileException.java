package hu.bf.storageservice.storage.file.service;

public class InvalidFileException extends Exception {
    public InvalidFileException(String message) {
        super(message);
    }
}
