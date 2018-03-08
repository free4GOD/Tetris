/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import tetris.model.BoardSingleton;
import tetris.model.Piece;
import tetris.model.Piece.Square;

/**
 *
 * @author mauricio
 */
public final class PieceInBoard {

    public Piece activePiece;
    public Piece nextPiece;
    public long activePieceNumber;
    public long nextPieceNumber;
    public final List<Square> allSquares;
    public final List<Piece> allPieces;
    private int maxPieceSelected;
    final BoardSingleton board;
    private static final String pieceFile = "pieceFile.txt";

    public PieceInBoard() {
        this.maxPieceSelected = 0; //For debugging purpose
        this.allPieces = new ArrayList<>();
        this.allSquares = new ArrayList<>();
        this.board = BoardSingleton.getInstance();
    }

    public void setActivePiece(Piece piece) {
        this.activePiece = piece;
    }

    public Piece getActivePiece() {
        if (this.activePiece != null && this.activePiece.getActive()) {
            return this.activePiece;
        } else {
            return null;
        }
    }

    public int getActivePieceMinX() {
        return this.activePiece.getMinX();
    }

    public int getActivePieceMaxX() {
        return this.activePiece.getMaxX();
    }

    public int getActivePieceMaxY() {
        return this.activePiece.getMaxY();
    }

    public int getActivePieceMinY() {
        return this.activePiece.getMinY();
    }

    public void setNextPiece(Piece nextPiece) {
        this.nextPiece = nextPiece;
    }

    public Piece getNextPiece() {
        return this.nextPiece;
    }

    public Square getSquareByNumber(long number) {
        Iterator iter = this.allSquares.iterator();
        while (iter.hasNext()) {
            Square s = (Square) iter.next();
            if (s.getNumber() == number) {
                return s;
            }
        }
        return null;
    }

    public Piece getPieceByNumber(long number) {
        Iterator iter = this.allPieces.iterator();
        while (iter.hasNext()) {
            Piece p = (Piece) iter.next();
            if (number == p.getNumber()) {
                return p;
            }
        }
        return null;
    }

    public List<Square> getAllSquares() {
        return this.allSquares;
    }

    public void setAllSquares(List<Square> squares) {
        this.allSquares.addAll(squares);
    }

    public void removeSquares(List<Square> squares) {
        this.allSquares.removeAll(squares);
    }

    public List<Piece> getAllPieces() {
        return this.allPieces;
    }

    public void setAllPieces(List<Piece> pieces) {
        this.allPieces.addAll(pieces);
    }

    public void removePieces(List<Piece> pieces) {
        this.allPieces.removeAll(pieces);
    }

    public void removePiece(Piece piece) {
        this.allPieces.remove(piece);
    }

    public String getPieceFile() {
        return pieceFile;
    }

    public Piece getPieceBySquareNumber(long number) {
        Iterator i = this.allPieces.iterator();
        while (i.hasNext()) {
            Piece p = (Piece) i.next();
            Iterator j = this.allSquares.iterator();
            while (j.hasNext()) {
                Square s = (Square) j.next();
                if (s.getNumber() == number) {
                    return p;
                }
            }
        }
        return null;
    }

    public List<Square> setPositions(List<Square> squares, int value) {
        Iterator is = squares.iterator();
        boolean result = true;
        while (is.hasNext()) {
            Square s = (Square) is.next();
            board.setPosition(s.getX(), s.getY(), value);
        }
        return squares;
    }

    public void removePositions(List<Square> squares) {
        Iterator iterator = squares.iterator();
        while (iterator.hasNext()) {
            Square s = (tetris.model.Piece.Square) iterator.next();
            board.setPosition(s.getX(), s.getY(), 0);
        }
    }

