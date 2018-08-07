package trie;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class DatabaseWrapper {

    private DB database;
    private Options options;

    public DatabaseWrapper(String filePath) throws IOException {
        options = new Options();
        database = factory.open(new File(filePath), options);
    }

    public DB getDatabase() {
        return database;
    }

    public void put(Object o) throws NoSuchAlgorithmException {
        Value value = new Value(o);
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] v = value.encode();
        byte[] k = sha256.digest(v);
        System.out.println(v.length);
        this.database.put(k, v);
    }

    public byte[] get(byte[] key){
        return this.database.get(key);
    }
}
