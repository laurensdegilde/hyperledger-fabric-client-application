package trie;

import org.bouncycastle.util.encoders.Hex;
import util.NibbleEncoder;

import static java.util.Arrays.copyOfRange;
import static util.NibbleEncoder.binToNibbles;
import static util.NibbleEncoder.packNibbles;
import static util.NibbleEncoder.unpackToNibbles;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Trie {

    private static byte PAIR_SIZE = 2;
    private static byte LIST_SIZE = 17;

    private Object root;
    private DatabaseExposure databaseExposure;

    public Trie(Object root) throws IOException {
        this.root = root;
        this.databaseExposure = new DatabaseExposure();
    }

    public void setRoot(Object root) {
        this.root = root;
    }

    public byte[] get(String key) throws NoSuchAlgorithmException {
        return this.get(key.getBytes());
    }

    public byte[] get(byte[] key) throws NoSuchAlgorithmException {
        byte[] k = binToNibbles(key);
        Value c = new Value(this.get(this.root, k));
        return (c == null)? null : c.asBytes();
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


    public void insert(String key, String value) throws NoSuchAlgorithmException {
        byte[] k = binToNibbles(key.getBytes());
        this.root = this.insert(this.root, k, value);
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
        Value val = Value.fromRlpEncoded(this.databaseExposure.get(k));

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

    // Simple compare function which compares two tries based on their stateRoot
    private Object[] emptyStringSlice(int l) {
        Object[] slice = new Object[l];
        for (int i = 0; i < l; i++) {
            slice[i] = "";
        }
        return slice;
    }
    public byte[] getRootHash() throws NoSuchAlgorithmException {
        if (root == null
                || (root instanceof byte[] && ((byte[]) root).length == 0)
                || (root instanceof String && "".equals((String) root))) {
            return NibbleEncoder.EMPTY_BYTE_ARRAY;
        } else if (root instanceof byte[]) {
            return (byte[]) this.root;
        } else {
            Value rootValue = new Value(this.root);
            byte[] val = rootValue.encode();
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return sha256.digest(val);
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
        Value node = Value.fromRlpEncoded(data);
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
        this.scanTree(this.getRootHash(), traceAction);

        if (this.root instanceof Value) {
            root = "root: " + Hex.toHexString(getRootHash()) +  " => " + this.root +  "\n";
        } else {
            root = "root: " + Hex.toHexString(getRootHash()) + "\n";
        }
        return root + traceAction.getOutput();
    }

    public interface ScanAction {
        public void doOnNode(byte[] hash, Value node);
    }
}
