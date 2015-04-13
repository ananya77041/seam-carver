import java.awt.Color;
import java.util.Iterator;

public class SeamCarver {
	private int[] img;
	private int[] shortestNext;
	private int height;
	private int width;

	public SeamCarver(Picture picture) {
		height = picture.height();
		width = picture.width();

		img = new int[width*height];		

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				img[c2v(i, j)] = picture.get(i, j).getRGB();
			}
		}
	}
	
	private void calcSP() {
		shortestNext = new int[width*height];
		
		for (int i = 0; i < img.length; i++) {
			if (!(i > width*(height-1)-1)) {
				if (i % width == 0) {
					double temp = energy(i+width);
					shortestNext[i] = i+width;
					if (energy(i+width+1) < temp) shortestNext[i] = i+width+1;		
				} else if (i % width == width - 1) {
					double temp = energy(i+width-1);
					shortestNext[i] = i+width-1;
					if (energy(i+width) < temp) shortestNext[i] = i+width;	
				} else {
					double temp = energy(i+width-1);
					shortestNext[i] = i+width-1;
					if (energy(i+width) < temp) { shortestNext[i] = i+width; temp = energy(i+width); }
					if (energy(i+width+1) < temp) shortestNext[i] = i+width+1;
				}				
			}
		}
	}

	private int c2v(int x, int y) {		
		return y*width + x;
	}

	private int v2c(int v) {
		return v % width;
	}

	public Picture picture() {
		Picture image = new Picture(width, height);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				image.set(i, j, new Color(img[c2v(i, j)]));
			}
		}
		return image;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	private void checkBounds(int x, int y) {
		if (x < 0 || x > width()-1 || y < 0 || y > height()-1) {
			throw new java.lang.IndexOutOfBoundsException();
		}
	}

	// energy of pixel at column x and row y
	public double energy(int x, int y) {
		checkBounds(x,y);
		
		// border case
		if (x == 0 || x == width()-1 || y == 0 || y == height()-1) {
			return 195075;
		}
		
		return energy(c2v(x, y));
	}
	
	private double energy(int x) {
		if (x % width == 0 || x % width == width - 1 || x < width || x > width*(height-1)-1) return 195075;
		
		double[] x1 = getRGB(x-1); double[] x2 = getRGB(x+1);	double[] y1 = getRGB(x+width); double[] y2 = getRGB(x-width);
		return dSquared(x1, x2) + dSquared(y1, y2);
	}

	// array of RGB values for pixel at x,y
	private double[] getRGB(int x) {				
		double[] rgbs = new double[3];
		Color color = new Color(img[x]);
		
		rgbs[0] = color.getRed(); rgbs[1] = color.getGreen(); rgbs[2] = color.getBlue();
		return rgbs;
	}

	// calculates Delta^2
	private double dSquared(double[] a, double[] b) {
		return Math.pow(Math.abs(b[0]-a[0]),2) + Math.pow(Math.abs(b[1]-a[1]),2) + Math.pow(Math.abs(b[2]-a[2]),2);	   
	}

	// sequence of indices for horizontal seam
	public int[] findHorizontalSeam() {
		transpose();
		int[] result = findVerticalSeam();
		transpose();
		return result;
	}

	private void transpose() {
		int[] transpose = new int[width*height];
		
		int ind = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				transpose[ind] = img[c2v(i, j)];
				ind++;
			}
		}
		
		img = transpose;
		int temp = width;
		width = height;
		height = temp;
	}

	// sequence of indices for vertical seam
	public int[] findVerticalSeam() {
		calcSP();
		double curMinDist = height*195075;

		int[] seam = new int[height];
		double[] dist = new double[width];
		
		for (int i = 0; i < width; i++) {
			int ind = 0;
			int[] tempseam = new int[height];
			
			for (Integer v = i; v != null && ind < height; v = shortestNext[v]) {
				dist[i] += energy(v);
				tempseam[ind] = v2c(v);	
				ind++;
			}
			
			if (dist[i] < curMinDist) {
				seam = tempseam;
				curMinDist = dist[i];
			}
		}

		return seam;
	}

	// remove horizontal seam from current picture
	public void removeHorizontalSeam(int[] seam) {		
		transpose();
		removeVerticalSeam(seam);
		transpose();
	}

	// remove vertical seam from current picture
	public void removeVerticalSeam(int[] seam) {
		if (seam == null) throw new java.lang.NullPointerException();
		if (seam.length != height || width <= 1) throw new java.lang.IllegalArgumentException();
		checkSeam(seam);
	
		int[] removed = new int[width*height-height];		
		
		int idx = 0, j = 0;		
		for (int i = 0; i < img.length; i++) {			
			if (idx < seam.length && i != seam[idx]+idx*width) {
				removed[j] = img[i];
				j++;
			} else {
				idx++;
			}
		}
		
		width--;
		img = removed;
	}

	private void checkSeam(int[] seam) {
		int x = seam[0];
		for (int i = 0; i < seam.length; i++) {
			if (java.lang.Math.abs(seam[i]-x) > 1) throw new java.lang.IllegalArgumentException();
			x = seam[i];
		}
	}	

	public static void main(String[] args)
	{
		Picture inputImg = new Picture(args[0]);		
		SeamCarver sc = new SeamCarver(inputImg);
		System.out.printf("image is %d pixels wide by %d pixels high.\n", sc.width(), sc.height());
		
		//		for (int i = 0; i < sc.shortestpaths.length; i++) {
		//			for (int j = sc.coordToVert(0, sc.pic.height()-1); j < sc.graph.V(); j++) {
		//				StdOut.println(sc.shortestpaths[i].pathTo(j));
		//			}
		//		}		
	}
}