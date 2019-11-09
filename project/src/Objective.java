
/* Objective functions
 *
 * for SE project 2019
 *
 * AM
 */

import java.util.ArrayList;
import java.lang.reflect.*;

public class Objective
{
   private int id;
   private String name;
   private String methodName;
   private Double lastValue;

   // Private constructor
   private Objective()
   {
      this.id = -1;
      this.name = "";
      this.methodName = "";
      this.lastValue = null;
   }

   // Constructor: creates an Objective object from a given id
   // id = 0 ==> objective "BitSum", performs the sum of the bits in the Data object
   // id = 1 ==> objective "SubSetBitSum", computes the absolute value of (number of zeros - number of ones)
   public Objective(int id)
   {
      try
      {
         if (id == 0)
         {
            this.id = id;
            this.name = "BitSum";
            this.methodName = "obj0";
            this.lastValue = null;
         }
         else if (id == 1)
         {
            this.id = id;
            this.name = "SubSetBitSum";
            this.methodName = "obj1";
            this.lastValue = null;
         }
         else throw new Exception("Objective: id " + id + " unknown");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Method implementing Objective related to id = 0
   private double obj0(Data D)
   {
      double count = 0.0;
      for (int k = 0; k < D.numberOfBits(); k++)  count = count + D.getBit(k);
      return count;
   }

   // Method implementing Objective related to id = 1
   private double obj1(Data D)
   {
      double count = 0.0;

      for (int k = 0; k < D.numberOfBits(); k++)
      {
         if (D.getBit(k) == 0)
            count = count + 1.0;
         else
            count = count - 1.0;
      }
      if (count < 0.0)  count = -count;

      return count;
   }

   // General method evaluating the value of the selected objective for the Data object D
   public double value(Data D) throws NoSuchMethodException
   {
      Objective O = new Objective();
      Method method = O.getClass().getDeclaredMethod(this.methodName,Data.class);
      try
      {
         this.lastValue = (Double) method.invoke(this,D);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      return (double) this.lastValue;
   }

   // toString
   public String toString()
   {
      String print = this.name + " (id = " + this.id;
      if (this.lastValue != null)  print = print + ", last computed value = " + this.lastValue;
      print = print + ")";
      return print;
   }

   /**
    * Computes the energy of the image. energy is produced if a pixel is in contact with a pixel of a different color
    * @param image input image
    * @return energy of the image
    */
   static int energyImage(int[][] image){
      int energy =0;
      for (int i=0;i<image.length;i++){
         for (int j=0;j<image.length;j++){
            if(i-1 >0){
               if(image[i][j] != image[i-1][j]){ energy++;}
               if(j-1 >0){
                  if(image[i][j] != image[i-1][j-1]){ energy++;}
               }
               if (j + 1 < image.length) {
                  if (image[i][j] != image[i - 1][j + 1]) {
                     energy++;
                  }
               }
            }
            if(i+1 < image.length) {
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
            if(j-1 >0) {
               if (image[i][j] != image[i][j - 1]) {
                  energy++;
               }
            }


            }
         }

      return energy;
   }


   /*public static int energyImage2(int[][] image){
      int energy =0;
      for (int i=0;i<image.length;i++){
         for (int j=0;j<image.length;j++){

            for (int x=0;x<image.length;x++){
               for (int y=0;y<image.length;y++) {
                     if ((x==i && (y==j-1 || y==j+1))
                             || ((x==i-1 || x==i+1) && (y==i-1 ||y==i||y==i+1))){
                        if(image[x][y] != image[i][j]){
                           //System.out.printf("lel");
                           energy++;
                        }

                     }
                     else{
                        if(image[x][y] == image[i][j]){
                           energy++;
                        }
                     }
               }
               }


         }
      }

      return energy;
   }*/
}

