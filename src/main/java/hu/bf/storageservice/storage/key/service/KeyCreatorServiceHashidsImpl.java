package hu.bf.storageservice.storage.key.service;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class KeyCreatorServiceHashidsImpl implements KeyCreatorService {

    private Hashids hashids;

    @Autowired
    public KeyCreatorServiceHashidsImpl(@Value("${key.salt}") String salt) {
        this.hashids = new Hashids(salt);
    }


    public KeyCreatorServiceHashidsImpl(Hashids hashids) {
        this.hashids = hashids;
    }

    @Override
    public String createKey(long id) {
        return hashids.encode(id);
    }

}
