package hu.bf.storageservice.storage.key.service;

public interface KeyCreatorService {
    /**
     * Create a unique key from a numeric value to be used to reference a file.
     * Implementations must ensure that the same keys are returned for the same values and the returned keys are unique.
     *
     * @param value the numeric value
     * @return the unique key
     */
    String createKey(long value);
}
