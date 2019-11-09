import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;


public class SimulatedAnnealing {

    private Objective f;

    /**
     * Starts the analysis on a single thread
     *
     * @param sizeArray      size of the image: square with a side length of sizeArray pixels
     * @param numberOfColors number of colors desired
     * @param f              objective object
     * @param temperature    Starting temperature
     * @param coolingRate    cooling rate after each iteration
     * @throws IOException
     */
    public SimulatedAnnealing(int sizeArray, int numberOfColors, Objective f, double temperature, double coolingRate) throws IOException {
        new SimulatedAnnealing(Data.randomArray(sizeArray, numberOfColors), f, temperature, coolingRate);
    }

    SimulatedAnnealing(int[][] color, Objective f, double temperature, double coolingRate) throws IOException {
        this.f = f;
        BufferedImage firstImage = Data.writeImage(color, "Sequential process start");
        //System.out.println("Generating starting image: start.png");
        double T = temperature;
        double currentValue = Objective.energyImage(color);
        int iterations = 0;

        System.out.println("1 thread :");
        System.out.println("Energy before annealing : " + currentValue);
        double startValue = currentValue;

        ImageOutputStream output =
                new FileImageOutputStream(new File("Out.gif"));
        System.out.println("Generating Out.gif : history of the production. Please Wait ...");

        GifSequenceWriter writer =
                new GifSequenceWriter(output, firstImage.getType(), 100, false);

        writer.writeToSequence(firstImage);

        while (T > 1) {

            int[] neighbours = randomAdjacentPixelPair(color);
            int candidate1x = neighbours[0];
            int candidate1y = neighbours[1];
            int candidate2x = neighbours[2];
            int candidate2y = neighbours[3];


            int[][] colorCandidate = new int[color.length][color.length];

            for (int x = 0; x < color.length; x++) {
                System.arraycopy(color[x], 0, colorCandidate[x], 0, color.length);
            }

            int temp = colorCandidate[candidate1x][candidate1y];
            colorCandidate[candidate1x][candidate1y] = colorCandidate[candidate2x][candidate2y];
            colorCandidate[candidate2x][candidate2y] = temp;
            int candidateValue = Objective.energyImage(colorCandidate);

            if (candidateValue < currentValue) {
                color[candidate1x][candidate1y] = colorCandidate[candidate1x][candidate1y];
                color[candidate2x][candidate2y] = colorCandidate[candidate2x][candidate2y];
                currentValue = candidateValue;

            } else if ((Math.pow(Math.E, (currentValue - candidateValue) / T)) > Math.random()) { //operation: as the heat goes down, less probability to switch the colors
                color[candidate1x][candidate1y] = colorCandidate[candidate1x][candidate1y];
                color[candidate2x][candidate2y] = colorCandidate[candidate2x][candidate2y];
                currentValue = candidateValue;
            }

            T *= 1 - coolingRate;
            iterations++;

            if ((iterations % 10000) == 0) {
                BufferedImage nextImage = Data.writeImage(color, "next step");
                writer.writeToSequence(nextImage);
            }

        }
        currentValue = Objective.energyImage(color);

        System.out.println("Number of iterations : " + iterations);
        System.out.println("Energy after annealing : " + currentValue);
       System.out.println("Efficiency in energy reduction - End energy / Start energy:  " + currentValue/startValue);
        Main.energySeq = currentValue;
        BufferedImage end = Data.writeImage(color, "Sequential process end");
        // System.out.println("Generating final image: end.png");
        writer.writeToSequence(end);
        try {
            Path fileToDeletePath = Paths.get("./next step.png");
            Files.delete(fileToDeletePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs the algorithm by using four threads, each focused on a quarter of the image
     * version with undefined input image, undefined nbThread. default =4
     *
     * @param sizeArray      size of the image: square with a side length of sizeArray pixels
     * @param numberOfColors number of colors desired
     * @param f              objective object
     * @param temperature    Starting temperature
     * @param coolingRate    cooling rate after each iteration
     * @throws InterruptedException
     */
    public static void msa(int sizeArray, int numberOfColors, Objective f, double temperature, double coolingRate) throws InterruptedException {
        msa(sizeArray, numberOfColors, f, temperature, coolingRate, 4);
    }

    /**
     * version with defined input image, undefined nbThread. default =4
     *
     * @param color
     * @param f
     * @param temperature
     * @param coolingRate
     * @throws InterruptedException
     */
    public static void msa(int[][] color, Objective f, double temperature, double coolingRate) throws InterruptedException {
        msa(color, f, temperature, coolingRate, 4);
    }

    /**
     * version with defined input image, defined nbThread.
     *
     * @param sizeArray
     * @param numberOfColors
     * @param f
     * @param temperature
     * @param coolingRate
     * @param nbThreads
     * @throws InterruptedException
     */
    public static void msa(int sizeArray, int numberOfColors, Objective f, double temperature, double coolingRate, int nbThreads) throws InterruptedException {
        msa(Data.randomArray(sizeArray, numberOfColors), f, temperature, coolingRate, nbThreads);
    }


    /**
     * @param color       input image
     * @param f           objective object
     * @param temperature Starting temperature
     * @param coolingRate cooling rate after each iteration
     * @param nbThreads   Number of threads to involve. must be a square number
     * @throws InterruptedException
     */

    public static void msa(int[][] color, Objective f, double temperature, double coolingRate, int nbThreads) throws InterruptedException {
        int realNbThread = Math.abs(nbThreads);
        int n = (int) Math.sqrt(realNbThread);
        //Checks for bad nbThreads parameters. must be a non-null square number
        if (realNbThread <= 0 ||
                n * n != nbThreads
        ) {
            System.out.println("Error number of threads. Default = 4");
            realNbThread = 4;
        }
        //int[][] color = Data.randomArray(sizeArray, numberOfColors);

        //to check with if the sum at the end is the same (if it's =! then there is problem, or the input length is not a factor of sqrt(nbThreads))
        int sum = 0;
        for (int[] ints : color) {
            for (int y = 0; y < color.length; y++) {
                sum += ints[y];
            }
        }

        Data.writeImage(color, "Multithreading process Start");


        double currentValue = Objective.energyImage(color);
        //int iterations = 0;

        System.out.println(realNbThread + " threads :");
        System.out.println("Energy before annealing : " + currentValue);
        double startValue = currentValue;

        Map<Integer, int[][]> map = Data.subArray(color, realNbThread);


        CountDownLatch latch = new CountDownLatch(realNbThread);
        multiSimulatedAnnealing[] threads = new multiSimulatedAnnealing[realNbThread];
        for (int i = 0; i < realNbThread; i++) {
            threads[i] = new multiSimulatedAnnealing(f, map.get(i), latch, temperature, coolingRate);
        }
        for (int i = 0; i < realNbThread; i++) {
            threads[i].start();
        }
        latch.await();

        color = Data.unite4arrays(map, map.get(0).length);

        int sum2 = 0;
        currentValue = Objective.energyImage(color);
        for (int[] ints : color) {
            for (int y = 0; y < color.length; y++) {
                sum2 += ints[y];
            }
        }

        System.out.println("Energy after annealing : " + currentValue);
        Main.energyMulti=currentValue;

        System.out.println("Efficiency in energy reduction - End energy / Start energy:  " + currentValue/startValue);
        Data.writeImage(color, " Multithreading process End");
        if (sum != sum2)
            System.out.println("Error sum not equal: ");

    }

    /**
     * Selects a random pixel and returns an array of the pixel's x and y, as well as a random neighbour's x and y
     *
     * @param color The image we work on
     * @return Array of a pixel's x and y, and a neighbour's x and y
     */
    static int[] randomAdjacentPixelPair(int[][] color) {
        int max = color.length;
        Random r1 = new Random();
        int candidate1x = r1.nextInt(max);
        Random r2 = new Random();
        int candidate1y = r2.nextInt(max);

        int[] arrayAdjacent = {1, -1, 0};
        int candidate2x = -1;
        int candidate2y = -1;
        Random r3 = new Random();
        Random r4 = new Random();
        while (!(candidate2x < max && candidate2x >= 0 && candidate2y < max && candidate2y >= 0 && (candidate2x != candidate1x || candidate2y != candidate1y))) {
            candidate2x = candidate1x + arrayAdjacent[r3.nextInt(3)];
            candidate2y = candidate1y + arrayAdjacent[r4.nextInt(3)];
        }

        return new int[]{candidate1x, candidate1y, candidate2x, candidate2y};
    }
}