    public final Piece createPiece() {
        Random random = new Random();
        int index = random.nextInt(7);
        /*int[] stringPieceSelected = {8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8}; //For debugging purpose only
        if (this.maxPieceSelected < stringPieceSelected.length) {
            index = stringPieceSelected[this.maxPieceSelected];
            this.maxPieceSelected++;
        }*/
        String name = choosePieceName(index);
        Piece piece = new Piece(name);
        piece.create();
        BoardSingleton.setPiece(1 + BoardSingleton.getPiece());
        piece.setNumber(BoardSingleton.getPiece());
        this.allPieces.add(piece);
        this.allSquares.addAll(piece.getSquares());
        return piece;
    }

    public String choosePieceName(int index) {
        String[] pieceNames = {"Long", "L", "Inverse L", "Cube", "S", "Inverse S", "T", "Big", "Very Big"};

        return pieceNames[index];
    }

    public boolean canMoveDown(List<Square> ls) {
        int i = 0;
        int[][] belongToPiece = new int[ls.size()][2];
        Iterator iter = ls.iterator();
        while (iter.hasNext()) {
            Square s = (Square) iter.next();
            belongToPiece[i][0] = s.getX();
            belongToPiece[i][1] = (s.getY());
            i++;
        }
        iter = ls.iterator();
        i = 0;
        while (iter.hasNext()) {
            Square s = (Square) iter.next();
            boolean shouldCheck = true;
            for (int j = 0; j < belongToPiece.length; j++) {
                if (s.getY() + 1 == belongToPiece[j][1] && s.getX() == belongToPiece[j][0]) {
                    shouldCheck = false;
                }
            }
            if ((s.getY() + 1) >= BoardSingleton.getHeight()) {
                return false;
            }
            if (shouldCheck) {
                if (BoardSingleton.getValue(s.getX(), (s.getY() + 1)) == 1) {
                    return false;
                }

            }
        }
        return true;
    }

