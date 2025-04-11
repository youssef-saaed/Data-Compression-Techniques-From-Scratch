package adaptivehuffman;

public class HuffmanTree 
{
    private Integer nextNodeNumber;
    private Node tree;
    private Node nyt;
    private Node[] asciiToNode, nodeNumberToNode;
    private HuffmanBroadcast broadcast; // A broadcasting object that send important updates in tree (ex. to be usee for visualization)

    public HuffmanTree()
    {
        nextNodeNumber = 256;
        tree = new Node(nextNodeNumber--); // Root NYT Node
        nyt = tree;
        asciiToNode = new Node[128];
        nodeNumberToNode = new Node[257];
        
        // Intializing mapping arrays to null
        for (Integer i = 0; i < 128; ++i)
        {
            asciiToNode[i] = null;
        }
        for (Integer i = 0; i < 257; ++i)
        {
            nodeNumberToNode[i] = null;
        }

        nodeNumberToNode[256] = nyt;
        broadcast = null;
    }    

    public HuffmanTree(HuffmanBroadcast broadcast)
    {
        this();
        this.broadcast = broadcast;
        broadcast.broadcastTree(tree);
    }

    private void updateTree(Node node)
    {
        Integer oldNodeNumber = node.getNodeNumber(), newNodeNumber = oldNodeNumber;

        // Finding node that satisfy swapping condition with highest node number
        for (int i = node.getNodeNumber() + 1; i < 257; ++i)
        {
            if (Node.swapable(node, nodeNumberToNode[i]))
            {
                newNodeNumber = i;
            }
        }

        if (newNodeNumber != oldNodeNumber) // A swapping node is found
        {
            if (broadcast != null)
            {
                broadcast.broadcastSwapNodes(node, nodeNumberToNode[newNodeNumber]);
            }
            Node.swap(node, nodeNumberToNode[newNodeNumber]);
            nodeNumberToNode[oldNodeNumber] = nodeNumberToNode[newNodeNumber];
            nodeNumberToNode[newNodeNumber] = node;
        }
        node.incrementFrequency();
    }

    public void add(Character c)
    {
        if (broadcast != null)
        {
            broadcast.broadcastSymbol(c);
        }
        Node current;
        if (asciiToNode[(int)c] != null) // Existing symbol
        {
            current = asciiToNode[(int)c];
        }
        else // New symbol
        {
            Node newNode = new Node(nextNodeNumber--, c, nyt); // New symbol node
            Node newNYT = new Node(nextNodeNumber--, nyt); // New NYT node

            nyt.incrementFrequency();

            asciiToNode[(int)c] = newNode;
            nodeNumberToNode[newNode.getNodeNumber()] = newNode;
            nodeNumberToNode[newNYT.getNodeNumber()] = newNYT;

            current = nyt.getParent();
            nyt = newNYT;
        }
        if (broadcast != null)
        {
            broadcast.broadcastTree(tree);
        }

        // Updating tree bottom up
        while (current != null)
        {
            updateTree(current);
            current = current.getParent();
            if (broadcast != null)
            {
                broadcast.broadcastTree(tree);
            }
        }
    }

    public Boolean exist(Character c)
    {
        return asciiToNode[(int)c] != null;
    }

    public String getCode(Character c)
    {
        if (!exist(c)) return "";
        return asciiToNode[(int)c].getCode();
    }

    public String getNYTCode()
    {
        return nyt.getCode();
    }

    public String toString()
    {
        String tree = "";
        for (int i = 0; i < 256; ++i)
        {
            if (nodeNumberToNode[i] != null) tree += nodeNumberToNode[i].toString() + '\n';
        }
        tree += nodeNumberToNode[256].toString();
        return tree;
    }

    public String getNextCode(String str)
    {
        // Segment the next huffman code from long binary string and return it
        Node current = tree;
        int i = 0;
        while (i < str.length() && current != null)
        {
            if (current.getLeft() == null)
            {
                return current.getCode();
            }
            if (str.charAt(i) == '0') 
            {
                current = current.getLeft();
            }
            else 
            {
                current = current.getRight();
            }
            ++i;
        }
        return "";
    }

    public Character getSymbol(String code)
    {
        // Traverse tree to get symbol using the huffman code
        Node current = tree;
        int i = 0;
        while (i < code.length() && current != null)
        {
            if (code.charAt(i) == '0') 
            {
                current = current.getLeft();
            }
            else 
            {
                current = current.getRight();
            }
            if (current.getLeft() == null)
            {
                return current.getSymbol();
            }
            ++i;
        }
        return '\0';
    }
}
