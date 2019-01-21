package hu.bf.storageservice.storage.key.service;

import org.hashids.Hashids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyCreatorServiceHashidsImpl implements KeyCreatorService {

    private static final Logger LOG = LoggerFactory.getLogger(KeyCreatorServiceHashidsImpl.class);

    private Hashids hashids;

    @Autowired
    public KeyCreatorServiceHashidsImpl(@Value("${storage.key.salt}") String salt) {
        LOG.info("Initializing Hashids with salt");
        this.hashids = new Hashids(salt);
    }

    public KeyCreatorServiceHashidsImpl(Hashids hashids) {
        LOG.info("Initializing with external Hashids instance");
        this.hashids = hashids;
    }

    @Override
    public String createKey(long value) {
        LOG.info("Creating key for id '{}'", value);
        return hashids.encode(value);
    }

}
