package trie;

public class Node {

    private Edge leftEdge;
    private Edge rightEdge;

    public Node(){

    }
    public Node(Edge leftEdge, Edge rightEdge){
        this.leftEdge = leftEdge;
        this.rightEdge = rightEdge;
    }

    public Edge getLeftNode() {
        return leftEdge;
    }

    public Edge getRightNode() {
        return rightEdge;
    }
}
