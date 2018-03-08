/**
 *
 * @author Mauricio Sosa Giri (free4GOD)
 */
package tetris.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BoardSingleton {

    private static BoardSingleton instance;
    private static int[][] board;
    private static int width;
    private static int height;
    private static long square;
    private static long piece;
    private static long activePiece;
    private static long nextPiece;
    private static String boardFile;

    private BoardSingleton() {
        BoardSingleton.width = 20;
        BoardSingleton.height = 32;
        BoardSingleton.board = new int[BoardSingleton.width][BoardSingleton.height];
        BoardSingleton.square = 0;
        BoardSingleton.piece = 0;
        BoardSingleton.boardFile = "boardFile.txt";
    }

    public static BoardSingleton getInstance() {
        return instance;
    }

    public synchronized static void initialize() {
        if (instance != null) {
            reset();
        }
        instance = new BoardSingleton();
    }

    public synchronized static void reset() {
        if (instance != null) {
            instance = new BoardSingleton();
        }
    }

    public static long getSquare() {
        return BoardSingleton.square;
    }

    public static void setSquare(long square) {
        BoardSingleton.square = square;
    }

    public static long getPiece() {
        return BoardSingleton.piece;
    }

    public static void setPiece(long piece) {
        BoardSingleton.piece = piece;
    }

    public static long getActivePiece() {
        return BoardSingleton.activePiece;
    }

    public static void setActivePiece(long piece) {
        BoardSingleton.activePiece = piece;
    }

    public static long getNextPiece() {
        return BoardSingleton.nextPiece;
    }

    public static void setNextPiece(long piece) {
        BoardSingleton.nextPiece = piece;
    }

    public static int[][] getBoard() {
        return BoardSingleton.board;
    }

    public static void setBoard(int[][] board) {
        BoardSingleton.board = board;
    }

    public static int getWidth() {
        return BoardSingleton.width;
    }

    public static void setWidth(int width) {
        BoardSingleton.width = width;
    }

    public static int getHeight() {
        return BoardSingleton.height;
    }

    public static void setHeight(int height) {
        BoardSingleton.height = height;
    }

    public static void clear() {
        BoardSingleton.setBoard(new int[BoardSingleton.getWidth()][BoardSingleton.getHeight()]);
    }

    public static void clearX(int x) {
        if (x < BoardSingleton.width) {
            for (int y = 0; y < BoardSingleton.height; y++) {
                BoardSingleton.board[x][y] = 0;
            }
        } else {
            throw new NullPointerException("The board has only " + BoardSingleton.width + " width and you passed " + x);
        }
    }

    public static void clearY(int y) {
        if (y < BoardSingleton.height) {
            for (int x = 0; x < BoardSingleton.width; x++) {
                BoardSingleton.board[x][y] = 0;
            }
        } else {
            throw new NullPointerException("The board has only " + BoardSingleton.height + " height and you passed " + y);
        }
    }

    public static void print() {
        System.out.println("Print board matrix");
        for (int x = 0; x < BoardSingleton.width; x++) {
            String space;
            if (x < 10) {
                space = "  ";
            } else {
                space = " ";
            }
            System.out.print(x + space);
        }
        System.out.print("\n");
        for (int y = 0; y < BoardSingleton.height; y++) {
            for (int[] board1 : BoardSingleton.board) {
                System.out.print(board1[y] + "  ");
            }
            System.out.print(" = " + y);
            System.out.print("\n");
        }
        System.out.print("\n");
    }

    public static List<Integer> getLinesCompleted() {
        List<Integer> linesCompleted = new ArrayList<>();
        boolean lineCompleted;
        for (int y = 0; y < BoardSingleton.height; y++) {
            lineCompleted = true;
            for (int x = 0; x < BoardSingleton.width; x++) {
                if (BoardSingleton.board[x][y] == 0) {
                    lineCompleted = false;
                    break;
                }
            }
            if (lineCompleted) {
                linesCompleted.add(y);
            }
        }
        return linesCompleted;
    }

    public static void moveDown(int maxY) {
        for (int y = maxY; y >= 0; y--) {
            for (int x = 0; x < BoardSingleton.width; x++) {
                if (y + 1 <= maxY && BoardSingleton.board[x][y] == 0) {
                    BoardSingleton.board[x][y + 1] = 0;
                }
                if (y - 1 >= 0 && BoardSingleton.board[x][y - 1] == 1) {
                    BoardSingleton.board[x][y] = 1;
                }
            }
        }
    }

    public static boolean setPosition(int x, int y, int i) {
        if (x >= BoardSingleton.width || y < 0 || x < 0 || y >= BoardSingleton.height) {
            return false;
        }
        BoardSingleton.board[x][y] = i;
        return true;
    }

    public static int getValue(int x, int y) {
        if (x >= BoardSingleton.width || y < 0 || x < 0 || y >= BoardSingleton.height) {
            return -1;
        }
        return BoardSingleton.board[x][y];

    }

    public static String getLineY(int y) {
        String result = "";
        for (int x = 0; x < BoardSingleton.getWidth(); x++) {
            result += "" + getValue(x, y) + " ";
        }
        return result;
    }

    public static void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(BoardSingleton.boardFile, false));
            writer.write("" + BoardSingleton.getWidth());
            writer.newLine();
            writer.write("" + BoardSingleton.getHeight());
            writer.newLine();
            writer.write("" + BoardSingleton.getSquare());
            writer.newLine();
            writer.write("" + BoardSingleton.getPiece());
            writer.newLine();
            writer.write("" + BoardSingleton.getActivePiece());
            writer.newLine();
            writer.write("" + BoardSingleton.getNextPiece());
            writer.newLine();
            for (int y = 0; y < BoardSingleton.getHeight(); y++) {
                writer.write(BoardSingleton.getLineY(y));
                writer.newLine();
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(RecordSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void load() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(BoardSingleton.boardFile));
            String line = null;
            int i = 0;
            int y = 0;
            while ((line = in.readLine()) != null) {
                switch (i) {
                    case 0:
                        BoardSingleton.setWidth(Integer.parseInt(line));
                        break;
                    case 1:
                        BoardSingleton.setHeight(Integer.parseInt(line));
                        break;
                    case 2:
                        BoardSingleton.setSquare(Long.parseLong(line));
                        break;
                    case 3:
                        BoardSingleton.setPiece(Long.parseLong(line));
                        break;
                    case 4:
                        BoardSingleton.setActivePiece(Long.parseLong(line));
                        break;
                    case 5:
                        BoardSingleton.setNextPiece(Long.parseLong(line));
                        break;
                }
                if (i <= 5) {
                    i++;
                }
                String[] strX = line.split(" ");
                if (strX.length == BoardSingleton.getWidth()) {
                    for (int x = 0; x < BoardSingleton.getWidth(); x++) {
                        BoardSingleton.setPosition(x, y, Integer.parseInt(strX[x]));
                    }
                    y++;
                }
                if (y >= BoardSingleton.getHeight()) {
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BoardSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
