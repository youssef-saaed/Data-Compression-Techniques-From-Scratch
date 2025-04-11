package adaptivehuffman;

public interface HuffmanBroadcast {
    public void broadcastSymbol(Character symbol);
    public void broadcastSwapNodes(Node a, Node b);
    public void broadcastTree(Node root);
}
