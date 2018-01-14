import java.util.*;
import java.io.*;


public class Analyze {

    private static final int maxLength = 100;
    private static final int maxWidth = 100;
    private static int width = 21;
    private static int length = 15;
    private static int[][] error = new int[length][width];

    public static void main(String args[]) throws FileNotFoundException {
        Scanner in = new Scanner(new File("results.txt"));
        PrintWriter writer = new PrintWriter(new File("analysis.txt"));

        String data[], line;
        int x,y, tx, ty;
        while(in.hasNextLine()) {
            line = in.nextLine();
            data = line.split(" ");
            x = Integer.parseInt(data[1]);
            y = Integer.parseInt(data[0]);
            tx = Integer.parseInt(data[3]);
            ty = Integer.parseInt(data[2]);
            error[tx][ty] = (x==tx && y==ty) ? (error[tx][ty]) : (error[tx][ty]+1);
        }

        for(int i = 0; i < length; i++) {
            for(int j = 0; j < width; j++) {
                writer.write(i + ", " + j + ": " + error[i][j] + "\n");
            }
        }
        writer.close();
    }
}
