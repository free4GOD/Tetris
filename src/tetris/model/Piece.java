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
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Piece {

    private String name;
    private boolean active;
    private int maxRotation;
    private int rotate;
    private List<Square> squares;
    private int maxX;
    private int minX;
    private int maxY;
    private int minY;
    private long number;
    final BoardSingleton board;

    public Piece(String name) {
        this.name = name;
        this.active = false;
        this.squares = new ArrayList<>();
        this.maxRotation = 0;
        this.rotate = 0;
        this.board = BoardSingleton.getInstance();
    }

    public void setMaxRotation(int rotations) {
        this.maxRotation = rotations;
    }

    public int getMaxRotation() {
        return this.maxRotation;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public int getRotate() {
        return this.rotate;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNumber() {
        return this.number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Square> getSquares() {
        return this.squares;
    }

    public void setSquares(List<Square> squares) {
        this.squares = squares;
    }

    public void addSquares(List<Square> squares) {
        this.squares.addAll(squares);
    }

    public void removeSquares(List<Square> squares) {
        this.squares.removeAll(squares);
    }

    public void addSquare(Square square) {
        this.squares.add(square);
    }

    public void removeSquare(Square square) {
        this.squares.remove(square);
    }

    public void setColor(String color) {
        this.squares.forEach(new Consumer<Square>() {
            @Override
            public void accept(Square s) {
                s.setColor(color);
            }
        });
    }

    public Piece rotateRight(Piece p) {
        this.rotate++;
        if (this.rotate >= this.maxRotation) {
            this.rotate = 0;
        }
        return this.rotate();
    }

    public Piece rotateLeft(Piece p) {
        this.rotate--;
        if (this.rotate < 0) {
            this.rotate = this.maxRotation - 1;
        }
        return this.rotate();
    }

    public void create() {
        Square s;
        switch (name) {
            case "Long":
                for (int i = 0; i < 5; i++) {
                    s = new Square(9, i);
                    this.addSquare(s);
                }
                this.maxRotation = 2;
                this.setColor("RED");
                break;
            case "L":
                for (int i = 0; i < 3; i++) {
                    s = new Square(9, i);
                    this.addSquare(s);
                }
                s = new Square(10, 2);
                this.addSquare(s);
                this.maxRotation = 4;
                this.setColor("YELLOW");
                break;
            case "Inverse L":
                for (int i = 0; i < 3; i++) {
                    s = new Square(10, i);
                    this.addSquare(s);
                }
                s = new Square(9, 2);
                this.addSquare(s);
                this.maxRotation = 4;
                this.setColor("MAGENTA");
                break;
            case "Cube":
                for (int i = 0; i < 2; i++) {
                    s = new Square(i + 9, 0);
                    this.addSquare(s);
                }
                for (int i = 0; i < 2; i++) {
                    s = new Square(i + 9, 1);
                    this.addSquare(s);
                }
                this.maxRotation = 0;
                this.setColor("BLUE");
                break;
            case "S":
                for (int i = 1; i < 3; i++) {
                    s = new Square(i + 9, 0);
                    this.addSquare(s);
                }
                for (int i = 0; i < 2; i++) {
                    s = new Square(i + 9, 1);
                    this.addSquare(s);
                }
                this.maxRotation = 2;
                this.setColor("CYAN");
                break;
            case "Inverse S":
                for (int i = 0; i < 2; i++) {
                    s = new Square(i + 9, 0);
                    this.addSquare(s);
                }
                for (int i = 1; i < 3; i++) {
                    s = new Square(i + 9, 1);
                    this.addSquare(s);
                }
                this.maxRotation = 2;
                this.setColor("ORANGE");
                break;
            case "T":
                for (int i = 0; i < 3; i++) {
                    s = new Square(i + 9, 1);
                    this.addSquare(s);
                }
                s = new Square(10, 0);
                this.addSquare(s);
                this.maxRotation = 4;
                this.setColor("GREEN");
                break;
            case "Big":
                for (int i = 0; i < 10; i++) {
                    s = new Square(i + 9, 0);
                    this.addSquare(s);
                }
                for (int i = 0; i < 10; i++) {
                    s = new Square(i + 9, 1);
                    this.addSquare(s);
                }
                this.maxRotation = 2;
                this.setColor("WHITE");
                break;
            case "Very Big":
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 5; j++) {
                        s = new Square(i + 4, j);
                        this.addSquare(s);
                    }
                }
                this.maxRotation = 2;
                this.setColor("GREY");
                break;
        }
        Iterator i = this.getSquares().iterator();
        while (i.hasNext()) {
            s = (Square) i.next();
            s.setNumber(BoardSingleton.getSquare());
            BoardSingleton.setSquare(1 + BoardSingleton.getSquare());
        }
    }

    public Piece rotate() {
        maxY = this.getMaxY();
        minY = this.getMinY();
        minX = this.getMinX();
        maxX = this.getMaxX();
        this.removeSquares(this.getSquares());
        Square s;
        switch (name) {
            case "Long":
                if (this.rotate == 0) {

                    for (int i = 0; i < 5; i++) {
                        s = new Square(maxX - 2, minY + i);
                        this.addSquare(s);
                    }
                } else {
                    for (int i = 0; i < 5; i++) {
                        s = new Square(maxX + i - 2, minY);
                        this.addSquare(s);
                    }
                }
                this.setColor("RED");
                break;
            case "L":
                switch (this.rotate) {
                    case 0:
                        for (int i = 0; i < 3; i++) {
                            s = new Square(minX - 1, minY + i);
                            this.addSquare(s);
                        }
                        s = new Square(minX, minY + 2);
                        this.addSquare(s);
                        break;
                    case 1:
                        for (int i = 0; i < 3; i++) {
                            s = new Square(minX + i, minY);
                            this.addSquare(s);
                        }
                        s = new Square(minX, minY + 1);
                        this.addSquare(s);
                        break;
                    case 2:
                        for (int i = 0; i < 3; i++) {
                            s = new Square(minX + 1, minY + i);
                            this.addSquare(s);
                        }
                        s = new Square(minX, minY);
                        this.addSquare(s);
                        break;
                    case 3:
                        for (int i = 0; i < 3; i++) {
                            s = new Square(minX + i - 1, minY + 1);
                            this.addSquare(s);
                        }
                        s = new Square(minX + 1, minY);
                        this.addSquare(s);
                        break;
                }
                this.setColor("YELLOW");
                break;
            case "Inverse L":
                switch (this.rotate) {
                    case 0:
                        for (int i = 0; i < 3; i++) {
                            s = new Square(minX, minY + i);
                            this.addSquare(s);
                        }
                        s = new Square(minX - 1, this.getMinY() + 2);
                        this.addSquare(s);
                        break;
                    case 1:
                        for (int i = 0; i < 3; i++) {
                            s = new Square(minX + i, minY + 1);
                            this.addSquare(s);
                        }
                        s = new Square(minX, minY);
                        this.addSquare(s);
                        break;
                    case 2:
                        for (int i = 0; i < 3; i++) {
                            s = new Square(minX, minY + i);
                            this.addSquare(s);
                        }
                        s = new Square(minX + 1, minY);
                        this.addSquare(s);
                        break;
                    case 3:
                        for (int i = 0; i < 3; i++) {
                            s = new Square(minX + i, minY);
                            this.addSquare(s);
                        }
                        s = new Square(minX + 2, minY + 1);
                        this.addSquare(s);
                        break;
                }
                this.setColor("MAGENTA");
                break;
            case "Cube":
                for (int i = 0; i < 2; i++) {
                    s = new Square(minX + i, minY);
                    this.addSquare(s);
                }
                for (int i = 0; i < 2; i++) {
                    s = new Square(minX + i, minY + 1);
                    this.addSquare(s);
                }
                this.setColor("BLUE");
                break;
            case "S":
                if (this.rotate == 0) {
                    for (int i = 0; i < 2; i++) {
                        s = new Square(minX + i, minY);
                        this.addSquare(s);
                    }
                    for (int i = 0; i < 2; i++) {
                        s = new Square(minX + i - 1, minY + 1);
                        this.addSquare(s);
                    }
                } else {
                    for (int i = 0; i < 2; i++) {
                        s = new Square(minX, minY + i);
                        this.addSquare(s);
                    }
                    for (int i = 0; i < 2; i++) {
                        s = new Square(minX + 1, minY + i + 1);
                        this.addSquare(s);
                    }
                }
                this.setColor("CYAN");
                break;
            case "Inverse S":
                if (this.rotate == 0) {
                    for (int i = 0; i < 2; i++) {
                        s = new Square(minX + i - 1, minY);
                        this.addSquare(s);
                    }
                    for (int i = 0; i < 2; i++) {
                        s = new Square(minX + i, minY + 1);
                        this.addSquare(s);
                    }
                } else {
                    for (int i = 0; i < 2; i++) {
                        s = new Square(minX + 1, minY + i);
                        this.addSquare(s);
                    }
                    for (int i = 0; i < 2; i++) {
                        s = new Square(minX, minY + i + 1);
                        this.addSquare(s);
                    }
                }
                this.setColor("ORANGE");
                break;
            case "T":
                switch (this.rotate) {
                    case 0:
                        for (int i = 0; i < 3; i++) {
                            s = new Square(i + minX, minY + 1);
                            this.addSquare(s);
                        }
                        s = new Square(minX + 1, minY);
                        this.addSquare(s);
                        break;
                    case 1:
                        for (int i = 0; i < 3; i++) {
                            s = new Square(minX, minY + i);
                            this.addSquare(s);
                        }
                        s = new Square(minX + 1, minY + 1);
                        this.addSquare(s);
                        break;
                    case 2:
                        for (int i = 0; i < 3; i++) {
                            s = new Square(minX + i, minY);
                            this.addSquare(s);
                        }
                        s = new Square(minX + 1, minY + 1);
                        this.addSquare(s);
                        break;
                    case 3:
                        for (int i = 0; i < 3; i++) {
                            s = new Square(minX, minY + i);
                            this.addSquare(s);
                        }
                        s = new Square(minX - 1, minY + 1);
                        this.addSquare(s);
                        break;
                }
                this.setColor("GREEN");
                break;
            case "Big":
                if (this.rotate == 0) {
                    for (int i = 0; i < 10; i++) {
                        s = new Square(i + minX, minY);
                        this.addSquare(s);
                    }
                    for (int i = 0; i < 10; i++) {
                        s = new Square(i + minX, minY + 1);
                        this.addSquare(s);
                    }
                } else {
                    for (int i = 0; i < 10; i++) {
                        s = new Square(minX - 1, minY + i);
                        this.addSquare(s);
                    }
                    for (int i = 0; i < 10; i++) {
                        s = new Square(minX, minY + i);
                        this.addSquare(s);
                    }
                }
                this.setColor("WHITE");
                break;
            case "Very Big":
                if (this.rotate == 0) {
                    for (int i = 0; i < 10; i++) {
                        for (int j = 0; j < 5; j++) {
                            s = new Square(i + 4, j);
                            this.addSquare(s);
                        }
                    }
                } else {
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 10; j++) {
                            s = new Square(i + 4, j);
                            this.addSquare(s);
                        }
                    }
                }
                this.setColor("GREY");
        }
        Iterator i = this.getSquares().iterator();
        while (i.hasNext()) {
            s = (Square) i.next();
            s.setNumber(BoardSingleton.getSquare());
            BoardSingleton.setSquare(1 + BoardSingleton.getSquare());
        }
        return this;
    }

    public int getMaxX() {
        Iterator<Square> iterator1 = this.getSquares().iterator();
        Iterator<Square> iterator2 = this.getSquares().iterator();
        while (iterator1.hasNext()) {
            Square s1 = iterator1.next();
            while (iterator2.hasNext()) {
                Square s2 = iterator2.next();
                if (s2.getX() >= s1.getX()) {
                    this.maxX = s2.getX();
                }
            }
        }
        return this.maxX;
    }

    public int getMaxY() {
        Iterator<Square> iterator1 = this.getSquares().iterator();
        Iterator<Square> iterator2 = this.getSquares().iterator();
        while (iterator1.hasNext()) {
            Square s1 = iterator1.next();
            while (iterator2.hasNext()) {
                Square s2 = iterator2.next();
                if (s2.getY() >= s1.getY()) {
                    this.maxY = s2.getY();
                }
            }
        }
        return this.maxY;
    }

    public int getMinX() {
        Iterator<Square> iterator1 = this.getSquares().iterator();
        Iterator<Square> iterator2 = this.getSquares().iterator();
        while (iterator1.hasNext()) {
            Square s1 = iterator1.next();
            while (iterator2.hasNext()) {
                Square s2 = iterator2.next();
                if (s1.getX() <= s2.getX()) {
                    this.minX = s1.getX();
                }
            }
        }
        return this.minX;
    }

    public int getMinY() {
        Iterator<Square> iterator1 = this.getSquares().iterator();
        Iterator<Square> iterator2 = this.getSquares().iterator();
        while (iterator1.hasNext()) {
            Square s1 = iterator1.next();
            while (iterator2.hasNext()) {
                Square s2 = iterator2.next();
                if (s1.getY() <= s2.getY()) {
                    this.minY = s1.getY();
                }
            }
        }
        return this.minY;
    }

    public static void save(String pieceFile, List<Piece> allPieces) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(pieceFile, false));
            Iterator i = allPieces.iterator();
            while (i.hasNext()) {
                Piece p = (Piece) i.next();
                writer.write("piece");
                writer.newLine();
                writer.write("" + p.getName());
                writer.newLine();
                writer.write("" + p.getActive());
                writer.newLine();
                writer.write("" + p.getMaxRotation());
                writer.newLine();
                writer.write("" + p.getRotate());
                writer.newLine();
                writer.write("" + p.getNumber());
                writer.newLine();
                Iterator j = p.getSquares().iterator();
                while (j.hasNext()) {
                    Square s = (Square) j.next();
                    writer.write("square");
                    writer.newLine();
                    writer.write("" + s.getX());
                    writer.newLine();
                    writer.write("" + s.getY());
                    writer.newLine();
                    writer.write("" + s.getColor());
                    writer.newLine();
                    writer.write("" + s.getNumber());
                    writer.newLine();
                }
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Piece.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static List[] load(String pieceFile) {
        List[] result = new List[2];
        List<Piece> lp = new ArrayList<>();
        List<Square> ls = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(pieceFile));
            String line = "";
            boolean isPiece = false;
            boolean isSquare = false;
            int i = 0;
            int j = 0;
            int x = -1;
            int y = -1;
            int z = 0;
            Square s = null;
            Piece p = null;
            while ((line = in.readLine()) != null) {
                if (isPiece) {
                    if (i == 4) {
                        p.setNumber(Long.parseLong(line));
                        lp.add(p);
                        isPiece = false;
                        i++;
                    }
                    if (i == 3) {
                        p.setRotate(Integer.parseInt(line));
                        i++;
                    }
                    if (i == 2) {
                        p.setMaxRotation(Integer.parseInt(line));
                        i++;
                    }
                    if (i == 1) {
                        p.setActive(Boolean.valueOf(line));
                        i++;
                    }
                    if (i == 0) {
                        p = new Piece(line);
                        i++;
                    }
                }
                if (isSquare) {
                    if (j == 3) {
                        s.setNumber(Long.parseLong(line));
                        p.addSquare(s);
                        ls.add(s);
                        x = -1;
                        y = -1;
                        isSquare = false;
                        j++;
                    }
                    if (j == 2) {
                        if (x != -1 && y != -1) {
                            s = new Square(x, y);
                        }
                        s.setColor(line);
                        j++;
                    }
                    if (j == 1) {
                        y = Integer.parseInt(line);
                        j++;
                    }
                    if (j == 0) {
                        x = Integer.parseInt(line);
                        j++;
                    }
                }
                if (line.equals("piece")) {
                    isPiece = true;
                    isSquare = false;
                    i = 0;
                }
                if (line.equals("square")) {
                    isSquare = true;
                    isPiece = false;
                    j = 0;
                }
            }
            result[0] = lp;
            result[1] = ls;
        } catch (IOException ex) {
            Logger.getLogger(BoardSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public static class Square {

        private int x;
        private int y;
        private String color;
        private long number;

        public Square(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public long getNumber() {
            return number;
        }

        public void setNumber(long number) {
            this.number = number;
        }

        public String getColor() {
            return this.color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getXByY(int y) {
            if (this.y == y) {
                return x;
            }
            return -1;
        }

        public int getYByX(int x) {
            if (this.x == x) {
                return y;
            }
            return -1;
        }

        public Square getByNumber(int n) {
            if (this.number == n) {
                return this;
            }
            return null;
        }
    }
}
