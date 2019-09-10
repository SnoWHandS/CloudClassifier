import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.Locale;
import java.util.concurrent.ForkJoinPool;

public class ParallelCloudData {

	Vector [][][] advection; // in-plane regular grid of wind vectors, that evolve over time
	static float [][][] convection; // vertical air movement strength, that evolves over time
	int [][][] classification; // cloud type per grid point, evolving over time
	static int dimx, dimy, dimt; // data dimensions
	Vector total = new Vector(); //stores average X and Y wind values for entire grid
	static final ForkJoinPool threadPool = new ForkJoinPool();
	int threadSplit;


	public ParallelCloudData(String inputDataPath){
		//Constructor
		readData(inputDataPath);
	}

	public void setSplit(int split){
            threadSplit = split;
    }

	// overall number of elements in the timeline grids
	int dim(){
		return dimt*dimx*dimy;
	}
	
	// convert linear position into 3D location in simulation grid
	void locate(int pos, int [] ind)
	{
		ind[0] = (int) pos / (dimx*dimy); // t
		ind[1] = (pos % (dimx*dimy)) / dimy; // x
		ind[2] = pos % (dimy); // y
	}
	
	// read cloud simulation data from file
	void readData(String fileName){ 
		try{ 
			//Locale needed since , and . are different and used for floats in different countrys
			Scanner sc = new Scanner(new File(fileName), "UTF-8").useLocale(Locale.US);
			
			// input grid dimensions and simulation duration in timesteps
			dimt = sc.nextInt();
			dimx = sc.nextInt(); 
			dimy = sc.nextInt();
			
			// initialize and load advection (wind direction and strength) and convection
			advection = new Vector[dimt][dimx][dimy];
			convection = new float[dimt][dimx][dimy];
			for(int t = 0; t < dimt; t++)
				for(int x = 0; x < dimx; x++)
					for(int y = 0; y < dimy; y++){
						advection[t][x][y] = new Vector();
						convection[t][x][y] = sc.nextFloat();
						advection[t][x][y].add(sc.nextFloat());
						advection[t][x][y].add(sc.nextFloat());
					}
			
			classification = new int[dimt][dimx][dimy];
			sc.close(); 
		}
		catch (IOException e){ 
			System.out.println("Unable to open input file "+fileName);
			e.printStackTrace();
		}
		catch (java.util.InputMismatchException e){ 
			System.out.println("Malformed input file "+fileName);
			e.printStackTrace();
		}
	}

	public void start(){
			//Spawn a thread with relevant data and the split
            Vector averageWinds = threadPool.invoke(new ParallelThread(0, dim(), threadSplit, advection, classification));
			//Assign average wind x and y components
			float windX = (Float)averageWinds.get(0);
            float windY = (Float)averageWinds.get(1);
            setAdvecation(windX, windY);
    }
        
	public void setAdvecation(float windX, float windY){
		double avX = (double)((int)(windX/dim()*1000))/1000;
		double avY = (double)((int)(windY/dim()*1000))/1000;
		total.add(avX);
		total.add(avY);
	}
	
	// write classification output to file
	void writeData(String fileName){
		 try{
			 FileWriter fileWriter = new FileWriter(fileName);
			 PrintWriter printWriter = new PrintWriter(fileWriter);
			 printWriter.printf("%d %d %d\n", dimt, dimx, dimy);
			 printWriter.printf("%f %f\n", total.get(0), total.get(1));
			 for(int t = 0; t < dimt; t++){
				 for(int x = 0; x < dimx; x++){
					for(int y = 0; y < dimy; y++){
						printWriter.printf("%d ", classification[t][x][y]);
					}
				 }
				 printWriter.printf("\n");
		     }	 
			 printWriter.close();
		 }
		 catch (IOException e){
			 System.out.println("Unable to open output file "+fileName);
				e.printStackTrace();
		 }
	}
}
