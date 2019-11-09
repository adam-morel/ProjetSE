

import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;


public class multiSimulatedAnnealing extends Thread {


    private int[][] color;
    private double currentValue;
    private double coolingRate;
    private double T;
    private int[] neighbours;
    private boolean change;
    private CountDownLatch latch;

    /**
     * Called by each threads individially
     * @param f Objective function
     * @param color Image input
     * @param latch shared memory latch
     * @param temperature Starting temperature
     * @param coolingRate Cooling rate between iterations
     */
        multiSimulatedAnnealing(Objective f, int[][] color, CountDownLatch latch, double temperature, double coolingRate){
            this.latch=latch;
            this.color=color;
            this.coolingRate=coolingRate;
            this.T = temperature;
            neighbours= SimulatedAnnealing.randomAdjacentPixelPair(color);
        }

        public void run()
        {
            try
            {

                while (T > 1) {

                    int[][] colorCandidate = new int[color.length][color.length];
                    for (int x = 0; x < color.length; x++) {
                        System.arraycopy(color[x], 0, colorCandidate[x], 0, color.length);
                    }

                    neighbours= SimulatedAnnealing.randomAdjacentPixelPair(colorCandidate);
                    change = false;
                    int candidate1x = neighbours[0];
                    int candidate1y = neighbours[1];
                    int candidate2x = neighbours[2];
                    int candidate2y = neighbours[3];
                    currentValue=Objective.energyImage(colorCandidate);
                    int temp = colorCandidate[candidate1x][candidate1y];
                    colorCandidate[candidate1x][candidate1y] = colorCandidate[candidate2x][candidate2y];
                    colorCandidate[candidate2x][candidate2y] = temp;
                    int candidateValue = Objective.energyImage(colorCandidate);

                    if (candidateValue < currentValue) {
                        color[candidate1x][candidate1y] = colorCandidate[candidate1x][candidate1y];
                        color[candidate2x][candidate2y] = colorCandidate[candidate2x][candidate2y];
                        currentValue = candidateValue;


                    } else if ((Math.pow(Math.E, (currentValue - candidateValue) / T)) > Math.random()) {
                        color[candidate1x][candidate1y] = colorCandidate[candidate1x][candidate1y];
                        color[candidate2x][candidate2y] = colorCandidate[candidate2x][candidate2y];
                        currentValue = candidateValue;

                    }

                    T *= 1 - coolingRate;


                }
                latch.countDown();
            }
            catch (Exception e)
            {
                // Throwing an exception
                System.out.println ("Exception is caught"+ e.getMessage() );
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

