import java.util.*;
import java.lang.*;
import java.io.*;

public class Calibrate {
    private static int maxAP = 1000;
    private static int maxLoc = 1000;
    private static int numAP = 0;
    private static int numLoc = 0;
    private static HashMap<String, Integer> index = new HashMap<String, Integer>();
    private static String[] AP = new String[maxAP];
    private static HashMap<String, ArrayList<Double>> meanSignal = new HashMap<String, ArrayList<Double>>();
    private static HashMap<String, ArrayList<Double>> stdDev = new HashMap<String, ArrayList<Double>>();
    private static int numCorrect = 0;

    /**********************************
     *   print results to csv files   *
     **********************************/
    public static void printResults() throws FileNotFoundException {
        File file = new File("aveSignals.csv");
        FileOutputStream fos = new FileOutputStream(file);
        PrintStream ps = new PrintStream(fos);
        System.setOut(ps);

        ArrayList<Double> aveSignal, std;

        /* Print out AP */
        for(int i = 0; i < numAP; i++) {
            System.out.print("," + AP[i]);
        }
        System.out.println();

        for(String key : meanSignal.keySet()) {
            System.out.print(key);
            aveSignal = (ArrayList<Double>)meanSignal.get(key);
            for(int i = 0; i < numAP; i++) {
                System.out.print("," + aveSignal.get(i));
            }
            System.out.println();
        }

        for(int i = 0; i < numAP; i++) {
            System.out.print(","+AP[i]);
        }
        System.out.println();

        for(String key : stdDev.keySet()) {
            System.out.print(key);
            std = (ArrayList<Double>)stdDev.get(key);
            for(int i = 0; i < numAP; i++) {
                System.out.print("," + std.get(i));
            }
            System.out.println();
        }
    }


    /**********************
     *   set count as 0   *
     **********************/
    public static void init() {
    }


    /*************************************
     *   Read in Data and store average  *
     *************************************/
    public static void loadData() throws FileNotFoundException {
        String[] data;
        String line;

        /* Open training.csv for training data */
        Scanner in = new Scanner(new File("training.csv"));

        /*****************************
         *   Read in mac addresses   *
         *****************************/
        /*line = in.nextLine();
        data = line.split(",");
        numAP = data.length - 1;
        for(int i = 0; i < numAP; i++) {
            AP[i] = data[i];
        }*/

        /******************************************
         *  Parse and store average signal data   *
         ******************************************/
        while(in.hasNextLine()) {
            line = in.nextLine();
            data = line.split(",");
            numAP = data.length-1;
            String location = data[numAP];//+","+data[numAP+1]+","+data[numAP+2];
            ArrayList<Double> signals;

            /* if it's the first reading of a location, simply add to hash */
            if(!index.containsKey(location)) {
                numLoc++;
                signals = new ArrayList<Double>();
                for(int i = 0; i < numAP; i++)
                    signals.add(Double.parseDouble(data[i]));
                index.put(location, 1);
            }

            /* else update mean Signal */
            else {
                signals = meanSignal.get(location);
                //System.out.print(signals);
                for(int i = 0; i < numAP; i++) {
                    signals.set(i, signals.get(i)*index.get(location));
                    signals.set(i, signals.get(i) + Double.parseDouble(data[i]));
                    signals.set(i, signals.get(i)/(index.get(location)+1));
                }
                index.put(location, index.get(location)+1);
            }
            meanSignal.put(location, signals);
        }
    }

    /*************************
     *   Calculate std dev   *
     *************************/
    public static void calculate() throws FileNotFoundException {
        Scanner in = new Scanner(new File("trainingset.csv"));
        String line;
        String[] data;
        /* ignore first line */
        line = in.nextLine();

        init();
        /* init stdDev with 0 sum */
        for(String key : index.keySet()) {
            ArrayList<Double> std = new ArrayList<Double>();
            for(int j = 0; j < numAP; j++) {
                std.add(0.0);
            }
            stdDev.put(key, std);
        }

        while(in.hasNextLine()) {
            line = in.nextLine();
            data = line.split(",");
            //System.out.println(numAP);
            //System.out.println(line);
            String loc = data[numAP];//+","+data[numAP+1]+","+data[numAP+2];
            //System.out.println(loc);
            //System.out.println(data.length);

            /* last element is class */
            ArrayList<Double> std = new ArrayList<Double>();
            std = (ArrayList<Double>)stdDev.get(loc);

            /* retrieve std double array for a certain location and assign variance */
            for(int i = 0; i < numAP; i++) {
                double signal = Double.parseDouble(data[i]);
                std.set(i, std.get(i) + (signal-(meanSignal.get(loc)).get(i))*(signal-(meanSignal.get(loc)).get(i)));
                if(std.get(i) < 0.000001) {
                    std.set(i, 0.1);
                }
            }
            stdDev.put(loc, std);
        }
        /* now calculate stddev from variance */
        for(String i : stdDev.keySet()) {
            ArrayList<Double> std = stdDev.get(i);
            for(int j = 0; j < numAP; j++) {
                std.set(j, Math.sqrt(std.get(j)/index.get(i)));
                stdDev.put(i, std);
            }
        }
    }

    public static void nearestLocation() throws FileNotFoundException {
        Scanner in = new Scanner(new File("testData.csv"));
        System.setOut(new PrintStream(new FileOutputStream(new File("results.csv"))));
        String line, data[], real;
        String minIndex = "null";
        double prob, minprob;
        ArrayList<Double> std, readings = new ArrayList<Double>(), ave;
        int numTest = 0;

        while(in.hasNextLine()) {
            minprob = Double.MAX_VALUE;
            line = in.nextLine();
            data = line.split(",");
            real = data[numAP];//+","+data[numAP+1]+","+data[numAP+2];

            for(int i = 0; i < numAP; i++)
                readings.add(i, Double.parseDouble(data[i]));
            for(String key : stdDev.keySet()) {
                prob = 0;
                std = (ArrayList<Double>)stdDev.get(key);
                ave = (ArrayList<Double>)meanSignal.get(key);
                for(int i = 0; i < numAP; i++) {
                    prob += 0.5*((readings.get(i)-ave.get(i))/std.get(i))*((readings.get(i)-ave.get(i))/std.get(i));
                    //System.out.println("key: " + key + "      prob: " + prob);
                }
                if(prob < minprob) {
                    minprob = prob;
                    minIndex = key;
                }
            }
            System.out.println(real+"     "+minIndex);
            if(real.equals(minIndex)) {
                numCorrect++;
            }
            numTest++;
        }
        System.out.println(numCorrect + " correct out of " + numTest);
    }

    public static void main(String args[]) throws FileNotFoundException {
        init();
        loadData();
        calculate();
        printResults();
        nearestLocation();
    }
}
