import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.Vector;

public class ParallelThread extends RecursiveTask<Vector>{

    int min;
    int max;
    Vector[][][] advVector;
    float sumX, sumY;
    int[][][] classList;
    final int split;
    int dimt, dimx, dimy;
    
    //Constructor for the thread
    public ParallelThread(int mi, int mx, int spl, Vector[][][] inVector, int[][][] inClass)
    {
        dimt = ParallelCloudData.dimt;
        dimx = ParallelCloudData.dimx;
        dimy = ParallelCloudData.dimy;
        min = mi;
        max = mx;
        split = spl;
        advVector = inVector;
        classList = inClass;
    }
    
    private int[] locate(int pos)
    {
        int[] ind = new int[3];
        ind[0] = (int) pos / (dimx*dimy); // t
        ind[1] = (pos % (dimx*dimy)) / dimy; // x
        ind[2] = pos % (dimy); // y
        return ind;
    }

    public void setCloudDataClassification(int x, int y, int t){
        //Iterate over all the data and set magnitude for classification                     
        double windMagnitude = getMagnitude(t,x,y);
        if (Math.abs(ParallelCloudData.convection[t][x][y]) > windMagnitude){
            classList[t][x][y] = 0;
        }
        else if (windMagnitude > 0.2){
            classList[t][x][y] = 1;
        }
        else{
            classList[t][x][y] = 2;
        }
    }

    public double getMagnitude(int t, int x, int y){
        //Get the vectors magnitude
        float X = 0;
        float Y = 0;
        //|-1|0|1| average in x direction
        for (int i = x-1; i <= x+1; i++) {
            if (i >= 0 && i < dimx){
                //|-1|
                //|0 |
                //|1 | average in y direction
                for (int j = y-1; j <= y+1; j++){
                    if (!(i == x && j == y) && j >= 0 && j < dimy){
                        //If at boundary case, ignore those values
                        X += ((Float)advVector[t][i][j].get(0)).floatValue();
                        Y += ((Float)advVector[t][i][j].get(1)).floatValue();
                    }
                }
            }
        }
        //Square both vectors, total them and squareroot
        double out =  Math.sqrt(Math.pow(Y, 2) + Math.pow(X, 2));
        //return (float)((int)out*10)/10;
        //(double)((int)out*10)/10
        return out;
    }

    @Override
    protected Vector compute(){
        //if less than split point, compute
        if ((max-min) <= split){
            sumX = 0;
            sumY = 0;
            Vector outVect = new Vector();
            for (int i = min; i < max; i++){ 
                int[] vecLoc = locate(i);
                int tt = vecLoc[0];
                int tx = vecLoc[1];
                int ty = vecLoc[2];
                Vector temp = advVector[tt][tx][ty];

                sumX += ((Float)temp.get(0));
                sumY += ((Float)temp.get(1));
                setCloudDataClassification(ty, tx, tt);
            }
            outVect.add(sumX);
            outVect.add(sumY);
            return outVect;
        }else{//Split into left and right thread and compute
            int middle = (max+min)/2;
            ParallelThread left = new ParallelThread(min, middle, split, advVector, classList);
            ParallelThread right = new ParallelThread(middle, max, split, advVector, classList);
            //start the left thread
            left.fork();
            //compute in this thread
            Vector rightVect = right.compute();
            //join outputs upon completion
            Vector leftVect = left.join();
            float localx = ((Float)rightVect.get(0)) + ((Float)leftVect.get(0));
            float localy = ((Float)rightVect.get(1)) + ((Float)leftVect.get(1));
            Vector rVector = new Vector();
            rVector.add(localx);
            rVector.add(localy);
            return rVector;
        }
    }
}