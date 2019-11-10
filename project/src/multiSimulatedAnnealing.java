

import java.util.concurrent.CountDownLatch;


public class multiSimulatedAnnealing extends Thread {


    private int[][] color;
    private double currentValue;
    private double coolingRate;
    private double T;
    private int[] neighbours;
    private CountDownLatch latch;

    /**
     * Called by each threads individially
     *
     * @param f           Objective function
     * @param color       Image input
     * @param latch       shared memory latch
     * @param temperature Starting temperature
     * @param coolingRate Cooling rate between iterations
     */
    multiSimulatedAnnealing(Objective f, int[][] color, CountDownLatch latch, double temperature, double coolingRate) {
        this.latch = latch;
        this.color = color;
        this.coolingRate = coolingRate;
        this.T = temperature;
        neighbours = SimulatedAnnealing.randomAdjacentPixelPair(color);

    }

    public void run() {
        try {
            int candidateValue;
           /* if (!Main.crystalMode) {
                candidateValue = Objective.initEnergy(color);
            }else {
                candidateValue = Objective.energyImage2(color);
            }*/
            //   int iteration = 0;
            while (T > 1) {

              /*  int[][] colorCandidate = new int[color.length][color.length];
                for (int x = 0; x < color.length; x++) {
                    System.arraycopy(color[x], 0, colorCandidate[x], 0, color.length);
                }*/
                neighbours = SimulatedAnnealing.randomAdjacentPixelPair(color);
                int candidate1x = neighbours[0];
                int candidate1y = neighbours[1];
                int candidate2x = neighbours[2];
                int candidate2y = neighbours[3];
                if (!(color[candidate1x][candidate1y] == color[candidate2x][candidate2y])) {

                    //Swap
                    double beforeSwapLocalValue;
                    double afterSwapLocalValue;

                    if (!Main.crystalMode) {
                        beforeSwapLocalValue = Objective.checkPxEnergy(color, candidate1x, candidate1y, candidate2x, candidate2y);
                        Objective.swapPx(color, candidate1x, candidate1y, candidate2x, candidate2y);
                        afterSwapLocalValue = Objective.checkPxEnergy(color, candidate1x, candidate1y, candidate2x, candidate2y);
                        candidateValue = (int) (currentValue - beforeSwapLocalValue + afterSwapLocalValue);
                        Objective.swapPx(color, candidate1x, candidate1y, candidate2x, candidate2y);


                    } else {
                        candidateValue = Objective.energyImage2(color);
                    }

                    if (candidateValue < currentValue || (Main.acceptWorse && ((Math.pow(Math.E, (currentValue - candidateValue) / T)) > Math.random()))) { //operation: as the heat goes down, less probability to switch the colors
                        Objective.swapPx(color, candidate1x, candidate1y, candidate2x, candidate2y);
                        currentValue = candidateValue;
                    }

                    T *= 1 - coolingRate;
                    //  iteration++;

                }
            }
            latch.countDown();
        } catch (Exception e) {
            // Throwing an exception
            System.out.println("Exception is caught" + e.getMessage());
            e.printStackTrace();
        }
    }
/*
        public double getValue(){
            return currentValue;
        }
        public int[][] getNewState(){
        return color;
    }
        public boolean getChange(){
            return change;
        }*/
}

