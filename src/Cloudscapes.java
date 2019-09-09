import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.FileNotFoundException;

public class Cloudscapes{

    public static void main(String[] args) throws FileNotFoundException {
        try {

            String inputFile = args[0];
            String outputFile = args[1];
            long lStartTime = 0;
            long lEndTime = 0;
            long lSeqTotalTime = 0;         

            CloudData cloudData = new CloudData("./inputs/"+inputFile);
            //Warm the cache
            for (int j = 0; j<5; j++){
                System.out.println("Warming up cache for sequential run"+j+"/5");
                cloudData.setAdvecation();
                cloudData.setCloudDataClassification();
            }
            System.out.println("Cache warming complete; beginning sequential runs");
            //begin timed run and average over 5 runs
            for(int i = 0; i<5; i++){
                //start timer
                lStartTime = System.nanoTime();
                //compute
                cloudData.setAdvecation();
                cloudData.setCloudDataClassification();
                //end timer
                lEndTime = System.nanoTime();
                //time elapsed
                long output = (lEndTime - lStartTime)/1000;
                lSeqTotalTime += output;
                System.out.println("Run "+i+" took "+output+" microseconds");
            }
            System.out.println("Average run time for sequential implementation: "+(lSeqTotalTime/5)+" microseconds");

            //cloudData.printAverage();
            //cloudData.printClassification();
            cloudData.writeData("./outputs/"+outputFile);
        } catch (Exception e) {
            System.out.println("File error: "+e);
        }
    }
}