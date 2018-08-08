package trie;

import org.bouncycastle.util.encoders.Hex;
import util.NibbleEncoder;
import util.Util;

import static java.util.Arrays.copyOfRange;
import static util.NibbleEncoder.binToNibbles;
import static util.NibbleEncoder.packNibbles;
import static util.NibbleEncoder.unpackToNibbles;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Trie {

    private static byte PAIR_SIZE = 2;
    private static byte LIST_SIZE = 17;

    private Object root;
    private DatabaseExposure databaseExposure;

    public Trie() throws IOException, NoSuchAlgorithmException {
        this.databaseExposure = new DatabaseExposure();
        this.root = setRoot();
    }

    public Object setRoot() throws NoSuchAlgorithmException {
        byte [] data = this.databaseExposure.get(this.getRoot());
        if (data == null){
            return new byte[]{};
        }
        Value node = Value.decode(data);
        return node;
    }

    public byte[] get(String key) throws NoSuchAlgorithmException {
        byte[] k = binToNibbles(key.getBytes());
        Value value = new Value(this.get(this.root, k));
        return (value == null)? null : value.asBytes();
    }

    private Object get(Object node, byte[] key) throws NoSuchAlgorithmException {
        // Return the node if key is empty (= found)
        if (key.length == 0 || isEmptyNode(node)) {
            return node;
        }

        Value currentNode = this.getNode(node);
        if (currentNode == null) return null;

        if (currentNode.length() == PAIR_SIZE) {
            // Decode the key
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
        Util.writeToFile("root-hash.txt", Hex.toHexString(this.getRoot()));
    }

    private Object insert(Object node, byte[] key, Object value) throws NoSuchAlgorithmException {
        if (key.length == 0) {
            return value;
        }

        if (isEmptyNode(node)) {
            Object[] newNode = new Object[] { packNibbles(key), value };
            this.databaseExposure.put(newNode);
            return newNode;
        }

        Value currentNode = this.getNode(node);

        if (currentNode.length() == PAIR_SIZE) {
            byte[] k = unpackToNibbles(currentNode.get(0).asBytes());
            Object v = currentNode.get(1).asObj();

            if (Arrays.equals(k, key)) {
                Object[] newNode = new Object[] {packNibbles(key), value};
                this.databaseExposure.put(newNode);
                return newNode;
            }

            Object newHash;
            int matchingLength = matchingNibbleLength(key, k);
            if (matchingLength == k.length) {
                // Insert the hash, creating a new node
                byte[] remainingKeypart = copyOfRange(key, matchingLength, key.length);
                newHash = this.insert(v, remainingKeypart, value);

            } else {

                // Expand the 2 length slice to a 17 length slice
                // Create two nodes to putToCache into the new 17 length node
                Object oldNode = this.insert("", copyOfRange(k, matchingLength+1, k.length), v);
                Object newNode = this.insert("", copyOfRange(key, matchingLength+1, key.length), value);

                // Create an expanded slice
                Object[] scaledSlice = emptyStringSlice(17);

                // Set the copied and new node
                scaledSlice[k[matchingLength]] = oldNode;
                scaledSlice[key[matchingLength]] = newNode;
                this.databaseExposure.put(scaledSlice);

                newHash = scaledSlice;
            }

            if (matchingLength == 0) {
                // End of the chain, return
                return newHash;
            } else {
                Object[] newNode = new Object[] { packNibbles(copyOfRange(key, 0, matchingLength)), newHash};
                this.databaseExposure.put(newNode);
                return newNode;
            }
        } else {

            // Copy the current node over to the new node
            Object[] newNode = copyNode(currentNode);
            // Replace the first nibble in the key

            newNode[key[0]] = this.insert(currentNode.get(key[0]).asObj(), copyOfRange(key, 1, key.length), value);
            this.databaseExposure.put(newNode);
            return newNode;
        }

    }

    private Value getNode(Object node) throws NoSuchAlgorithmException {
        Value value = new Value(node);
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] v = value.encode();
        byte[] k = sha256.digest(v);
        Value val = Value.decode(this.databaseExposure.get(k));

        // in that case we got a node
        // so no need to encode it
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

    public byte[] getRoot() throws NoSuchAlgorithmException {
        if (root == null
                || (root instanceof byte[] && ((byte[]) root).length == 0)
                || (root instanceof String && "".equals((String) root))) {
            try{
                return Hex.decode(Util.readFile("root-hash.txt"));
            }catch(Exception ee){
                return new byte[]{};
            }
        } else if (root instanceof byte[]) {
            return (byte[]) this.root;
        } else {
            Value value = new Value(this.root);
            byte[] val = value.encode();
            return Util.getSHA256().digest(val);
        }
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

    private void scanTree(byte[] hash, ScanAction scanAction) {
        byte [] data = this.databaseExposure.get(hash);
        Value node = Value.decode(data);
        if (node == null) return;

        if (node.isList()) {
            List<Object> siblings =  node.asList();
            if (siblings.size() == PAIR_SIZE) {
                Value val = new Value(siblings.get(1));
                if (val.isHashCode())
                    scanTree(val.asBytes(), scanAction);
            } else {
                for (int j = 0; j < LIST_SIZE; ++j) {
                    Value val = new Value(siblings.get(j));
                    if (val.isHashCode())
                        scanTree(val.asBytes(), scanAction);
                }
            }
            scanAction.doOnNode(hash, node);
        }
    }

    public String getTrieDump() throws NoSuchAlgorithmException {

        String root = "";
        TraceAllNodes traceAction = new TraceAllNodes();
        this.scanTree(this.getRoot(), traceAction);

        if (this.root instanceof Value) {
            root = "root: " + Hex.toHexString(getRoot()) +  " => " + this.root +  "\n";
        } else {
            root = "root: " + Hex.toHexString(getRoot()) + "\n";
        }
        return root + traceAction.getOutput();
    }

    public interface ScanAction {
        public void doOnNode(byte[] hash, Value node);
    }
}
