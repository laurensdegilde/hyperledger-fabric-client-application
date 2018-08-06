package trie;

import network.NetworkExposure;

import static java.util.Arrays.copyOfRange;
import static org.bouncycastle.pqc.math.linearalgebra.ByteUtils.concatenate;
import static util.CompactEncoder.binToNibbles;
import static util.CompactEncoder.packNibbles;
import static util.CompactEncoder.unpackToNibbles;

import java.util.*;

public class TrieParser {

    private static byte PAIR_SIZE = 2;
    private static byte LIST_SIZE = 17;

    private Object root;

    public TrieParser(Object root) {
        this.root = root;
    }

    public byte[] get(String key) {
        return this.get(key.getBytes());
    }

    public byte[] get(byte[] key) {
        byte[] k = binToNibbles(key);
        Node c = new Node(this.get(this.root, k));

        return (c == null)? null : c.asBytes();
    }

    public void insert(String key, String value) {
        byte[] k = binToNibbles(key.getBytes());
        Object temp = this.insert(this.root, k, value);

        this.root = temp;

        System.out.println();
    }

    private Object get(Object node, byte[] key) {

        // Return the node if key is empty (= found)
        if (key.length == 0 || isEmptyNode(node)) {
            return node;
        }

        Node currentNode = this.getNode(node);
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


    private Object insert(Object node, byte[] key, Object value) {
        if (key.length == 0) {
            return value;
        }

        if (isEmptyNode(node)) {
            Object[] newNode = new Object[] { packNibbles(key), value };
            return newNode;
        }

        Node currentNode = this.getNode(node);

        if (currentNode.length() == PAIR_SIZE) {
            byte[] k = unpackToNibbles(currentNode.get(0).asBytes());
            Object v = currentNode.get(1).asObj();

            if (Arrays.equals(k, key)) {
                Object[] newNode = new Object[] {packNibbles(key), value};
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
                newHash = scaledSlice;
            }

            if (matchingLength == 0) {
                // End of the chain, return
                return newHash;
            } else {
                Object[] newNode = new Object[] { packNibbles(copyOfRange(key, 0, matchingLength)), newHash};
                return newNode;
            }
        } else {

            // Copy the current node over to the new node
            Object[] newNode = copyNode(currentNode);

            // Replace the first nibble in the key
            newNode[key[0]] = this.insert(currentNode.get(key[0]).asObj(), copyOfRange(key, 1, key.length), value);
            return newNode;
        }
    }

    private Object delete(Object node, byte[] key) {

        if (key.length == 0 || isEmptyNode(node)) {
            return "";
        }

        // New node
        Node currentNode = this.getNode(node);
        // Check for "special" 2 slice type node
        if (currentNode.length() == PAIR_SIZE) {
            // Decode the key
            byte[] k = unpackToNibbles(currentNode.get(0).asBytes());
            Object v = currentNode.get(1).asObj();

            // Matching key pair (ie. there's already an object with this key)
            if (Arrays.equals(k, key)) {
                return "";
            } else if (Arrays.equals(copyOfRange(key, 0, k.length), k)) {
                Object hash = this.delete(v, copyOfRange(key, k.length, key.length));
                Node child = this.getNode(hash);

                Object newNode;
                if (child.length() == PAIR_SIZE) {
                    byte[] newKey = concatenate(k, unpackToNibbles(child.get(0).asBytes()));
                    newNode = new Object[] {packNibbles(newKey), child.get(1).asObj()};
                } else {
                    newNode = new Object[] {currentNode.get(0).asString(), hash};
                }
                return newNode;
            } else {
                return node;
            }
        } else {
            // Copy the current node over to a new node
            Object[] itemList = copyNode(currentNode);

            // Replace the first nibble in the key
            itemList[key[0]] = this.delete(itemList[key[0]], copyOfRange(key, 1, key.length));

            byte amount = -1;
            for (byte i = 0; i < LIST_SIZE; i++) {
                if (itemList[i] != "") {
                    if (amount == -1) {
                        amount = i;
                    } else {
                        amount = -2;
                    }
                }
            }

            Object[] newNode = null;
            if (amount == 16) {
                newNode = new Object[] { packNibbles(new byte[] {16} ), itemList[amount]};
            } else if (amount >= 0) {
                Node child = this.getNode(itemList[amount]);
                if (child.length() == PAIR_SIZE) {
                    key = concatenate(new byte[]{amount}, unpackToNibbles(child.get(0).asBytes()));
                    newNode = new Object[] {packNibbles(key), child.get(1).asObj()};
                } else if (child.length() == LIST_SIZE) {
                    newNode = new Object[] { packNibbles(new byte[]{amount}), itemList[amount]};
                }
            } else {
                newNode = itemList;
            }
            return newNode;
        }
    }

    private Node getNode(Object node) {
        Node val = new Node(node);

        // in that case we got a node
        // so no need to encode it
        if (!val.isBytes()) {
            return val;
        }

        byte[] keyBytes = val.asBytes();
        if (keyBytes.length == 0) {
            return val;
        } else if (keyBytes.length < 32) {
            return new Node(keyBytes);
        }
        return null;
    }


    private boolean isEmptyNode(Object node) {
        Node n = new Node(node);
        return (node == null || (n.isString() && (n.asString() == "" || n.get(0).isNull())) || n.length() == 0);
    }

    private Object[] copyNode(Node currentNode) {
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
}