    public boolean canMoveRight(Piece piece) {
        List<Square> squares = piece.getSquares();
        Iterator iter = squares.iterator();
        int i = 0;
        int[] belongToPiece = new int[squares.size()];
        while (iter.hasNext()) {
            Square s = (Square) iter.next();
            belongToPiece[i] = s.getX();
            i++;
        }
        iter = squares.iterator();
        i = 0;
        while (iter.hasNext()) {
            Square s = (Square) iter.next();
            boolean shouldCheck = true;
            for (int j = 0; j < squares.size(); j++) {
                if (s.getX() + 1 == belongToPiece[j]) {
                    shouldCheck = false;
                    break;
                }
            }
            if (shouldCheck) {
                if (s.getX() + 1 >= BoardSingleton.getWidth()) {
                    return false;
                }
                if (board.getValue(s.getX() + 1, s.getY()) == 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean canMoveLeft(tetris.model.Piece piece) {
        List<Square> squares = piece.getSquares();
        Iterator iter = squares.iterator();
        int i = 0;
        int[] belongToPiece = new int[squares.size()];
        while (iter.hasNext()) {
            Square s = (Square) iter.next();
            belongToPiece[i] = s.getX();
            i++;
        }
        iter = squares.iterator();
        i = 0;
        while (iter.hasNext()) {
            Square s = (Square) iter.next();
            boolean shouldCheck = true;
            for (int j = 0; j < squares.size(); j++) {
                if (s.getX() - 1 == belongToPiece[j]) {
                    shouldCheck = false;
                    break;
                }
            }
            if (shouldCheck) {
                if (s.getX() - 1 < 0) {
                    return false;
                }
                if (board.getValue(s.getX() - 1, s.getY()) == 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public Piece searchActivePiece() {
        Iterator<Piece> iterator = allPieces.iterator();
        while (iterator.hasNext()) {
            tetris.model.Piece p = iterator.next();
            if (p.getActive()) {
                return p;
            }
        }
        return null;
    }

    public boolean canRotate(boolean clockwise) {
        boolean canRotate = true;
        List<Square> oldSquares = new ArrayList<>();
        oldSquares.addAll(this.activePiece.getSquares());
        this.activePiece.getSquares().stream().forEach((s) -> {
            board.setPosition(s.getX(), s.getY(), 0);
        });
        this.allPieces.remove(activePiece);
        this.allSquares.removeAll(oldSquares);
        if (clockwise) {
            this.activePiece = this.activePiece.rotateRight(this.activePiece);
        } else {
            this.activePiece = this.activePiece.rotateLeft(this.activePiece);
        }
        Iterator<tetris.model.Piece.Square> iterator = this.activePiece.getSquares().iterator();
        while (iterator.hasNext()) {
            Square s = iterator.next();
            if (s.getX() < 0 || s.getX() >= board.getWidth() || s.getY() < 0 || s.getY() >= board.getHeight()) {
                canRotate = false;
                break;
            }
            if (canRotate && board.getValue(s.getX(), s.getY()) == 1) {
                canRotate = false;
                break;
            }
        }
        if (!canRotate) {
            activePiece.removeSquares(activePiece.getSquares());
            this.allPieces.remove(activePiece);
            this.allSquares.removeAll(activePiece.getSquares());
            activePiece.addSquares(oldSquares);
            this.allPieces.add(activePiece);
            this.allSquares.addAll(oldSquares);
            if (clockwise) {
                activePiece.setRotate(activePiece.getRotate() - 1);
            } else {
                activePiece.setRotate(activePiece.getRotate() + 1);
            }
            this.activePiece.getSquares().stream().forEach((s) -> {
                board.setPosition(s.getX(), s.getY(), 1);
            });
        } else {
            this.allSquares.addAll(activePiece.getSquares());
            this.allPieces.add(activePiece);
            this.activePiece.getSquares().stream().forEach((s) -> {
                board.setPosition(s.getX(), s.getY(), 1);
            });
        }
        return canRotate;
    }

    public void printActivePieceCoords() {
        activePiece.getSquares().stream().forEach((s) -> {
            System.out.println("x = " + s.getX() + " y = " + s.getY());
        });
    }

    private void removeLineSquares(List<Integer> linesCompleted) {
        Iterator<Piece> iteratorPieces = allPieces.iterator();
        Iterator<Integer> iterator = linesCompleted.iterator();
        while (iterator.hasNext()) {
            int y = iterator.next();
            BoardSingleton.clearY(y);
            BoardSingleton.moveDown(y);
            while (iteratorPieces.hasNext()) {
                tetris.model.Piece p = iteratorPieces.next();
                Iterator<Square> iteratorSquare = p.getSquares().iterator();
                while (iteratorSquare.hasNext()) {
                    Square s = iteratorSquare.next();
                    if (s.getY() == y) {
                        p.setActive(false);
                        p.removeSquare(s);
                        if (p.getSquares().isEmpty()) {
                            allPieces.remove(p);
                        }
                    }
                }
            }
        }
    }

    public Piece updateCoordinates(List<Square> ls, int x, int y) {
        Iterator i = ls.iterator();
        while (i.hasNext()) {
            Square s = (Square) i.next();
            BoardSingleton.setPosition(s.getX(), s.getY(), 0);
        }
        i = ls.iterator();
        boolean canUpdate = true;
        while (i.hasNext()) {
            Square s = (Square) i.next();

            if ((s.getY() + y) >= BoardSingleton.getHeight() || (s.getX() + x) < 0 || (s.getX() + x) > BoardSingleton.getWidth() || s.getY() + y < 0) {
                canUpdate = false;
            }
            if (!canUpdate) {
                break;
            }
        }
        if (canUpdate) {
            i = ls.iterator();
            List<Square> newLs = new ArrayList<>();
            while (i.hasNext()) {
                Square s = (Square) i.next();
                s.setY((s.getY() + y));
                s.setX((s.getX() + x));
                newLs.add(s);
            }
            getActivePiece().setSquares(newLs);
            setPositions(newLs, 1);
        }
        return getActivePiece();
    }
}
