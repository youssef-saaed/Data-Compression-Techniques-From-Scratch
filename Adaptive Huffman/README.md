# Adaptive Huffman Compression

This project implements the adaptive Huffman compression algorithm, providing both command-line and GUI interfaces for encoding and decoding text.

## Table of Contents

- [Project Description](#project-description)
- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Running the Application](#running-the-application)
- [Usage](#usage)
  - [Command-Line Interface](#command-line-interface)
  - [Graphical User Interface](#graphical-user-interface)
- [Code Structure](#code-structure)
- [Examples](#examples)
- [Test Cases](#test-cases)

## Project Description

This project provides an implementation of the adaptive Huffman compression algorithm. Adaptive Huffman coding differs from standard Huffman coding in that it dynamically builds the Huffman tree as it processes the input data. This allows it to adapt to changing symbol frequencies, making it more efficient for data streams where symbol frequencies are not known in advance.

## Features

- **Adaptive Huffman Encoding:** Compresses text files using the adaptive Huffman algorithm.
- **Adaptive Huffman Decoding:** Decompresses files encoded with the adaptive Huffman algorithm.
- **Command-Line Interface:** Provides a simple command-line interface for encoding and decoding files.
- **Graphical User Interface (GUI):** Visualizes the construction of the adaptive Huffman tree during encoding.
- **JUnit Tests:** Includes JUnit tests to ensure the correctness of the encoding and decoding implementation.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or later.
- A Java IDE or text editor (optional).
- JUnit 4 (for running tests).

### Installation

1.  Clone the repository:

    ```bash
    git clone [repository URL]
    ```

2.  Navigate to the project directory:

    ```bash
    cd [project directory]
    ```

### Running the Application

#### Command-Line Interface

To use the command-line interface, navigate to the project's root directory in your terminal.

-   **Help:**

    ```bash
    java AdaptiveHuffman help
    ```

-   **Encoding:**

    ```bash
    java AdaptiveHuffman encode [original file name] [encoded file name]
    ```

-   **Decoding:**

    ```bash
    java AdaptiveHuffman decode [encoded file name] [decoded file name]
    ```

#### Graphical User Interface

To use the GUI, navigate to the project's root directory in your terminal.

-   **Visualization:**

    ```bash
    java AdaptiveHuffmanGUI [text file name]
    ```

## Usage

### Command-Line Interface

The command-line interface allows you to encode and decode text files using the adaptive Huffman algorithm.

### Graphical User Interface

The GUI visualizes the construction of the adaptive Huffman tree for the first 20 characters of the input text file.

## Code Structure

-   `adaptivehuffman/`: Contains the core adaptive Huffman encoding and decoding logic.
    -   `Decoder.java`: Implements the adaptive Huffman decoding algorithm.
    -   `Encoder.java`: Implements the adaptive Huffman encoding algorithm.
    -   `HuffmanBroadcast.java`: Defines an interface for broadcasting Huffman tree changes.
    -   `HuffmanTree.java`: Manages the adaptive Huffman tree.
    -   `Node.java`: Represents a node in the Huffman tree.
-   `gui/`: Contains the GUI-related classes.
    -   `GUIBroadcast.java`: Implements the `HuffmanBroadcast` interface for GUI updates.
    -   `GUIComponents.java`: Provides static methods for drawing GUI components.
    -   `GUINode.java`: Represents a node for GUI display.
    -   `MainFrame.java`: Sets up the main GUI window.
    -   `MainPanel.java`: Draws the Huffman tree and encoded text.
-   `testing/`: Contains JUnit tests.
    -   `AdaptiveHuffmanTest.java`: Tests the encoding and decoding functionality.
-   `AdaptiveHuffman.java`: Main entry point for the command-line application.
-   `AdaptiveHuffmanGUI.java`: Main entry point for the GUI application.

## Examples

The `examples/` directory contains sample text files and their corresponding encoded and decoded versions.

-   `example_1.txt`: A sample text file.
-   `example_1_encoded.bin`: The encoded version of `example_1.txt`.
-   `example_1_decoded.txt`: The decoded version of `example_1_encoded.bin`.
-   `example_2.txt`, `example_2_encoded.bin`, `example_2_decoded.txt`
-   `example_3.txt`, `example_3_encoded.bin`, `example_3_decoded.txt`
-   `example_4.txt`, `example_4_encoded.bin`, `example_4_decoded.txt`
-   `example_5.txt`, `example_5_encoded.bin`, `example_5_decoded.txt`

These examples can be used to test the encoding and decoding functionality of the application.

## Test Cases

The `testing/AdaptiveHuffmanTest.java` file contains JUnit tests for the encoding and decoding functionality.

**Encoding Examples:**

-   **Test Case 1:**
    -   Original String: `ABCCCAAAA`
    -   Binary String (Result): `010000010010000100001000011101000101110`
-   **Test Case 2:**
    -   Original String: `ABABABABAAA`
    -   Binary String (Result): `01000001001000010101101101111`

**Decoding Examples:**

-   **Test Case 1:**
    -   Binary String: `0100000100100001000010000111010001011100`
    -   Decoded String: `ABCCCAAAA`
-   **Test Case 2:**
    -   Binary String: `01000001001000010101101101111000`
    -   Decoded String: `ABABABABAAA`