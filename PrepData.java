import java.util.*;
import java.lang.*;
import java.io.*;

public class PrepData {

    private static final int maxCalib = 10000;
    private static final int maxLoc = 1000;
    private static final int maxAP = 1000;
    private static int numAP = 0, numCalib = 0;
    private static final int sigDefault = -127;
    private static final int iter = 10;
    private static LinkedHashMap<String, Integer> APmac = new LinkedHashMap<String, Integer>();
    private static LinkedHashMap<String, Double> normalized = new LinkedHashMap<String, Double>();
    private static int minC = 0, maxC;
    private static String[] location = new String[maxCalib];
    private static int[] output = new int[maxLoc];
    private static int index = -1;

    /****************************************************
     *  read in and record all discovered APs in hash   *
     ****************************************************/
    public static void readAP() throws FileNotFoundException {
        try {
            Scanner in = new Scanner(new File("inputs.out"));
            String line;
            String[] data;
            String[] pair;

            while(in.hasNextLine()) {
                line = in.nextLine();
                if(!line.equals("")) {
                    if(!line.equals("#")) {
                        data = line.split(", ");

                        for(int i = 0; i < data.length; i++) {
                            pair = data[i].split(",");
                            APmac.put(pair[0], sigDefault);
                        }
                        numCalib++;
                    }
                }
            }
            numAP = APmac.size();

            /* print out all the APs */
            PrintWriter p = new PrintWriter(new File("APs.out"));
            Set set = APmac.entrySet();
            Iterator iterator = set.iterator();
            Map.Entry next = (Map.Entry)iterator.next();
            Object bleh = next.getKey();
            p.write(bleh + "\n");
            while(iterator.hasNext()) {
                next = (Map.Entry)iterator.next();
                bleh = next.getKey();
                p.write(bleh + "\n");
            }
            p.flush();
            p.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /***************************************************
     *   Read the Calibration Point from outputs.out   *
     ***************************************************/
    public static void readLocation() {
        int count = 0;
        try {
            Scanner in = new Scanner(new File("outputs.out"));
            String loc, data[];
            while(in.hasNextLine()) {

                loc = in.nextLine();
                data = loc.split(", ");
                loc = "";
                for(int i = 0; i < data.length; i++)
                    loc += "," + data[i];
                location[count] = loc;
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /************************************************************
     *   Read line by line and export data to calibration.csv   *
     ************************************************************/
    public static void export() {
        try{
            Scanner in = new Scanner(new File("inputs.out"));

            String line;
            String[] data;
            String[] pair;

            /* Redirect output to training.csv and trainingWEKA.csv */

            /* trainingWEKA.csv has AP headers */
            PrintWriter writer = new PrintWriter("trainingNorm.csv", "UTF-8");
            /*for(String key : APmac.keySet()) {
                writer.print(key + ",");
            }*/
            //writer.println("pos");

            PrintWriter writer2 = new PrintWriter("training.csv", "UTF-8");

            while(in.hasNextLine()) {

                /* Reset signal values to sigDefault */
                for(String key : APmac.keySet()) {
                    APmac.put(key, sigDefault);
                    normalized.put(key, 1+sigDefault/127.0);
                }

                /* Read in new values  */
                line = in.nextLine();

                /* Ignore and update location */
                if(line.equals("#")) {
                    index++;
                }

                else if(line.equals("")) {

                }

                /* Parse data and update hash */
                else {
                    data = line.split(", ");
                    for(int i = 0 ; i < data.length; i++) {
                        pair = data[i].split(",");
                        APmac.put(pair[0], Integer.parseInt(pair[1]));
                        normalized.put(pair[0], 1+1.0*(Integer.parseInt(pair[1])/127.0));
                    }


    		        /* print out values */
            		/* Generating a Set of entries */
                    Set set = APmac.entrySet();
                    Iterator iterator = set.iterator();
            		Map.Entry next = (Map.Entry)iterator.next();
                    Object bleh = next.getValue();
                    writer2.print(bleh);
            		while(iterator.hasNext()) {
            		    next = (Map.Entry)iterator.next();
                        bleh = next.getValue();
                        writer2.print("," + bleh);
            		}
                    writer2.println(location[index]);

                    /* for normalized */
                    set = normalized.entrySet();
                    iterator = set.iterator();
                    next = (Map.Entry)iterator.next();
                    bleh = next.getValue();
                    writer.print(bleh);
                    while(iterator.hasNext()) {
                        next = (Map.Entry)iterator.next();
                        bleh = next.getValue();
                        writer.print("," + bleh);
                    }
                    writer.println(location[index]);
                }
    		}
            writer.close();
            writer2.close();
		} catch(Exception e) {
		    e.printStackTrace();
		}
    }

    /*public static void uploadtoS3() {
        try
        {
            HTTP_post post = new HTTP_post();
            post.u = new URL("https://s3-us-west-1.amazonaws.com/elenytics-1/outputs.out");

        }
    }*/

    public static void main(String args[]) throws FileNotFoundException {
        readAP();
        readLocation();
        export();
       // uploadtoS3();
    }

}
