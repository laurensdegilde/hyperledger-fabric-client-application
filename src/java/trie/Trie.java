package trie;

import org.bouncycastle.util.encoders.Hex;
import util.encrypt.EncryptHelper;
import util.encrypt.EncryptedNode;
import util.Util;
import util.rlp.RLPHelper;

import static java.util.Arrays.copyOfRange;
import static util.NibbleHelper.binToNibbles;
import static util.NibbleHelper.packNibbles;
import static util.NibbleHelper.unpackToNibbles;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Trie {

    private static byte PAIR_SIZE = 2;
    private static byte LIST_SIZE = 17;
    private static final String ROOTHASH_FILE_PATH = "trie/root-hash.txt";
    private static final String TREEDUMP_FILE_PATH = "trie/trie-dump.json";

    private Object root;
    private DatabaseExposure databaseExposure;

    public Trie() throws IOException {
        this.databaseExposure = new DatabaseExposure();
        this.root = setRoot();
    }

    public Object setRoot() {
        byte [] data = this.databaseExposure.get(this.getRoot());
        if (data == null){
            return new byte[]{};
        }
        Value node = RLPHelper.decode(data);
        return node;
    }

    public byte[] get(String key) throws NoSuchAlgorithmException {
        byte[] k = binToNibbles(key.getBytes());
        Value value = new Value(this.get(this.root, k));
        return (value == null)? null : value.asBytes();
    }

    private Object get(Object node, byte[] key) throws NoSuchAlgorithmException {
        if (key.length == 0 || isEmptyNode(node)) {
            return node;
        }

        Value currentNode = this.getNode(node);
        if (currentNode == null) return null;

        if (currentNode.length() == PAIR_SIZE) {

            byte[] k = unpackToNibbles(currentNode.get(0).asBytes());
            Object v = currentNode.get(1).asObj();

            if (key.length >= k.length && Arrays.equals(k, copyOfRange(key, 0, k.length))) {
                return this.get(v, copyOfRange(key, k.length, key.length));
            } else {
                return "";
            }
        } else {
            return this.get(currentNode.get(key[0]).asObj(), copyOfRange(key, 1, key.length));
        }
    }

    public void insert(String key, String value) throws NoSuchAlgorithmException, IOException {
        byte[] k = binToNibbles(key.getBytes());
        this.root = this.insert(this.root, k, value);
        Util.writeToFile(ROOTHASH_FILE_PATH, Hex.toHexString(this.getRoot().getEncryptedKey()));
    }

    private Object insert(Object node, byte[] key, Object value) throws NoSuchAlgorithmException {
        if (key.length == 0) {
            return value;
        }

        if (isEmptyNode(node)) {
            Object[] newNode = new Object[] { packNibbles(key), value };
            EncryptedNode encryptedNode = EncryptHelper.encryptNode(newNode);
            this.databaseExposure.put(encryptedNode);
            return newNode;
        }

        Value currentNode = this.getNode(node);

        if (currentNode.length() == PAIR_SIZE) {
            byte[] k = unpackToNibbles(currentNode.get(0).asBytes());
            Object v = currentNode.get(1).asObj();

            if (Arrays.equals(k, key)) {
                Object[] newNode = new Object[] {packNibbles(key), value};
                EncryptedNode encryptedNode = EncryptHelper.encryptNode(newNode);
                this.databaseExposure.put(encryptedNode);
                return newNode;
            }

            Object newHash;
            int matchingLength = matchingNibbleLength(key, k);
            if (matchingLength == k.length) {

                byte[] remainingKeypart = copyOfRange(key, matchingLength, key.length);
                newHash = this.insert(v, remainingKeypart, value);

            } else {

                Object oldNode = this.insert("", copyOfRange(k, matchingLength+1, k.length), v);
                Object newNode = this.insert("", copyOfRange(key, matchingLength+1, key.length), value);

                Object[] scaledSlice = emptyStringSlice(17);

                scaledSlice[k[matchingLength]] = oldNode;
                scaledSlice[key[matchingLength]] = newNode;

                EncryptedNode encryptedNode = EncryptHelper.encryptNode(scaledSlice);
                this.databaseExposure.put(encryptedNode);
                newHash = scaledSlice;
            }

            if (matchingLength == 0) {
                return newHash;
            } else {
                Object[] newNode = new Object[] { packNibbles(copyOfRange(key, 0, matchingLength)), newHash};
                EncryptedNode encryptedNode = EncryptHelper.encryptNode(newNode);
                this.databaseExposure.put(encryptedNode);
                return newNode;
            }
        } else {
            Object[] newNode = copyNode(currentNode);

            newNode[key[0]] = this.insert(currentNode.get(key[0]).asObj(), copyOfRange(key, 1, key.length), value);
            EncryptedNode encryptedNode = EncryptHelper.encryptNode(newNode);
            this.databaseExposure.put(encryptedNode);
            return newNode;
        }

    }

    private Value getNode(Object node) throws NoSuchAlgorithmException {
        EncryptedNode encryptedNode = EncryptHelper.encryptNode(node);
        Value val = RLPHelper.decode(this.databaseExposure.get(encryptedNode));

        if (!val.isBytes()) {
            return val;
        }

        byte[] keyBytes = val.asBytes();
        if (keyBytes.length == 0) {
            return val;
        } else if (keyBytes.length < 32) {
            return new Value(keyBytes);
        }
        return null;
    }


    private boolean isEmptyNode(Object node) {
        Value n = new Value(node);
        return (node == null || (n.isString() && (n.asString() == "" || n.get(0).isNull())) || n.length() == 0);
    }

    private Object[] copyNode(Value currentNode) {
        Object[] itemList = emptyStringSlice(LIST_SIZE);
        for (int i = 0; i < LIST_SIZE; i++) {
            Object cpy = currentNode.get(i).asObj();
            if (cpy != null)
                itemList[i] = cpy;
        }
        return itemList;
    }

    private Object[] emptyStringSlice(int l) {
        Object[] slice = new Object[l];
        for (int i = 0; i < l; i++) {
            slice[i] = "";
        }
        return slice;
    }

    public EncryptedNode getRoot(){
        EncryptedNode encryptedRoot;
        try{
           encryptedRoot = new EncryptedNode(Hex.decode(Util.readFile(ROOTHASH_FILE_PATH)), null);
        }catch(Exception ee){
           encryptedRoot = new EncryptedNode(new byte[]{}, null);
        }
        return encryptedRoot;
    }

    public static int matchingNibbleLength(byte[] a, byte[] b) {
        int i = 0;
        int length = a.length < b.length ? a.length : b.length;
        while (i < length) {
            if (a[i] != b[i])
                break;
            i++;
        }
        return i;
    }

    private void scanTree(EncryptedNode encryptedNode, ScanAction scanAction) {
        byte [] data = this.databaseExposure.get(encryptedNode);
        Value node = RLPHelper.decode(data);
        if (node == null) return;

        if (node.isList()) {
            List<Object> siblings =  node.asList();
            if (siblings.size() == PAIR_SIZE) {
                Value val = new Value(siblings.get(1));
                if (val.isHashCode())
                    scanTree(new EncryptedNode(val.asBytes(),null), scanAction);
            } else {
                for (int j = 0; j < LIST_SIZE; ++j) {
                    Value val = new Value(siblings.get(j));
                    if (val.isHashCode())
                        scanTree(new EncryptedNode(val.asBytes(),null), scanAction);
                }
            }
            scanAction.doOnNode(encryptedNode, node);
        }
    }

    public String getTrieDump() {

        String root = "";
        TraceAllNodes traceAction = new TraceAllNodes();
        this.scanTree(this.getRoot(), traceAction);

        if (this.root instanceof Value) {
            root = "root: " + Hex.toHexString(getRoot().getEncryptedKey()) +  " => " + this.root +  "\n";
        } else {
            root = "root: " + Hex.toHexString(getRoot().getEncryptedKey()) + "\n";
        }
        return root + traceAction.getOutput();
    }

    public void dumpTrie() throws  IOException {
        Util.writeToFile(TREEDUMP_FILE_PATH, this.getTrieDump());
    }

    public interface ScanAction {
        public void doOnNode(EncryptedNode encryptedNode, Value node);
    }
}
