
/* Objective functions
 *
 * for SE project 2019
 *
 * AM
 */

import java.lang.reflect.*;

public class Objective {
    private int id;
    private String name;
    private String methodName;
    private Double lastValue;

    // Private constructor
    private Objective() {
        this.id = -1;
        this.name = "";
        this.methodName = "";
        this.lastValue = null;
    }

    // Constructor: creates an Objective object from a given id
    // id = 0 ==> objective "BitSum", performs the sum of the bits in the Data object
    // id = 1 ==> objective "SubSetBitSum", computes the absolute value of (number of zeros - number of ones)
    public Objective(int id) {
        try {
            if (id == 0) {
                this.id = id;
                this.name = "BitSum";
                this.methodName = "obj0";
                this.lastValue = null;
            } else if (id == 1) {
                this.id = id;
                this.name = "SubSetBitSum";
                this.methodName = "obj1";
                this.lastValue = null;
            } else throw new Exception("Objective: id " + id + " unknown");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Method implementing Objective related to id = 0
    private double obj0(Data D) {
        double count = 0.0;
        for (int k = 0; k < D.numberOfBits(); k++) count = count + D.getBit(k);
        return count;
    }

    // Method implementing Objective related to id = 1
    private double obj1(Data D) {
        double count = 0.0;

        for (int k = 0; k < D.numberOfBits(); k++) {
            if (D.getBit(k) == 0)
                count = count + 1.0;
            else
                count = count - 1.0;
        }
        if (count < 0.0) count = -count;

        return count;
    }

    // General method evaluating the value of the selected objective for the Data object D
    public double value(Data D) throws NoSuchMethodException {
        Objective O = new Objective();
        Method method = O.getClass().getDeclaredMethod(this.methodName, Data.class);
        try {
            this.lastValue = (Double) method.invoke(this, D);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return this.lastValue;
    }

    // toString
    public String toString() {
        String print = this.name + " (id = " + this.id;
        if (this.lastValue != null) print = print + ", last computed value = " + this.lastValue;
        print = print + ")";
        return print;
    }

    /**
     * Computes the energy centered around two pixels.
     *
     * @param image the relevant image
     * @param x1    x of px1
     * @param y1    y of px1
     * @param x2    x of px2
     * @param y2    y of px2
     * @return the energy level in this configuration
     */
    static double checkPxEnergy(int[][] image, int x1, int y1, int x2, int y2) {
        int result = 0;
        int x0 = Math.min(x1, x2);
        int y0 = Math.min(y1, y2);
        int xx = (x0 == x1) ? x2 : x1;
        int yy = (y0 == y1) ? y2 : y1;
        //Loop area to search for changes
        for (int i = x0 - 1; i <= xx + 1; i++)
            for (int j = y0 - 1; j <= yy + 1; j++) {
                if (i >= 0 && i < image.length && j >= 0 && j < image.length) {
                    if (!Main.crystalMode) {
                        result += checkNrjSides(image, i, j);
                    } else{
                        result+=checkNrjSides(image,i,j,Main.crystalRange);
                    }
                }

            }

        return result;
    }

    /**
     * check the energy for a given pixel ( 8 sides around)
     *
     * @param image input image
     * @param i     pixel.x
     * @param j     pixel.y
     * @return energy of that pixel, between 0 and 4
     */
    private static double checkNrjSides(int[][] image, int i, int j) {
      // old version
        /*  int energy = 0;
        if (image.length > 1) {
            if (i - 1 > 0) {
                if (image[i][j] != image[i - 1][j]) {
                    energy++;
                }
                if (j - 1 > 0) {
                    if (image[i][j] != image[i - 1][j - 1]) {
                        energy++;
                    }
                }
                if (j + 1 < image.length) {
                    if (image[i][j] != image[i - 1][j + 1]) {
                        energy++;
                    }
                }
            }
            if (i + 1 < image.length) {
                if (j + 1 < image.length) {
                    if (image[i][j] != image[i + 1][j + 1]) {
                        energy++;
                    }
                }

                if (image[i][j] != image[i + 1][j]) {
                    energy++;
                }
                if (j - 1 > 0) {
                    if (image[i][j] != image[i + 1][j - 1]) {
                        energy++;
                    }
                }
            }
            if (j + 1 < image.length) {
                if (image[i][j] != image[i][j + 1]) {
                    energy++;
                }
            }
            if (j - 1 > 0) {
                if (image[i][j] != image[i][j - 1]) {
                    energy++;
                }
            }


        }
        return energy;*/
     return checkNrjSides(image, i, j,1);
    }

    private static double checkNrjSides(int[][] image, int i, int j, int range) {
        int energy = 0;
        for (int x = Math.max(0, i - range); x < Math.min(image.length, i + range); x++)
            for (int y = Math.max(0, j - range); y < Math.min(image.length, j + range); y++) {
                if (image[x][y] != image[i][j])
                    energy++;

            }
        return energy;
    }

    static void swapPx(int[][] img, int x1, int y1, int x2, int y2) {
        int tmp = img[x1][y1];
        img[x1][y1] = img[x2][y2];
        img[x2][y2] = tmp;
    }

    static double initEnergy(int[][] color) {
        int currentValue = 0;
        for (int i = 0; i < color.length; i++) {
            for (int j = 0; j < color.length; j++) {
                currentValue += Objective.checkNrjSides(color, i, j);
            }
        }
        return currentValue;
    }

    /**
     * Computes the energy of the image. energy is produced if a pixel is in contact with a pixel of a different color. Mode crystal form
     *
     * @param image input image
     * @return energy
     */
    static double initEnergyCrystal(int[][] image) {
        int energy = 0;
        int range = Main.crystalRange;


        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image.length; j++) {
                energy += checkNrjSides(image, i, j, range);
                int xsquare = i - range;
                int ysquare = j - range;
                if (i - range < 0)
                    xsquare = 0;
                if (i + range > image.length - 1)
                    ysquare = image.length - 1;
                energy += Math.pow(image.length, 2) - xsquare * ysquare;
            }
        }

        return energy;
    }
}

