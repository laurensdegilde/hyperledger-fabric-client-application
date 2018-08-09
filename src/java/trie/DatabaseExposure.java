package trie;

import org.bouncycastle.util.encoders.Hex;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import util.encrypt.EncryptedNode;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class DatabaseExposure {

    private final String DATABASE_FILE_PATH = "trie";
    private DB database;
    DBIterator iterator;
    public DatabaseExposure() throws IOException {
        this.database = factory.open(new File(DATABASE_FILE_PATH), new Options());
        this.iterator = this.database.iterator();
    }

    public void put(EncryptedNode node) {
        this.database.put(node.getEncryptedKey(), node.getEncodedValue());
    }

    public byte[] get(EncryptedNode node){
        return this.database.get(node.getEncryptedKey());
    }
}
