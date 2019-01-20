package hu.bf.storageservice.storage.key.service;

import org.hashids.Hashids;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class KeyCreatorServiceHashidsImplTest {

    @Test
    public void shouldCallHashids() {
        //given
        long id = 123;
        String key = "test";
        Hashids mockHashids = Mockito.mock(Hashids.class);
        Mockito.when(mockHashids.encode(Mockito.eq(id))).thenReturn(key);

        //when
        KeyCreatorServiceHashidsImpl keyCreatorServiceHashids = new KeyCreatorServiceHashidsImpl(mockHashids);
        String actualKey = keyCreatorServiceHashids.createKey(id);

        //then
        Mockito.verify(mockHashids).encode(id);
        assertEquals(key, actualKey);
    }

}