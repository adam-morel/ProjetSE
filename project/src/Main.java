import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
static double energyMulti=0;
static double energySeq=0;

    /**
     *
     * @param args Demonstration main, feel free to play with the parameters. warning, nbThreads MUST be a square number. arraylength should be a multiple of that sqrt(nbThreads) for better results.
     * @throws InterruptedException
     * @throws IOException
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        clean();

        //Parameters
        int nbThreads=4;
        int nbColors = 3;
        int arrayLength = 36;
        int temperature = 10000;
        double coolingRate = 0.00001;
        int[][] input = Data.randomArray(arrayLength, nbColors);
        int[][] clone = input.clone();
        Objective f = new Objective(0);

        demo(nbThreads,nbColors,temperature,coolingRate,input,clone,f);
    }

    private static void demo(int nbThreads,int nbColors,int temperature, double coolingRate, int[][] input, int[][] clone, Objective f) throws InterruptedException, IOException {
        System.out.println("Demonstration.\nComparison between a multithreaded and a sequential version.\nTest With " + nbColors + " colors, on an array of size " + input.length + ":");
        double time4Thread = System.currentTimeMillis();
        SimulatedAnnealing.msa(clone, f, temperature, coolingRate, nbThreads);
        time4Thread = System.currentTimeMillis() - time4Thread;
        System.out.println("End of multithreading run. Time elapsed: "+time4Thread+"\nPlease check the png files in project root\n------------ Test sequential version---------- ");
        double timeOneThread= System.currentTimeMillis();
        new SimulatedAnnealing(input, f, temperature, coolingRate);
        timeOneThread=System.currentTimeMillis()-timeOneThread;
        System.out.println("End of sequential run. Time elapsed: "+timeOneThread+"\nPlease check the png and gif files in project root");
        double compTime=100-100* time4Thread/timeOneThread;
        System.out.println("\nTime wise, multithreading was "+(int)compTime+ "% more effective");
        if(!(energyMulti ==0|| energySeq==0)) {
            double ratio=100- 100*energyMulti/energySeq;
            System.out.println("Computation wise, Multithreading was "+ratio+"% more effective");
        }
    }

    private static void clean() {
        try {
            Path fileToDeletePath = Paths.get("./Sequential process start.png");
            Files.delete(fileToDeletePath);
        } catch (IOException ignored) {

        }
        try {
            Path fileToDeletePath = Paths.get("./Sequential process end.png");
            Files.delete(fileToDeletePath);
        } catch (IOException ignored) {

        }
        try {
            Path fileToDeletePath = Paths.get("./Multithreading process End.png");
            Files.delete(fileToDeletePath);
        } catch (IOException ignored) {

        }
        try {
            Path fileToDeletePath = Paths.get("./Multithreading process Start.png");
            Files.delete(fileToDeletePath);
        } catch (IOException ignored) {

        }
        try {
            Path fileToDeletePath = Paths.get("./Out.gif");
            Files.delete(fileToDeletePath);
        } catch (IOException ignored) {

        }

    }
}