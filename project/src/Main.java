import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
static double energyMulti=0;
static double energySeq=0;
static boolean crystalMode=false;
static boolean acceptWorse=true;

    /**
     *
     * @param args Demonstration main, feel free to play with the parameters. warning, nbThreads MUST be a square number. arraylength should be a multiple of that sqrt(nbThreads) for better results.
     * @throws InterruptedException
     * @throws IOException
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        clean();

        //Parameters

        crystalMode=false; //Abandon All Hope ? TODO fix dat.
        acceptWorse=true;
        int nbThreads=4; //Obviously must be inferior to arraylength. must be a square number
        int nbColors = 3;
        int arrayLength = 400; // result of the process is more obvious with a small array ( such as 20)
        int temperature = 100000;
        double coolingRate = 0.00001;
        int[][] input = Data.randomArray(arrayLength, nbColors);
        int[][] clone0= new int[input.length][input.length];
        for(int x=0;x<input.length;x++)
            System.arraycopy(input[x], 0, clone0[x], 0, input.length);

        Objective f = new Objective(0);

        demo(nbThreads,nbColors,temperature,coolingRate,input,clone0,f);
    }

    private static void demo(int nbThreads,int nbColors,int temperature, double coolingRate, int[][] input, int[][] clone, Objective f) throws InterruptedException, IOException {
        System.out.println("Demonstration.\nComparison between a multithreaded and a sequential version.\nTest With " + nbColors + " colors, on an array of size " + input.length + ":");
       if(Main.crystalMode){
           System.out.println("CrystalMode activated. this will take much longer.");
       }else {
           System.out.println("CrystalMode not activated.");
       }

        double time4Thread = System.currentTimeMillis();
        SimulatedAnnealing.msa(clone, f, temperature, coolingRate, nbThreads);
        time4Thread = System.currentTimeMillis() - time4Thread;
        System.out.println("End of multithreading run. Time elapsed: "+time4Thread+"\nPlease check the png files in project root\n------------ Test sequential version---------- ");



        double timeOneThread= System.currentTimeMillis();
        new SimulatedAnnealing(input, f, temperature, coolingRate);


        timeOneThread=System.currentTimeMillis()-timeOneThread;
        System.out.println("End of sequential run. Time elapsed: "+timeOneThread+"\nPlease check the png and gif files in project root");
        double compTime=100* time4Thread/timeOneThread;
        System.out.println("\nTime wise, multithreading took "+(int)compTime+ "% of the Sequential version's time");
        if(!(energyMulti ==0|| energySeq==0)) {
            double ratio= 100*energyMulti/energySeq;
            System.out.println("Computation wise, Multithreading's energy is "+(int)ratio+"% of the Sequential version");
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
            Path fileToDeletePath = Paths.get("./Multithreading process End.png");
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