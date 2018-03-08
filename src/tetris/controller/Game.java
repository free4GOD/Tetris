/**
 *
 * @author Mauricio Sosa Giri (free4GOD)
 */
package tetris.controller;

import java.util.Iterator;
import java.util.List;
import tetris.model.RecordSingleton;
import tetris.model.BoardSingleton;
import tetris.model.Piece;
import tetris.model.Piece.Square;

public class Game {

    private final RecordSingleton recordSingleton;
    final BoardSingleton board;
    private final PieceInBoard PieceInBoard;
    private boolean pause;
    private boolean gameOver;

    public Game() {
        BoardSingleton.initialize();
        this.board = BoardSingleton.getInstance();
        RecordSingleton.initialize();
        this.recordSingleton = RecordSingleton.getInstance();
        this.PieceInBoard = new PieceInBoard();
        this.pause = false;
        this.gameOver = false;
    }

    public boolean getGameOver() {
        return this.gameOver;
    }

    public void setGameOver(boolean isFinished) {
        this.gameOver = isFinished;
    }

    public int getBoardWidth() {
        return BoardSingleton.getWidth();
    }

    public int getBoardHeight() {
        return BoardSingleton.getHeight();
    }

    public void setPoints(int newLines) {
        int newPoints = 0;
        for (int i = 1; i <= newLines; i++) {
            newPoints += 10;
        }
        newPoints *= newLines;
        long total = RecordSingleton.getPoints() + newPoints;
        if (maxPoints(total)) {
            total = 999999999999L;
        }
        RecordSingleton.setPoints(total);
    }

    public void setPoints(long points) {
        RecordSingleton.setPoints(points);
    }

    public long getPoints() {
        return RecordSingleton.getPoints();
    }

    public void setHighScore() {
        RecordSingleton.setHighScore(RecordSingleton.getPoints());
    }

    public void writeHighScore() {
        RecordSingleton.writeHighScore();
    }

    public long getHighScore() {
        return RecordSingleton.getHighScore();
    }

    public boolean isHighScore() {
        return (RecordSingleton.getPoints() > RecordSingleton.getHighScore() && RecordSingleton.getHighScore() > 0);
    }

    public void setLevel(int level) {
        RecordSingleton.setLevel(level);
    }

    public int getLevel() {
        return RecordSingleton.getLevel();
    }

    public void setLines(int newLines) {
        int total = RecordSingleton.getLines() + newLines;
        RecordSingleton.setLines(total);
    }

    public int getLines() {
        return RecordSingleton.getLines();
    }

    public int getBoardValue(int x, int y) {
        return BoardSingleton.getValue(x, y);
    }

    public PieceInBoard getPieceInBoard() {
        return this.PieceInBoard;
    }

    public long getActivePieceNumber() {
        return BoardSingleton.getActivePiece();
    }

    public long getNextPieceNumber() {
        return BoardSingleton.getNextPiece();
    }

    public void start() {
        PieceInBoard.setActivePiece(PieceInBoard.createPiece());
        PieceInBoard.setNextPiece(PieceInBoard.createPiece());
        PieceInBoard.activePiece.setActive(true);
        BoardSingleton.setActivePiece(PieceInBoard.getActivePiece().getNumber());
        BoardSingleton.setNextPiece(PieceInBoard.getNextPiece().getNumber());
    }

    public Piece play() {
        PieceInBoard.getActivePiece().setActive(false);
        Piece p = PieceInBoard.getNextPiece();
        PieceInBoard.setActivePiece(p);
        BoardSingleton.setActivePiece(p.getNumber());
        PieceInBoard.setNextPiece(PieceInBoard.createPiece());
        BoardSingleton.setNextPiece(PieceInBoard.getNextPiece().getNumber());
        PieceInBoard.setPositions(p.getSquares(), 1);
        p.setActive(true);
        if (!PieceInBoard.canMoveDown(PieceInBoard.getActivePiece().getSquares())) {
            checkGameOver();
        }
        return p;
    }

    public boolean loop() {
        Piece p = PieceInBoard.getActivePiece();
        //boardPrint();
        if (p == null) {
            return false;
        }
        if (PieceInBoard.canMoveDown(p.getSquares())) {
            //board.print();
            return true;
        } else {
            return false;
        }
    }

    public void boardPrint() {
        BoardSingleton.print();
    }

    public void stop() {

    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean getPause() {
        return pause;
    }

    public double getTickFrecuency() {
        return RecordSingleton.getLevel();
    }

    private void checkGameOver() {
        for (int x = 0; x < BoardSingleton.getWidth(); x++) {
            if (BoardSingleton.getValue(x, 0) == 1) {
                setGameOver(true);
            }
        }
    }

    public List<Integer> checkLinesCompleted() {
        return BoardSingleton.getLinesCompleted();
    }

    public List<Integer> removeLinesCompleted(List<Integer> linesCompleted) {
        if (linesCompleted.size() > 0) {
            Iterator<Integer> linesIter = linesCompleted.iterator();
            while (linesIter.hasNext()) {
                Integer line = linesIter.next();
                BoardSingleton.clearY(line);
            }
        }
        return linesCompleted;
    }

    public void moveLineDownInBoard(int line) {
        BoardSingleton.moveDown(line);
    }

    public int addLevel() {
        int total = RecordSingleton.getLevel() + 1;
        RecordSingleton.setLevel(total);
        return total;
    }

    private boolean maxPoints(long points) {
        return points >= 999999999999L;
    }

    public boolean checkLevel() {
        return (RecordSingleton.getPoints() / RecordSingleton.getLevel() >= 300);
    }

    public boolean checkHighScore() {
        return (RecordSingleton.getPoints() >= RecordSingleton.getHighScore() && RecordSingleton.getPoints() > 0);
    }

    public void save() {
        BoardSingleton.setActivePiece(PieceInBoard.getActivePiece().getNumber());
        BoardSingleton.setNextPiece(PieceInBoard.getNextPiece().getNumber());
        RecordSingleton.save();
        BoardSingleton.save();
        Piece.save(PieceInBoard.getPieceFile(), PieceInBoard.getAllPieces());
    }

    public void load() {
        RecordSingleton.load();
        BoardSingleton.reset();
        BoardSingleton.load();
        PieceInBoard.getAllPieces().clear();
        PieceInBoard.getAllSquares().clear();
        List[] result = Piece.load(PieceInBoard.getPieceFile());
        PieceInBoard.setAllPieces(result[0]);
        PieceInBoard.setAllSquares(result[1]);
        PieceInBoard.setNextPiece(PieceInBoard.getPieceByNumber(BoardSingleton.getNextPiece()));
        Piece p = PieceInBoard.getPieceByNumber(BoardSingleton.getActivePiece());
        p.setActive(true);
        PieceInBoard.setActivePiece(p);
    }

    public boolean checkFewLinesLeft() {
        int lines = 0;
        for (int y = 0; y < BoardSingleton.getHeight() - 10; y++) {
            for (int x = 0; x < BoardSingleton.getWidth(); x++) {
                if (BoardSingleton.getValue(x, y) == 1) {
                    lines++;
                    break;
                }
            }
        }
        if (lines == (BoardSingleton.getHeight() - 10)) {
            return true;
        } else {
            return false;
        }
    }
}
