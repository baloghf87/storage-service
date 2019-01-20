package hu.bf.storageservice;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public final class Util {

    public static byte[] getRandomBytes(int size) {
        byte[] bytes = new byte[size];
        new Random().nextBytes(bytes);
        return bytes;
    }

    public static void createDirectory(String directory) {
        new File(directory).mkdirs();
    }

    public static void deleteDirectory(String directory) throws IOException {
        File target = new File(directory);
        if (target.exists()) {
            FileUtils.deleteDirectory(target);
        }
    }

    private Util() {
    }
}
