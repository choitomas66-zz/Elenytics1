
import java.util.*;
import java.io.*;

public class Visual {

    public static final int width = 12;
    public static final int length = 10;
    private static int[][] grid = new int[length][width];
    private static String[][] output = new String[length][width];
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static int x1, y1, x2, y2;

    public static void init() throws FileNotFoundException {
        Scanner in = new Scanner(new File("gridLayout.csv"));
        int num, x1, y1i, x2, y2;
        String line, data[];

        for(int i = 0; i < length; i++) {
            for(int j = 0; j < width; j++) {
                grid[i][j] = length*width+1;
            }
        }

        num = Integer.parseInt(in.nextLine());
        for(int i = 0; i < num; i++) {
             line = in.nextLine();
             data = line.split(",");
             /* coord1, coord2, val */
             grid[Integer.parseInt(data[0])][Integer.parseInt(data[1])] = Integer.parseInt(data[2]);
        }
    }


    public static void prepGrid(int a, int b) {
        for(int i = 0; i < length; i++) {
            for(int j = 0; j < width; j++) {
                if(grid[i][j] == a) {
                    if(a < 10)
                        output[i][j] = ANSI_RED + "0" + String.valueOf(a) + ANSI_RESET;
                    else
                        output[i][j] = ANSI_RED + String.valueOf(a) + ANSI_RESET;
                    x1 = i;
                    y1 = j;
                }
                else if (grid[i][j] == b) {
                    if(b < 10)
                        output[i][j] = ANSI_GREEN + "0" + String.valueOf(b) + ANSI_RESET;
                    else
                        output[i][j] = ANSI_GREEN + String.valueOf(b) + ANSI_RESET;
                    x2 = i;
                    y2 = j;
                }
                else {
                    if(grid[i][j] < 10)
                        output[i][j] = ANSI_BLUE + "0" + String.valueOf(grid[i][j]) + ANSI_RESET;
                    else if(grid[i][j] == length*width+1)
                        output[i][j] = "  ";
                    else
                        output[i][j] = ANSI_BLUE + String.valueOf(grid[i][j]) + ANSI_RESET;

                }
            }
        }
    }

    public static void display() throws FileNotFoundException {
        //File file = new File("graph.out");
        //FileOutputStream fos = new FileOutputStream(file);
        //PrintStream ps = new PrintStream(fos);
        //System.setOut(ps);

        String line, data[];
        Scanner in = new Scanner(new File("results.csv"));

        while(in.hasNextLine()) {
            line = in.nextLine();
            data = line.split(",");
            prepGrid(Integer.parseInt(data[0]), Integer.parseInt(data[1]));


            System.out.print("-");
            for(int i = 0; i < width; i++) {
                System.out.print("-------");
            }
            System.out.println();
            for(int i = 0; i < length; i++) {
                System.out.print("|");
                for(int j = 0; j < width; j++) {
                    System.out.print("      |");
                }
                System.out.println();
                System.out.print("|");
                for(int j = 0; j < width; j++) {
                    System.out.print("  ");
                    System.out.print(ANSI_GREEN + output[i][j] + ANSI_RESET);
                    System.out.print("  |");
                }
                System.out.println();
                System.out.print("|");
                for(int j = 0; j < width; j++) {
                    System.out.print("      |");
                }
                System.out.println();
                for(int j = 0; j < width; j++) {
                    System.out.print("-------");
                }
                System.out.println();
            }
            if(Math.abs(Integer.parseInt(data[0])-Integer.parseInt(data[1])) <= 2) {
                for(int i = 0; i < width; i++) {
                    System.out.print("       ");
                }
                System.out.print("+1");
            }
            System.out.println();
        }
    }

    public static void main(String args[]) throws FileNotFoundException{
        init();
        display();
    }
}
