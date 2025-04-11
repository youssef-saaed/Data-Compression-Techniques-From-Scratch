package adaptivehuffman;

public class Node 
{
    private Character symbol;   
    private String code;
    private Integer nodeNumber;
    private Integer frequency;
    private Node parent;
    private Node left;
    private Node right;

    public Node(Integer nodeNumber, Character symbol, Node parent)
    {
        left = null;
        right = null;
        this.parent = parent;
        this.symbol = symbol;
        this.nodeNumber = nodeNumber;

        Character postfix; // The code ending if it is left node it will be 0 otherwise 1

        if (symbol != null) // In adaptive huffman we create right node with symbol (symbol != null) and the left one without symbol (NYT)
        {
            frequency = 1;
            postfix = '1';
        }
        else
        {
            frequency = 0;
            postfix = '0';
        }

        // Generate current node code by appending the postfix(0 or 1) to parent node code
        if (parent != null)
        {
            this.code = parent.code + postfix;
            if (postfix == '0')
            {
                parent.left = this;
            }
            else
            {
                parent.right = this;
            }
        }
        else
        {
            this.code = ""; // root node code
        }
    }

    public Node(Integer nodeNumber, Node parent) 
    {
        this(nodeNumber, null, parent);
    }

    public Node(Integer nodeNumber)
    {
        this(nodeNumber, null);
    }

    private static void updateCodes(Node node, Character postfix)
    {
        // Update codes recursively from a selected node (Mainly used after swapping)
        if (node == null) return;
        node.code = node.parent.code + postfix;
        updateCodes(node.left, '0');
        updateCodes(node.right, '1');
    }

    public static void swap(Node a, Node b)
    { 
        // Swap node numbers
        Integer nodeNumberA = a.nodeNumber, nodeNumberB = b.nodeNumber;
        b.nodeNumber = nodeNumberA;
        a.nodeNumber = nodeNumberB;
        
        // Swapping parent links
        Node parentA = a.parent, parentB = b.parent;
        a.parent = parentB;
        b.parent = parentA;

        // Swapping children of parent nodes and update subtree nodes code
        char endA = a.code.charAt(a.code.length() - 1), endB = b.code.charAt(b.code.length() - 1);

        if (endB == '0')
        {
            a.parent.left = a;
            updateCodes(a, '0');
        }
        else
        {
            a.parent.right = a;
            updateCodes(a, '1');
        }

        if (endA == '0')
        {
            b.parent.left = b;
            updateCodes(b, '0');
        }
        else
        {
            b.parent.right = b;
            updateCodes(b, '1');
        }
    }

    public Node getLeft()
    {
        return left;
    }

    public Node getRight()
    {
        return right;
    }

    public Node getParent()
    {
        return parent;
    }

    public static Boolean swapable(Node a, Node b)
    {
        // Checking the ability to swap between two nodes ensuring higher or equal frequency, a higher node number and not being parent or ancestors (no common code prefix)
        Integer i = 0;
        while (i < a.code.length() && i < b.code.length())
        {
            if (a.code.charAt(i) != b.code.charAt(i))
            {
                return a.frequency >= b.frequency && b.nodeNumber > a.nodeNumber;
            }
            ++i;
        }
        return false;
    }

    public Integer getNodeNumber()
    {
        return nodeNumber;
    }
    
    public void incrementFrequency()
    {
        ++frequency;
    }

    public String getCode()
    {
        return code;
    }

    public Character getSymbol()
    {
        return symbol;
    }

    public Integer getFrequency()
    {
        return frequency;
    }

    public String toString()
    {
        return "Symbol: " + symbol + ", Node Number: " + nodeNumber + ", Frequency: " + frequency + ", Code: " + code;
    }
}
