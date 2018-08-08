package trie;

import org.bouncycastle.util.encoders.Hex;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class DatabaseExposure {

    private final String DATABASE_FILE_PATH = "trie";
    private DB database;
    private Options options;
    public DatabaseExposure() throws IOException {
        options = new Options();
        database = factory.open(new File(DATABASE_FILE_PATH), options);
    }

    public void put(Object o) throws NoSuchAlgorithmException {
        Value value = new Value(o);
        byte[] v = value.encode();
        byte[] k = Util.getSHA256().digest(v);
        this.database.put(k, v);
    }

    public byte[] get(byte[] key){
        return this.database.get(key);
    }
}
