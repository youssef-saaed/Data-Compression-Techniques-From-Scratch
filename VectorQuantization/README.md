# Vector Quantization Image Compression

## Brief Description

**Vector Quantization (VQ)** is a lossy image compression technique that reduces image size by representing image blocks with a limited set of representative vectors known as a *codebook*. The main idea is to divide an image into fixed-size blocks, encode each block by finding the nearest codebook vector, and store the indices instead of the raw pixel data.

This Java code implements VQ to compress and decompress RGB images. The compression process generates a codebook of size `k`, while the block size `s` determines the resolution of image patches. By adjusting `k` and `s`, users can explore the trade-off between image quality and compression ratio.

---

## Command Line API

### Compression

**Command:**

```bash
java VectorQuantization.java compress [original file name] [compressed file name] [number of codebook] [block size]
```

**Arguments:**

- `[original file name]` – The path to the input image to be compressed.
- `[compressed file name]` – The path where the compressed binary output should be saved.
- `[number of codebook]` – The number of vectors in the codebook (`k`). A higher value improves image quality but increases file size.
- `[block size]` – The size `s` of the square image blocks (e.g., 3 for 3×3 blocks).

---

### Decompression

**Command:**

```bash
java VectorQuantization.java decompress [compressed file name] [decompressed file name]
```

**Arguments:**

- `[compressed file name]` – The path to the previously generated compressed file.
- `[decompressed file name]` – The path where the reconstructed image will be saved.

---

### Help

**Command:**

```bash
java VectorQuantization.java help
```

**Description:**

Displays usage instructions and command syntax for compression and decompression.

---

## Examples of Reconstructed Images

The following images demonstrate how different codebook sizes (`k`) and block sizes (`s`) affect image quality:

### Original Image

![Original Image](examples/image.jpg)

This is the original image before compression.

---

### Reconstruction with k=3, s=3

![k=3, s=3](examples/k3s3.jpg)

---

### Reconstruction with k=7, s=3

![k=7, s=3](examples/k7s3.jpg)

---

### Reconstruction with k=31, s=3

![k=31, s=3](examples/k31s3.jpg)

---

### Reconstruction with k=31, s=10

![k=31, s=10](examples/k31s10.jpg)

---

### Reconstruction with k=31, s=30

![k=31, s=30](examples/k31s30.jpg)

---

### Reconstruction with k=255, s=3

![k=255, s=3](examples/k255s3.jpg)

---

### Trade-Off Discussion

In Vector Quantization:

- **Larger `k` (codebook size)** improves fidelity but increases storage size and computational time.
- **Larger `s` (block size)** results in higher compression but often leads to a loss of fine details.

Optimal values depend on the application's storage constraints and quality requirements.

---

Feel free to experiment with different `k` and `s` values to better understand how they affect image compression and quality.
