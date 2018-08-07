package trie;

public class Node {

    /* RLP encoded value of the Trie-node */
    private Value value;

    public Node(Value val) {
        this.value = val;
    }

    public Node copy() {
        return new Node(this.value);
    }

    public Value getValue() {
        return value;
    }
}