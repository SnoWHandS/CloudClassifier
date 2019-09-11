import java.util.concurrent.TimeUnit;
import java.io.PrintStream;
import java.io.File;
import java.io.FileNotFoundException;

public class Cloudscapes{

    public static void main(String[] args) throws FileNotFoundException {
        try {

            String inputFile = args[0];
            String outputFile = args[1];
            long lStartTime = 0;
            long lEndTime = 0;
            float lSeqTotalTime = 0;         
            float lParaTotalTime = 0;

            System.out.println("Loading data...");
            CloudData cloudData = new CloudData("./inputs/"+inputFile);
            ParallelCloudData pCloudData = new ParallelCloudData("./inputs/"+inputFile);

            if(args[2].equals("bestSplit")){
                PrintStream csvOut = new PrintStream(new File("bestSplit.csv"));
                System.out.println("Finding best split...");
                //Run garbage collector to prevent it interfereing with run
                System.gc();
                //Warm the cache
                for (int j = 0; j<5; j++){
                    System.out.println("Warming up cache: "+j+"/5");
                    pCloudData.start();
                }
                for (int i = 10; i <= 10000; i+=10){
                    lParaTotalTime = 0;
                    pCloudData.setSplit(i);
                    for(int j = 0; j<5; j++){
                        //start timer
                        lStartTime = System.nanoTime();
                        //compute
                        pCloudData.start();
                        //end timer
                        lEndTime = System.nanoTime();
                        //time elapsed
                        float output = (lEndTime - lStartTime)/1000000.0f;
                        lParaTotalTime += output;
                    }
                    System.out.println("split: "+i+" millis: "+(lParaTotalTime/5));
                    csvOut.println(i +"," +(lParaTotalTime/5));
                }
                csvOut.close();
            }

            //Reset the variable
            lParaTotalTime = 0;

            //set Split to somewhere in the middle
            pCloudData.setSplit(6300);
            //Run garbage collector to prevent it interfereing with run
            System.gc();
            //Warm the cache
            for (int j = 0; j<5; j++){
                System.out.println("Warming up cache: "+j+"/5");
                cloudData.setAdvecation();
                cloudData.setCloudDataClassification();
                pCloudData.start();
            }
            System.out.println("Cache warming complete; beginning parallel runs");

            for(int i = 0; i<5; i++){
                //start timer
                lStartTime = System.nanoTime();
                //compute
                pCloudData.start();
                //end timer
                lEndTime = System.nanoTime();
                //time elapsed
                float output = (lEndTime - lStartTime)/1000000.0f;
                lParaTotalTime += output;
                System.out.println("Run "+i+" took "+output+" milliseconds");
            }
            System.out.println("Average run time for parallel implementation: "+(lParaTotalTime/5)+" milliseconds");

            //Begin sequential run
            for(int i = 0; i<5; i++){
                //start timer   
                lStartTime = System.nanoTime();
                //compute
                cloudData.setAdvecation();
                cloudData.setCloudDataClassification();
                //end timer
                lEndTime = System.nanoTime();
                //time elapsed
                float output = (lEndTime - lStartTime)/1000000.0f;
                lSeqTotalTime += output;
                System.out.println("Run "+i+" took "+output+" milliseconds");
            }
            System.out.println("Average run time for sequential implementation: "+(lSeqTotalTime/5)+" milliseconds");
            System.out.println("Speedup achieved: "+(((float)lSeqTotalTime/5)/((float)lParaTotalTime/5)));

            cloudData.writeData("./outputs/"+outputFile+".seqOutput");
            pCloudData.writeData("./outputs/"+outputFile+".paraOutput");
        } catch (Exception e) {
            System.out.println("File error: "+e);
        }
    }
}