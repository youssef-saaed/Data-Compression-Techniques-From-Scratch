package huffman;

public class TreeNode implements Comparable<TreeNode>
{
    private Character symbol;
    private Integer frequency;
    private TreeNode left, right;

    public TreeNode(Character symbol, Integer frequency)
    {
        this.symbol = symbol;
        this.frequency = frequency;
        left = null;
        right = null;
    }

    public TreeNode()
    {
        this(null, -1);
    }

    public TreeNode(TreeNode left, TreeNode right)
    {
        this(null, left.frequency + right.frequency);
        this.left = left;
        this.right = right;
    }

    // Compares nodes based on frequency (used for priority queue ordering)
    public int compareTo(TreeNode node)   
    {
        return this.frequency - node.frequency;
    } 

    public Character getSymbol()
    {
        return symbol;
    }

    public TreeNode getLeft()
    {
        return left;
    }

    public TreeNode getRight()
    {
        return right;
    }

    public void setSymbol(Character symbol)
    {
        if (symbol < 0) symbol = ' ';
        this.symbol = symbol;
    }

    public void setLeft(TreeNode node)
    {
        left = node;
    }

    public void setRight(TreeNode node)
    {
        right = node;
    }

     // Returns a string representation of the node for debugging
    public String toString()
    {
        return "(Symbol:" + symbol + ", Frequency:" + frequency + ')';
    }
}
