package gui;

import java.util.Dictionary;
import java.util.Hashtable;

import adaptivehuffman.HuffmanBroadcast;
import adaptivehuffman.Node;

public class GUIBroadcast implements HuffmanBroadcast
{
    private GUINode tree;
    private Dictionary<Integer, Integer> subtreeWidth;
    private Dictionary<Integer, GUINode> nodeNumberToNode;
    private Integer nodeWidth;
    private Integer nodeHeight;
    private Integer sepX;
    private Integer sepY;
    private MainPanel panel;

    public GUIBroadcast(MainPanel panel)
    {
        this.panel = panel;
        this.nodeHeight = panel.nodeHeight;
        this.nodeWidth = panel.nodeWidth;
        this.sepX = panel.sepX;
        this.sepY = panel.sepY;
    }

    public void broadcastSymbol(Character symbol)
    {
        panel.encoded += symbol;
        panel.repaint();
        try
        {
            Thread.sleep(10);
        }
        catch (InterruptedException e)
        {
            System.err.println(e.getStackTrace());
        }
    }

    public void broadcastSwapNodes(Node a, Node b)
    {
        nodeNumberToNode.get(a.getNodeNumber()).highlighted = true;
        nodeNumberToNode.get(b.getNodeNumber()).highlighted = true;
        panel.repaint();
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            System.err.println(e.getStackTrace());
        }
    }

    public void broadcastTree(Node root)
    {
        tree = null;
        subtreeWidth = new Hashtable<Integer, Integer>();
        nodeNumberToNode = new Hashtable<Integer, GUINode>();

        calculateSubtreesWidth(root);

        Integer treeWidth = subtreeWidth.get(root.getNodeNumber()) * (nodeWidth + sepX);
        panel.treeWidth = treeWidth;
        panel.treeHeight = 0;
        tree = buildGUITree(root, 0, treeWidth, 0);

        panel.tree = tree;
        panel.repaint();
        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            System.err.println(e.getStackTrace());
        }
    }

    private GUINode buildGUITree(Node subtree, Integer x1, Integer x2, Integer y)
    {
        GUINode current = new GUINode();
        if (subtree.getLeft() == null) 
        {
            current.left = null;
            current.right = null;
        }
        else
        {
            Integer widthLeft = subtreeWidth.get(subtree.getLeft().getNodeNumber());
            GUINode left = buildGUITree(subtree.getLeft(), x1, x1 + widthLeft * (nodeWidth + sepX), y + nodeHeight + sepY);
            GUINode right = buildGUITree(subtree.getRight(), x1 + widthLeft * (nodeWidth + sepX), x2, y + nodeHeight + sepY);
            current.left = left;
            current.right = right;
        }
        current.frequency = subtree.getFrequency();
        current.nodeNumber = subtree.getNodeNumber();
        current.symbol = subtree.getSymbol();
        current.x = (x1 + x2) / 2;
        current.y = y;
        current.nyt = subtree.getSymbol() == null && subtree.getLeft() == null;
        current.highlighted = false;
        panel.treeHeight = Math.max(panel.treeHeight, y + nodeHeight + sepY);
        nodeNumberToNode.put(current.nodeNumber, current);
        return current;
    }

    private void calculateSubtreesWidth(Node subtree)
    {
        if (subtree.getLeft() == null) 
        {
            subtreeWidth.put(subtree.getNodeNumber(), 1);
            return;
        }
        calculateSubtreesWidth(subtree.getLeft());
        calculateSubtreesWidth(subtree.getRight());
        subtreeWidth.put(subtree.getNodeNumber(), subtreeWidth.get(subtree.getLeft().getNodeNumber()) + subtreeWidth.get(subtree.getRight().getNodeNumber())); 
    }
}
