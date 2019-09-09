import java.util.concurrent.TimeUnit;

public class Cloudscapes{

    public static void main(String[] args){

        System.out.println("running Cloudscapes");
        CloudData cloudData = new CloudData("./inputs/"+args[0]);

        //start timer
        long lStartTime = System.nanoTime();
        cloudData.setAdvecation();
        cloudData.setCloudDataClassification();
		//end timer
        long lEndTime = System.nanoTime();

		//time elapsed
        long output = lEndTime - lStartTime;

        System.out.println("Elapsed time in microseconds: " + output/1000);        
        cloudData.printAverage();
        cloudData.printClassification();
    }
}