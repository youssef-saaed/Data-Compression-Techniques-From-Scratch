package huffman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PriorityQueue;

public class Compressor 
{
    private String text;
    private Integer textSize; 
    private BitSet compressedBits;
    private Integer bitSetPtr; // Pointer to track the current bit position in the bit set

    public Compressor(String textOrFile, Boolean fromFile)
    {
        if (fromFile)
        {
            try
            {
                text = Files.readString(Path.of(textOrFile));
            }
            catch (IOException e)
            {
                System.err.println(e.getStackTrace());
            }
        }
        else 
        {
            text = textOrFile;
        }
        text = enforceAscii(text) + '\0'; // Convert text to ASCII and append null character
        textSize = text.length();
        compressedBits = new BitSet();
        bitSetPtr = 0;
    }

    private String enforceAscii(String text)
    {
        StringBuilder builder = new StringBuilder(text);
        for (Integer i = 0; i < builder.length(); ++i)
        {
            builder.setCharAt(i, (char)(byte)builder.charAt(i)); // Ensure all characters are ASCII by clipping their values
        }
        return builder.toString();
    }

    private Dictionary<Character, Integer> calculateFrequency()
    {
        Dictionary<Character, Integer> frequencyDictionary = new Hashtable<Character, Integer>();
        for (Integer i = 0; i < textSize; ++i)
        {
            Character c = text.charAt(i);
            Integer lastFrequency = frequencyDictionary.get(c);
            if (lastFrequency == null)
            {
                lastFrequency = 0; // Default frequency is 0 if character is not found
            }
            frequencyDictionary.put(c, lastFrequency + 1); // Increment character frequency
        }
        return frequencyDictionary;
    }

    private Double calculateEntropy(Dictionary<Character, Integer> frequencies)
    {
        Double entropy = 0.;
        Enumeration<Character> keys = frequencies.keys();
        while (keys.hasMoreElements())
        {
            Character symbol = keys.nextElement();
            Double probability = (double)frequencies.get(symbol) / textSize;
            entropy += probability * Math.log10(1 / probability) / Math.log10(2); // H(x) = ∑ P(x) Log_2(1 / P(x)) 
        }
        return entropy;
    }

    private PriorityQueue<TreeNode> constructTreeNodesQueue(Dictionary<Character, Integer> frequencies)
    {
        PriorityQueue<TreeNode> queue = new PriorityQueue<TreeNode>();
        Enumeration<Character> keys = frequencies.keys();

        // Append each symbol along with its frequency and put the in huffman priority queue 
        while (keys.hasMoreElements())
        {
            Character symbol = keys.nextElement();
            Integer frequency = frequencies.get(symbol);
            queue.add(new TreeNode(symbol, frequency)); 
        }
        return queue;
    }

    private TreeNode constructHuffmanTree(Dictionary<Character, Integer> frequencies)
    {
        PriorityQueue<TreeNode> queue = constructTreeNodesQueue(frequencies); 
        while (queue.size() > 1)
        {
            TreeNode nodeA = queue.poll(), nodeB = queue.poll(); // Get two least frequent nodes
            queue.add(new TreeNode(nodeA, nodeB)); // Merge them into a new node
        }
        return queue.poll(); // Root of the Huffman tree
    }

    private void getCodesRecursively(TreeNode node, String code, Dictionary<Character, String> codeDictionary)
    {
        Character symbol = node.getSymbol();
        if (symbol != null)
        {
            codeDictionary.put(symbol, code); // Store code for leaf node that contain symbol (character)
            return;
        }
        getCodesRecursively(node.getLeft(), code + '0', codeDictionary); // Traverse left with '0'
        getCodesRecursively(node.getRight(), code + '1', codeDictionary); // Traverse right with '1'
    }

    private void writeToCompressedBits(String bits)
    {
        // Loop over binary string and put its values into the bit set 
        for (int j = 0; j < bits.length(); ++j)
        {
            compressedBits.set(bitSetPtr, bits.charAt(j) == '1');
            ++bitSetPtr;
        }
    }

    // Pad binary string with leading zeros
    private String padBinaryString(String string, Integer size)
    {
        while (string.length() < size)
        {
            string = '0' + string;
        }
        return string;
    }

    private void encodeHeader(Dictionary<Character, String> codeDictionary)
    {
        Integer headerSize = codeDictionary.size();
        String headerSizeString = Integer.toBinaryString(headerSize);
        headerSizeString = padBinaryString(headerSizeString, 32); // Ensure 32-bit representation
        writeToCompressedBits(headerSizeString); // Write in header the number of huffman code blocks (32 bits) 
        
        Enumeration<Character> keys = codeDictionary.keys();
        while (keys.hasMoreElements())
        {
            Character symbol = keys.nextElement();
            String code = codeDictionary.get(symbol);

            Byte codeLength = (byte)code.length();
            String codeLengthString = Integer.toBinaryString(codeLength);
            codeLengthString = padBinaryString(codeLengthString, 8); // Store length in 8 bits
            
            String symbolString = Integer.toBinaryString(symbol);
            symbolString = padBinaryString(symbolString, 8); // Store ASCII symbol in 8 bits
            
            // Write in the header (huffman code length (8 bits) + ascii code (8 bits) + huffman code (dynamic))
            writeToCompressedBits(codeLengthString);
            writeToCompressedBits(symbolString);
            writeToCompressedBits(code);
        }
    }

    private void encodeText(Dictionary<Character, String> codeDictionary)
    {
        // Loop over text symbols and write their huffman codes in the bit set
        for (int i = 0; i < textSize; ++i)
        {
            String code = codeDictionary.get(text.charAt(i));
            writeToCompressedBits(code);
        }
    }

    private void displayEntropy(Dictionary<Character, Integer> frequencies)
    {
        Double textEntropy = calculateEntropy(frequencies);
        System.out.println("Entropy: %.2f bits/symbol\n".formatted(textEntropy)); // Print entropy value
    }

    private void displayCompressionInfo(Integer headerSize)
    {
        System.out.println("Original Size: %d Bits".formatted(textSize * 8));
        System.out.println("Compressed Size: %d Bits".formatted(bitSetPtr));
        System.out.println("\tHeader -> %d Bits".formatted(headerSize));
        System.out.println("\tContent -> %d Bits".formatted(bitSetPtr - headerSize));
        System.out.println("Compression Ratio: %.2f%%\n".formatted(bitSetPtr / (textSize * 8.) * 100));
    }

    public void compress()
    {
        compressedBits.clear();
        bitSetPtr = 0;

        Dictionary<Character, Integer> frequencies = calculateFrequency();

        displayEntropy(frequencies);
        
        TreeNode huffmanTree = constructHuffmanTree(frequencies);
        Dictionary<Character, String> codeDictionary = new Hashtable<Character, String>();
        getCodesRecursively(huffmanTree, "", codeDictionary);

        encodeHeader(codeDictionary);
        Integer headerSize = bitSetPtr;
        encodeText(codeDictionary);
        displayCompressionInfo(headerSize);
    }

    public void save(String location)
    {
        if (bitSetPtr == 0) return;

        try
        {
            Files.write(Path.of(location), compressedBits.toByteArray());
            System.out.println("Compressed binary file saved at '%s'".formatted(location));
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
    }
}
