package aishooter;

public final class Node {
    private final Cell cell;
    private final Node parent;

    public Node(Cell cell, Node parent) {
        this.cell = cell;
        this.parent = parent;
    }

    public Cell cell() {
        return cell;
    }

    public Node parent() {
        return parent;
    }
}
