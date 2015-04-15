# seam-carver

Seam-carving is a content-aware image resizing technique where the image is reduced in size by one pixel of height (or width) at a time. A vertical seam in an image is a path of pixels connected from the top to the bottom with one pixel in each row, while a horizontal seam is a path of pixels connected from the left to the right with one pixel in each column. Unlike standard content-agnostic resizing techniques (e.g. cropping and scaling), the most interesting features (aspect ratio, set of objects present, etc.) of the image are preserved.

First, the energy of each pixel is calculated using the dual gradient energy function, and the seams are identified and removed by representing the image as a directed acyclic graph and applying a shortest paths algorithm.
