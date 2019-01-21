package hu.bf.storageservice.storage.file.service;

/**
 * Exception thrown when the incoming file is not acceptable
 */
public class InvalidFileException extends Exception {
    public InvalidFileException(String message) {
        super(message);
    }
}
