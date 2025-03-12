# Standard Huffman Code Compression

## Introduction

Huffman coding is a widely used lossless data compression algorithm. In this implementation, I provided standard Huffman coding from scratch in Java, covering the entire process of compression and decompression.

## Objectives

- Implement Huffman coding for file compression and decompression.
- Provide an efficient and modular Java implementation.
- Evaluate performance through examples.

## API Usage

### Compression

```sh
java Huffman.java compress input.txt output.bin
```

### Decompression

```sh
java Huffman.java decompress output.bin decompressed.txt
```

## Implementation

The implementation is modular and consists of the following classes:

- `Compressor`: Handles file reading, Huffman tree construction, encoding, entropy calculation, and binary file writing.
- `Decompressor`: Reads the binary file, extracts mappings, decodes the text, and writes it back.
- `TreeNode`: Represents nodes in the Huffman tree, including comparison operations for priority queues.
- `Huffman`: Main class that provides an API for compression and decompression.

## Examples

### Example 1

**Title:** Short example

#### Example 1 Screenshot

![Example 1](assets/testcase1.png)

#### Compression Info for Example 1

![Compression Info 1](assets/compression1.png)

### Example 2

**Title:** Medium example

#### Example 2 Screenshot

![Example 2](assets/testcase2.png)

#### Compression Info for Example 2

![Compression Info 2](assets/compression2.png)

### Example 3

**Title:** Very long example

#### Example 3 Screenshot

![Example 3](assets/testcase3.png)

#### Compression Info for Example 3

![Compression Info 3](assets/compression3.png)

## Conclusion

This implementation of Huffman coding provides an efficient approach to text compression and decompression. By evaluating examples, the algorithm's effectiveness in reducing file size while maintaining data integrity is demonstrated especially when increasing file size.

