import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.Locale;

public class CloudData {

	Vector [][][] advection; // in-plane regular grid of wind vectors, that evolve over time
	float [][][] convection; // vertical air movement strength, that evolves over time
	int [][][] classification; // cloud type per grid point, evolving over time
	int dimx, dimy, dimt; // data dimensions
	Vector total = new Vector(); //stores average X and Y wind values for entire grid

	public CloudData(String inputDataPath){
		//Constructor
		readData(inputDataPath);
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
						advection[t][x][y].add(sc.nextFloat());
						advection[t][x][y].add(sc.nextFloat());
						convection[t][x][y] = sc.nextFloat();
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

	public void setCloudDataClassification(){
		//Iterate over all the data and set magnitude for classification
		for (int t = 0; t < dimt; t++){
			for (int x = 0; x < dimx; x++){
				for (int y = 0; y < dimy; y++){                       
					double windMagnitude = getMagnitude(t,x,y);
					if (Math.abs(convection[t][x][y]) > windMagnitude){
						classification[t][x][y] = 0;
					}
					else if (windMagnitude > 0.2){
						classification[t][x][y] = 1;
					}
					else{
						classification[t][x][y] = 2;
					}
				}
			}
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
						X += ((Float)advection[t][i][j].get(0)).floatValue();
						Y += ((Float)advection[t][i][j].get(1)).floatValue();
					}
				}
			}
		}
		//Square both vectors, total them and squareroot
		double out =  Math.sqrt(Math.pow(Y, 2) + Math.pow(X, 2));
		//return (float)((int)out*10)/10;
		return (double)((int)out*10)/10;
	}
        
	public void setAdvecation(){
		float X = 0;
		float Y = 0;
		for (int t = 0; t < dimt; t++){
			for (int x = 0; x < dimx; x++){
				for (int y = 0; y < dimy; y++){
					X += ((Float)advection[t][x][y].get(0)).floatValue();
					Y += ((Float)advection[t][x][y].get(1)).floatValue();
				}
			}
		}
		double outX = (double)((int)(X/dim()*1000))/1000;
		double outY = (double)((int)(Y/dim()*1000))/1000;
		total.add(outX);
		total.add(outY);
	}
	
	public void printAverage(){
		System.out.println(total.get(0) +" "+total.get(1));
	}

	public void printClassification(){
		for (int t = 0; t < dimt; t++){
			for (int x = 0; x < dimx; x++){
				for (int y = 0; y < dimy; y++){
						System.out.print(String.valueOf(classification[t][x][y])+" ");
				}
			}
			System.out.print("\n");
		}
	}
	
	// write classification output to file
	void writeData(String fileName, Vector wind){
		 try{
			 FileWriter fileWriter = new FileWriter(fileName);
			 PrintWriter printWriter = new PrintWriter(fileWriter);
			 printWriter.printf("%d %d %d\n", dimt, dimx, dimy);
			 printWriter.printf("%f %f\n", wind.get(0), wind.get(1));
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
