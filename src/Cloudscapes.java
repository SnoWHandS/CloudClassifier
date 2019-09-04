public class Cloudscapes{

    public static void main(String[] args){
        System.out.println("running Cloudscapes");
        CloudData cloudData = new CloudData("./inputs/"+args[0]);
        cloudData.setAdvecation();
        cloudData.printAverage();
        cloudData.setCloudDataClassification();
    }
}