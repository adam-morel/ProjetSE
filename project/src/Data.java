
/* Data class
 *
 * for SE project 2019
 *
 * AM
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Data {
    // the data are stored as lists of bytes
    private ArrayList<Byte> data;

    // Data constructor: it generates a random Data object consisting of n bytes,
    //                   and with probability p in [0,1] to have bits equal to 1
    public Data(int n, double p) {
        try {
            if (n <= 0) throw new Exception("Specified size for random Data object is zero or even negative");
            if (p < 0.0 || p > 1.0)
                throw new Exception("Specified probability of 1 bits in random Data object should be contained in [0,1]");

            Random r = new Random();
            this.data = new ArrayList<Byte>(n);
            for (int i = 0; i < n; i++) {
                int bi = 0;
                for (int j = 0; j < 8; j++) {
                    if (r.nextDouble() < p) bi = bi + 1;
                    bi = bi << 1;
                }
                byte b = (byte) bi;
                this.data.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Data constructor: it generates a random Data object consisting of n bytes,
    //                   with equal probability to have bits set to 0 or 1
    public Data(int n) {
        this(n, 0.5);
    }

    // Data constructor: it generates a Data object from an ArrayList of Objects
    //                   only Byte and Integer objects may be contained in the ArrayList
    public Data(ArrayList<Object> list) {
        try {
            int n = list.size();
            if (n == 0) throw new Exception("Specified argument for Data constructor is an empty ArrayList");
            this.data = new ArrayList<Byte>();

            for (int i = 0; i < n; i++) {
                Object o = list.get(i);
                if (o instanceof Byte) {
                    byte b = (byte) o;
                    this.data.add(b);
                } else if (o instanceof Integer) {
                    int v = (int) o;
                    for (int j = 0; j < 4; j++) {
                        int b = 0;
                        for (int k = 0; k < 8; k++) {
                            b = b << 1;
                            int x = (3 - j) * 8 + (7 - k);
                            int c = (v >> x) & 1;
                            b = b | c;
                        }
                        byte d = (byte) b;
                        this.data.add(d);
                    }
                } else // support for other formats (eg. Double) may be added
                {
                    throw new Exception("Specified argument for Data constructor contains Objects that are not supported yet");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    // Gives the number of bytes forming the Data object
    public int numberOfBytes() {
        return this.data.size();
    }

    // Verifies whether k is the byte range for this Data object
    private boolean inByteRange(int k) {
        try {
            if (k < 0 || k >= this.numberOfBytes())
                throw new Exception("Data: byte index is out of range (0<=" + k + "<" + this.numberOfBytes() + ")");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return true;
    }

    // Gives the k^th byte of the Data object
    public byte getByte(int k) {
        if (this.inByteRange(k)) return (byte) this.data.get(k);
        return 0;
    }

    // Gives the k^th byte of the Data object in String format
    public String getByteString(int k) {
        String print = "";
        byte b = this.getByte(k);
        for (int i = 0; i < 8; i++) print = print + ((b >> (7 - i)) & 1);
        return print;
    }

    // Gives the number of bits of the Data object
    public int numberOfBits() {
        return 8 * this.data.size();
    }

    // Verifies whether k in the bit range for this Data object
    private boolean inBitRange(int k) {
        try {
            if (k < 0 || k >= this.numberOfBits())
                throw new Exception("Data: bit index is out of range (0<=" + k + "<" + this.numberOfBits() + ")");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return true;
    }

    // Gives the k^th bit of the Data object
    public byte getBit(int k) {
        if (this.inBitRange(k)) {
            int i = k / 8;
            int j = k % 8;
            int b = this.data.get(i);
            return (byte) ((b >> j) & 1);
        }
        return 0;
    }

    // Sets the bit k to 1
    public void setBitToOne(int k) {
        if (this.inBitRange(k)) {
            int i = k / 8;
            int j = k % 8;
            int b = this.data.get(i);
            int c = 1 << (7 - j);
            b = b | c;
            byte d = (byte) b;
            this.data.set(i, d);
        }
    }

    // Sets the bit k to 0
    public void setBitToZero(int k) {
        if (this.inBitRange(k)) {
            int i = k / 8;
            int j = k % 8;
            int b = this.data.get(i);
            int c = ~(1 << (7 - j));
            b = b & c;
            byte d = (byte) b;
            this.data.set(i, d);
        }
    }

    // Flips the k^th bit
    public void flipBit(int k) {
        if (this.inBitRange(k)) {
            int i = k / 8;
            int j = k % 8;
            int b = this.data.get(i);
            int c = 1 << (7 - j);
            b = b ^ c;
            byte d = (byte) b;
            this.data.set(i, d);
        }
    }

    // Performs the "crossover" operation between two Data objects of the same size
    //      for d1 = xxxxxxxxxxxxxxxxxx
    //          d2 = yyyyyyyyyyyyyyyyyy
    //           x =             |
    //      result = xxxxxxxxxxxxxyyyyy
    public static Data crossover(Data d1, Data d2, int x) {
        try {
            if (d1.numberOfBytes() != d2.numberOfBytes())
                throw new Exception("Data crossover is impossible between two Data objects having different byte length");
            if (x < 0 || x > d1.numberOfBits())
                throw new Exception("Pivot point in crossover is out of bounds (0<=" + x + "<" + d1.numberOfBits() + ")");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        int bitx = x % 8;
        int bytex = x / 8;
        int n = d1.numberOfBytes();
        int m = d1.numberOfBits();

        ArrayList<Object> bytelist = new ArrayList<Object>(n);
        for (int i = 0; i < bytex; i++) bytelist.add(d1.getByte(i));
        int b1 = 0;
        for (int k = 0; k < bitx; k++) b1 = b1 + (1 << (7 - k));
        int b2 = 0;
        for (int k = bitx; k < 8; k++) b2 = b2 + (1 << (7 - k));
        int middle = (b1 & d1.getByte(bytex)) + (b2 & d2.getByte(bytex));
        byte bmiddle = (byte) middle;
        bytelist.add(bmiddle);
        for (int i = bytex + 1; i < n; i++) bytelist.add(d2.getByte(i));

        return new Data(bytelist);
    }

    // toString
    public String toString() {
        String print = "|";
        for (int i = 0; i < this.numberOfBytes(); i++) print = print + this.getByteString(i) + "|";
        return print;
    }

    /*------------ Our Data Methods ---------*
    instead of using an array of n bytes, we use an array of numberOfColors int to generate the colors.
     */

    static int[][] randomArray(int length, int numberOfColors) {

        Random r = new Random();
        Random r0 = new Random();
        Random r1 = new Random();
        int[] colours = new int[numberOfColors];
        for (int i = 0; i < numberOfColors; i++) {
            colours[i] = getIntFromColor(r.nextInt(256), r0.nextInt(256), r1.nextInt(256));
        }

        int[][] color = new int[length][length];
        for (int x = 0; x < length; x++)
            for (int y = 0; y < length; y++)
                color[x][y] = colours[r.nextInt(numberOfColors)];
        return color;
    }

    static BufferedImage writeImage(int[][] color, String name) {
      /*
      rouge 16724787
      bleu 3355643
      vert 3406643
       */
        String path = "./" + name + ".png";
        BufferedImage image = new BufferedImage(color.length, color[0].length, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < color.length; x++) {
            for (int y = 0; y < color.length; y++) {
                image.setRGB(x, y, color[x][y]);
            }
        }
        File ImageFile = new File(path);
        try {
            ImageIO.write(image, "png", ImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    /**
     * Subdivises the entry array (image) for each thread
     *
     * @param entry        input image
     * @param numberOfZone number of divisions
     * @return Map <ThreadId, portionOfInput>
     */
    static Map<Integer, int[][]> subArray(int[][] entry, int numberOfZone) {
        Map<Integer, int[][]> map = new HashMap<>();
        int sqrt = (int) Math.sqrt(numberOfZone);
        int size = entry.length / sqrt;
     /*   System.out.println("Work size: " + size*sqrt);
        System.out.println("Input size: " + entry.length);*/
        for (int i = 0; i < numberOfZone; i++) {
            int[][] na = new int[size][size];
            for (int x = 0; x < size; x++) {
                System.arraycopy(entry[x + size * (i % sqrt)], size * (i / sqrt), na[x], 0, size);
            }
            map.put(i, na);
        }
        return map;
    }


    /**
     * Generates and array based on a map
     *
     * @param map Map to convert
     * @param size size of the output image
     * @return Map converted into int[][]
     */
    static int[][] unite4arrays(Map<Integer, int[][]> map, int size) {
        int sqrt = (int) Math.sqrt( map.keySet().size());
        int[][] result = new int[size * sqrt][size * sqrt];
        for (int i = 0; i < map.keySet().size(); i++) {

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    result[x + size * (i % sqrt)][y + size * (i / sqrt)] = map.get(i)[x][y];
                }
            }

        }
        return result;
    }

    private static int getIntFromColor(int Red, int Green, int Blue) {
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
}