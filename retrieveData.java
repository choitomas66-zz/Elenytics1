import java.util.*;
import java.io.*;
import javax.net.ssl.*;
import java.net.*;

public class retrieveData {
    public static void main(String args[]) throws FileNotFoundException, IOException {
        URL inputURL = new URL("https://s3-us-west-1.amazonaws.com/elenytics-1/inputs.out");
        String inputLine;

        /*************************
         *  retrieve inputs.out  *
         *************************/

        System.setOut(new PrintStream(new FileOutputStream(new File("inputs.out"))));
        HttpsURLConnection inCONN = (HttpsURLConnection)inputURL.openConnection();
        BufferedReader readIN = new BufferedReader(new InputStreamReader(inCONN.getInputStream()));
        while((inputLine = readIN.readLine()) != null) {
            System.out.println(inputLine);
        }


        /**************************
         *  retrieve outputs.out  *
         **************************/

        URL outputURL = new URL("https://s3-us-west-1.amazonaws.com/elenytics-1/outputs.out");
        System.setOut(new PrintStream(new FileOutputStream(new File("outputs.out"))));
        HttpsURLConnection outCONN = (HttpsURLConnection)outputURL.openConnection();
        BufferedReader readOUT = new BufferedReader(new InputStreamReader(outCONN.getInputStream()));
        while((inputLine = readOUT.readLine()) != null) {
            System.out.println(inputLine);
        }
    }
}
