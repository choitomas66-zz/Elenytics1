
import java.util.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateLocation {
    
	/********************* VARIABLES + DATA **********************
	 * maxCalib = maximum number of calibration points expected  *
	 * numCalib = actual number of calibration points            *
	 * maxDev = maximum number of students/devices expected      *
	 * numDev = actual number of students/devices                *
	 * maxAP = maximum number of wifi Access Points expected     *
	 * numAP = actual number of wifi Access Points               *
	 * name[] = name of student                                  *
	 * grade[] = grade of student                                *
	 * location[] = location of student                          *
	 * ID[] = ID of student                                      *
	 * signal[][] = signals received for each student            *
	 * calibSignal[] = calibration signals from calibrations.dat *
	 * macAP[] = names of wifi Access Points                     *
	 * iterate = time between subsequent location updates        *
	 *************************************************************/
    private static int numCalib = 0, maxCalib = 500;
    private static String[] locationName = {"Dining", "Garage", "Kitchen", "Living Room", "Master", "My Room", "Printer", "Stairs", "TV", "Work"};
	private static int maxDev = 3000, numDev = 0;
    private static int numAP = 0, maxAP = 1000;
    private static String[] name = new String[maxDev];
    private static int[] grade = new int[maxDev];
    private static int[] location = new int[maxDev];
    private static int[] ID = new int[maxDev];
    private static double[][] deviceSignal = new double[maxDev][maxAP]; 
    private static double[][] calibSignal= new double[maxCalib][maxAP];
    private static double[][] stddev= new double[maxCalib][maxAP];
    private static String[] macAP = new String[maxAP];
	private static int iterate = 2000;

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
    
    /* Obtain JSON given URL */
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
              is.close();
        }
    }
    
    public static void printData(String formatInt, String formatStr, int ID, String name, int grade, int location) {
        
    	System.out.printf(formatInt, ID);
    	System.out.printf(formatStr, name);
    	System.out.printf(formatInt, grade);
    	System.out.printf(formatInt, location);
    	System.out.println();
    }
    
    public static void nearestLocation() {
    	for(int i = 0; i < numDev; i++) {
    		double logmaxprob = -Double.MAX_VALUE;
    		int nearestCalib = 0;
    		for(int j = 0; j < numCalib; j++) {
    			double logprob = 0;
    			for(int k = 0; k < numAP; k++) {
    	        	/* calculating probability is impossible due to miniscule values
    	        	 * therefore we calculate ln(probability) and add those up.
    	        	 * largest probability would require largest ln(probability)
    	        	 */
    	        	logprob += Math.log((1/(stddev[j][k] * Math.sqrt(2*Math.PI)))) - 0.5*Math.pow((deviceSignal[i][k] - calibSignal[j][k])/stddev[j][k], 2.0);
    			}
    			//System.out.println("ln(prob): " + logprob + "   index: " + j);
    			if(logprob > logmaxprob) {
    				logmaxprob = logprob;
    				nearestCalib = j;
    			}
    		}
    		location[i] = nearestCalib;
        	//System.out.println("Maximum ln(prob): " + logmaxprob + "   nearest index: " + nearestCalib);
    	}
    }

    /***************************
     * Read in Caibration Data *
     ***************************/
    public static void readCalibrationData() {


    	/***************************************************
    	 * Read in calibration data from calibrations.dat  *
    	 ***************************************************/
    	String Total, Wifi, Access, Calibration, Points, Average, Standard, Devation;
    	try {
    		Scanner in = new Scanner(new File("calibrations.dat"));
    		
    		/* Take care of non-data strings and then read in data*/
    		Total = in.next();
    		Wifi = in.next();
    		Access = in.next();
    		Points = in.next();
    		numAP = in.nextInt();
    		
    		Calibration = in.next();
    		Points = in.next();
    		numCalib = in.nextInt();
    		
    		/* read in MAC Addresses of wifi Access Points */
    		for(int i = 0; i < numAP; i++) {
    			macAP[i] = in.next();
    		}
    		
    		Average = in.next();
    		/* read in average signals for each calibration point */
    		for(int i = 0; i < numCalib; i++) {
        		Calibration = in.next();
        		Points = in.next();
    			for(int j = 0; j < numAP; j++) {
    				calibSignal[i][j] = in.nextDouble();
    			}
    		}
    		
    		Standard = in.next();
    		Devation = in.next();
    		/* read in standard deviation of signals for each calibration point */
    		for(int i = 0; i < numCalib; i++) {
        		Calibration = in.next();
        		Points = in.next();
    			for(int j = 0; j < numAP; j++) {
    				stddev[i][j] = in.nextDouble();
    			}
    		}

            /* print out average signals for each calibration point from each AP point */
            System.out.println("Total Wifi Access Points: " + numAP);
            System.out.println("Calibration Points: " + numCalib);
            System.out.print("                 ");
            for(int i = 0; i < numAP; i++) {
                System.out.printf("%15s", macAP[i]);
            }
            System.out.println();
            for (int i = 0; i < numCalib; i++) {
                System.out.print("Calibration ");
                System.out.printf("%3d: ", i);
                for(int j = 0; j < numAP; j++) {
                    System.out.printf("%15f", calibSignal[i][j]);
                }
                System.out.println();
            }
            System.out.println();
            
            /* Print std dev of signals from each Access Point*/
        	System.out.println("Standard Deviation: ");
            for (int i = 0; i < numCalib; i++) {
                System.out.print("Calibration ");
                System.out.printf("%3d: ", i);
                for(int j = 0; j < numAP; j++) {
                    System.out.printf("%15f", stddev[i][j]);
                }
                System.out.println();
            }
            System.out.println();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

	/**********************************
	 * Receive new data from firebase *
	 **********************************/
    public static void receiveNewData() throws IOException, JSONException {

    	
    	/************************************
    	 *    Retrieve Data from Firebase   *
    	 ************************************/
    	String url = "https://elenytics-prototype.firebaseio.com/.json";
        JSONObject json = readJsonFromUrl(url);
        
        Iterator keys = json.keys();

        /* loop to get the dynamic keys */
        while(keys.hasNext()) {
            String curKey = (String)keys.next();

            try {
            	
            	/* get the value of the dynamic key of ID JSON Objects */
            	JSONObject curObj = json.getJSONObject(curKey);
            	
            	/* parse data */
            	ID[numDev] = Integer.parseInt(curKey);
            	name[numDev] = curObj.getString("NAME");
            	grade[numDev] = curObj.getInt("GRADE");
            	location[numDev] = curObj.getInt("LOCATION");
            	
            	/* Get the MAC and strengths from each AP */
            	String[] MAC = (curObj.getString("MAC")).split(",");
            	String[] strength = (curObj.getString("STRENGTH")).split(",");
            	double[] strengths = new double[MAC.length];
            	for(int i = 0; i < MAC.length; i++)
            		strengths[i] = Integer.parseInt(strength[i]);

            	/* store signal strengths in respective order in deviceSignal */
            	/* set signal strength -100 if Access Point not detected */
            	for(int i = 0; i < numAP; i++) {
            		for(int j = 0; j < MAC.length; j++) {
            			if(MAC[j].equals(macAP[i])) {
            				deviceSignal[numDev][i] = strengths[j];
            				break;
            			}
            			deviceSignal[numDev][i] = -100;
            		}
            	}
            	
            	numDev++;
            	
            	
            	
            } catch (JSONException e) {
            	e.getStackTrace();
            }

        }
    }
    
    
    /*****************************
     *      Display results      *
     *****************************/
    public static void displayResults() {

        /* To format output */
        String formatStr = "%15s";
        String formatInt = "%15d";
        String formatFlo = "%15f";
        
        /* print MAC address of wifi Access Points */
    	System.out.print("             ");
        for(int i = 0; i < numAP; i++) {
        	System.out.printf(formatStr, macAP[i]);
        }
        System.out.println();
        
        /* print device and signals from each Access Point */
        for(int i = 0; i < numDev; i++) {
        	System.out.print("Device " + ID[i] + ": ");
            for(int j = 0; j < numAP; j++) {
            	System.out.printf(formatFlo, deviceSignal[i][j]);
            }
            System.out.println();
            System.out.println("Nearest Location: " + location[i]);
        }
        System.out.println();
    }

    public static void main(String[] args) throws IOException, JSONException, InterruptedException {
    	
    	/* For execution time purposes */
    	//final long startTime = System.currentTimeMillis();

        /***************************
         * Read in Caibration Data *
         ***************************/
    	readCalibrationData();
    	
        /***************************************************************
         * Calculate nearest position for each student every 2 seconds *
		 ***************************************************************/
        while(true) {
        	
        	/**********************************
        	 * Receive new data from firebase *
        	 **********************************/
        	receiveNewData();
            
            
            /***************************************************
             * Calculate the nearest location for each student *
             ***************************************************/
            nearestLocation();
            
            
            /*****************************
             *      Display results      *
             *****************************/
            displayResults();
            
        	/* reset */
            numDev = 0;
        	Thread.sleep(iterate);
        }
        
        /* For execution time purposes */
        /*final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) );*/
        
    }
}