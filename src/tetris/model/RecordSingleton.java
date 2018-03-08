/**
 *
 * @author Mauricio Sosa Giri (free4GOD)
 */
package tetris.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecordSingleton {

    private static RecordSingleton instance;
    private static String highScoreFile;
    private static String recordFile;
    private static long highScore;
    private static int level;
    private static int lines;
    private static long points;

    private RecordSingleton() {
        RecordSingleton.highScoreFile = "highscore.txt";
        RecordSingleton.recordFile = "recordFile.txt";
        RecordSingleton.level = 1;
        RecordSingleton.points = 0;
        try {
            RecordSingleton.setHighScore(RecordSingleton.readHighScore());
        } catch (Exception ex) {
            Logger.getLogger(RecordSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static RecordSingleton getInstance() {
        return instance;
    }

    public synchronized static void initialize() {
        if (instance != null) {
            reset();
        }
        instance = new RecordSingleton();
    }

    synchronized static void reset() {
        if (instance != null) {
            instance = new RecordSingleton();
        }
    }

    public static void setLevel(int level) {
        RecordSingleton.level = level;
    }

    public static int getLevel() {
        return RecordSingleton.level;
    }

    public static void setLines(int lines) {
        RecordSingleton.lines = lines;
    }

    public static int getLines() {
        return RecordSingleton.lines;
    }

    public static void setHighScore(long score) {
        RecordSingleton.highScore = score;
    }

    public static long getHighScore() {
        return RecordSingleton.highScore;
    }

    public static long getPoints() {
        return RecordSingleton.points;
    }

    public static void setPoints(long points) {
        RecordSingleton.points = points;
    }

    public static long readHighScore() throws Exception {
        String score;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(RecordSingleton.highScoreFile);
        } catch (FileNotFoundException ex) {
            RecordSingleton.setHighScore(0);
            RecordSingleton.writeHighScore();
            Logger.getLogger(RecordSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader reader = new BufferedReader(fileReader);
        String line = "";
        try {
            line = reader.readLine();
        } catch (IOException ex) {
            RecordSingleton.setHighScore(0);
            RecordSingleton.writeHighScore();
        }
        if (line.matches("\\d+")) {
            score = line;
        } else {
            throw new Exception("The highscore file has not numbers");
        }
        return Long.parseLong(score);
    }

    public static void writeHighScore() {
        try {
            Files.write(Paths.get(RecordSingleton.highScoreFile), String.valueOf(RecordSingleton.getHighScore()).getBytes());
        } catch (IOException ex) {
            Logger.getLogger(RecordSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RecordSingleton.recordFile, false))) {
            writer.write("" + RecordSingleton.getHighScore());
            writer.newLine();
            writer.write("" + RecordSingleton.getLevel());
            writer.newLine();
            writer.write("" + RecordSingleton.getLines());
            writer.newLine();
            writer.write("" + RecordSingleton.getPoints());
            writer.newLine();
        } catch (IOException ex) {
            Logger.getLogger(RecordSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void load() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(RecordSingleton.recordFile));
            String line = null;
            int i = 0;
            while ((line = in.readLine()) != null) {
                switch (i) {
                    case 0:
                        RecordSingleton.setHighScore(Long.parseLong(line));
                        break;
                    case 1:
                        RecordSingleton.setLevel(Integer.parseInt(line));
                        break;
                    case 2:
                        RecordSingleton.setLines(Integer.parseInt(line));
                        break;
                    case 3:
                        RecordSingleton.setPoints(Long.parseLong(line));
                        break;
                }
                i++;
                if (i >= 4) {
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BoardSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
