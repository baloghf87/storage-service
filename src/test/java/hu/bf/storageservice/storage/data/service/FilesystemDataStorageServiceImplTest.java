package hu.bf.storageservice.storage.data.service;

import hu.bf.storageservice.Util;
import hu.bf.storageservice.storage.data.exception.DataKeyIsNotUniqueException;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource("classpath:application.properties")
@ContextConfiguration(classes = {FilesystemDataStorageServiceImpl.class})
public class FilesystemDataStorageServiceImplTest {

    @Autowired
    private FilesystemDataStorageServiceImpl filesystemDataStorageServiceImpl;

    @Value("${storage.filesystem.path}")
    private String directory;

    @Before
    public void initializeStorageDirectory() throws IOException {
        Util.deleteDirectory(directory);
        Util.createDirectory(directory);
    }

    @After
    public void removeStorageDirectory() throws IOException {
        Util.deleteDirectory(directory);
    }

    @Test
    public void shouldStoreData() throws DataKeyIsNotUniqueException, IOException {
        //given
        String key = "test-file-1";
        File file = new File(directory + File.separator + key);
        assertFalse(file.exists());
        byte[] data = Util.getRandomBytes(1024 * 1024 * 10);

        //when
        filesystemDataStorageServiceImpl.store(key, new ByteArrayInputStream(data));

        //then
        assertTrue(file.exists());
        byte[] actualData = IOUtils.toByteArray(new FileInputStream(file));
        assertArrayEquals(data, actualData);
    }

    @Test
    public void shouldReturnData() throws IOException {
        //given
        String key = "test-file-1";
        byte[] data = Util.getRandomBytes(1024 * 1024 * 10);
        File file = new File(directory + File.separator + key);
        assertFalse(file.exists());
        IOUtils.copy(new ByteArrayInputStream(data), new FileOutputStream(file));
        assertTrue(file.exists());

        //when
        byte[] dataReturned = IOUtils.toByteArray(filesystemDataStorageServiceImpl.get(key));

        //then
        assertArrayEquals(data, dataReturned);
    }
}