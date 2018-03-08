/**
 *
 * @author Mauricio Sosa Giri (free4GOD)
 */
package tetris.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import static javafx.scene.media.MediaPlayer.INDEFINITE;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tetris.Tetris;
import tetris.controller.Game;
import tetris.controller.PieceInBoard;
import tetris.model.Piece;
import tetris.model.Piece.Square;

public class MainWindow {

    private final double width;
    private final double height;
    private final double hboxHeight;
    private final double paneWidth;
    private final double paneHeight;
    private final Color background;
    private Stage primaryStage;
    private final Scene scene;
    private final Timeline timeline;
    private Pane pane;
    Game Game;
    PieceInBoard PieceInBoard;
    Sound Sound;
    private MediaPlayer mediaPlayer;
    Popup popup = new Popup();
    boolean pause;
    boolean down;
    boolean music;
    boolean showedHighScore;
    EventHandler<KeyEvent> eventPiece;
    EventHandler<KeyEvent> eventGame;
    double translateX;
    double translateY;
    private Group activeGroup;
    private final List<Rectangle> allRectangles;
    private final MediaPlayer player;

    public MainWindow(Stage primaryStage) {
        this.Game = new Game();
        this.PieceInBoard = Game.getPieceInBoard();
        this.Sound = new Sound();
        Game.start();
        this.hboxHeight = 50.0;
        this.width = Game.getBoardWidth() * 25 + 150;
        this.height = Game.getBoardHeight() * 25 + hboxHeight;
        this.paneWidth = Game.getBoardWidth() * 25.0;
        this.paneHeight = Game.getBoardHeight() * 25.0;
        this.background = Color.BLACK;
        this.primaryStage = primaryStage;
        HBox hbox = createHBoxPane();
        VBox vbox = createVBoxPane();
        pane = createPane();
        BorderPane border = createborderPane(hbox, vbox, pane);
        this.scene = new Scene(border, width, height, background);
        scene.getStylesheets().add("tetris.css");
        primaryStage.setTitle("Tetris");
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.show();
        addKeyboardEvents();
        translateX = 0;
        translateY = 25;
        this.allRectangles = new ArrayList<>();
        printRectangleInVBox();
        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setRate(Game.getTickFrecuency());
        addPieceEvents();
        this.showedHighScore = false;
        activeGroup = printSquares(PieceInBoard.getActivePiece().getSquares());
        player = Sound.createPlayer("sounds/music.wav");
        run();
    }

    private void setTranslateX(double x) {
        this.translateX = x;
    }

    private void setTranslateY(double y) {
        this.translateY = y;
    }

    private void setPoints(long points) {
        Text text = (Text) scene.lookup("#pointsNumbers");
        text.setText("" + points);
    }

    private void setLevel(int level) {
        Text text = (Text) scene.lookup("#levelNumbers");
        text.setText("" + level);
    }

    private void setHighScore(long highScore) {
        Text text = (Text) scene.lookup("#highScore");
        text.setText("" + highScore);
    }

    private void setLines(int lines) {
        Text text = (Text) scene.lookup("#lineNumbers");
        text.setText("" + lines);
    }

    public void run() {
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (ActionEvent evt) -> {
            if (Game.loop()) {
                timeline.pause();
                movePiece(activeGroup);
                timeline.play();
            } else {
                timeline.pause();
                List<Integer> linesCompleted = Game.checkLinesCompleted();
                if (linesCompleted.size() > 0) {
                    showMessageInHBox(linesCompleted.size() + " lines completed!");
                    removeLines(linesCompleted);
                    Game.setLines(linesCompleted.size());
                    setLines(Game.getLines());
                    Game.setPoints(linesCompleted.size());
                    setPoints(Game.getPoints());
                    if (Game.checkLevel()) {
                        setLevel(Game.addLevel());
                        showMessageInHBox("Reached level " + Game.getLevel() + "!");
                    }
                    if (Game.checkHighScore()) {
                        Game.writeHighScore();
                        Game.setHighScore();
                        setHighScore(Game.getHighScore());
                        showMessageInHBox("New highscore of " + Game.getHighScore());
                        if (!this.showedHighScore) {
                            showNewHighScore();
                            this.showedHighScore = true;
                        }
                    } else {
                        Sound.playSound("sounds/emptyLine.wav");
                    }
                }
                Game.play();
                printRectangleInVBox();
                activeGroup = printSquares(PieceInBoard.getActivePiece().getSquares());
                timeline.setRate(Game.getTickFrecuency());
                timeline.play();
            }
            if (Game.getGameOver()) {
                timeline.stop();
                gameOver();
            }
        }
        ));
        timeline.play();
    }

    public void quit() {
        Game.writeHighScore();
        primaryStage.hide();
        primaryStage.close();
    }

    void newGame() {
        quit();
        Tetris t = new tetris.Tetris();
        primaryStage = new Stage();
        t.start((Stage) primaryStage);
    }

    private HBox createHBoxPane() {
        HBox hbox = new HBox();
        hbox.setMinSize(width, hboxHeight);
        hbox.prefHeight(hboxHeight);
        hbox.maxHeight(hboxHeight);
        hbox.setStyle("-fx-border-width : 0 0 5 0; -fx-border-color: white;");
        hbox.setAlignment(Pos.CENTER);
        hbox.setId("hbox");
        Text addTextToPane = addTextToPane("The TETRIS game! (F1 for help)", hbox);
        addTextToPane.setId("theTetrisGame");
        return hbox;
    }

    private VBox createVBoxPane() {
        VBox vbox = new VBox();
        vbox.setMaxSize(width - paneWidth, paneHeight);
        vbox.setMinSize(width - paneWidth, paneHeight);
        vbox.setStyle("-fx-border-width : 0 0 0 25; -fx-border-color: white;");
        vbox.setPadding(new Insets(10, 0, 30, 0));
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.TOP_CENTER);
        VBox vboxTop = new VBox();
        vboxTop.setId("vboxTop");
        VBox vboxMiddle = new VBox();
        VBox vboxBottom = new VBox();
        vboxBottom.setId("vboxBottom");
        setBoxProperties(vboxTop);
        setBoxProperties(vboxMiddle);
        setBoxProperties(vboxBottom);
        addTextToPane("Next", vboxTop);
        addTextToPane("Points", vboxMiddle);
        addTextToPane("0", vboxMiddle).setId("pointsNumbers");
        addTextToPane("Lines", vboxMiddle);
        addTextToPane("0", vboxMiddle).setId("lineNumbers");
        addTextToPane("Level", vboxMiddle);
        addTextToPane("1", vboxMiddle).setId("levelNumbers");
        addTextToPane("Highscore", vboxBottom);
        Text highScore = new Text(String.valueOf(Game.getHighScore()));
        highScore.setId("highScore");
        highScore.setFont(Font.font("Hack", 20));
        highScore.setFill(Color.WHITE);
        vboxBottom.getChildren().add(highScore);
        vbox.getChildren().addAll(vboxTop, vboxMiddle, vboxBottom);
        return vbox;
    }

    private Pane createPane() {
        pane = new Pane();
        pane.setMaxSize(paneWidth, paneHeight);
        pane.setMinSize(paneWidth, paneHeight);
        pane.setLayoutY(hboxHeight);
        return pane;
    }

    private BorderPane createborderPane(HBox hbox, VBox vbox, Pane pane) {
        BorderPane border;
        border = new BorderPane();
        border.setTop(hbox);
        border.setRight(vbox);
        border.setCenter(pane);
        border.setMaxSize(width, height);
        border.setMinSize(width, height);
        return border;
    }

    public Text addTextToPane(String text, Pane thePane) {
        Text title = new Text(text);
        title.setFont(Font.font("Hack", 20));
        title.setFill(Color.WHITE);
        thePane.getChildren().add(title);
        return title;

    }

    public void setBoxProperties(VBox vbox) {
        vbox.setMaxSize(width - paneWidth, paneHeight / 3 - 1);
        vbox.setMinSize(width - paneWidth, paneHeight / 3 - 1);
        vbox.setPadding(new Insets(10, 0, 30, 0));
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.TOP_CENTER);
    }

    private void printRectangleInVBox() {
        VBox vboxTop = (VBox) scene.lookup("#vboxTop");
        Group g = new Group();
        List<Square> ss = PieceInBoard.getNextPiece().getSquares();
        Iterator<Square> iterator = ss.iterator();
        while (iterator.hasNext()) {
            Square s = iterator.next();
            Rectangle r = new Rectangle(s.getX() * 25, s.getY() * 25, 25, 25);
            Color c = Color.web(s.getColor());
            r.setFill(c);
            r.setId("" + s.getNumber());
            this.allRectangles.add(r);
            g.getChildren().add(r);
        }
        if (vboxTop.getChildren().size() > 1) {
            vboxTop.getChildren().remove(1);
        }
        vboxTop.getChildren().add(g);
    }

    private Group printSquares(List<Square> ls) {
        timeline.pause();
        Group g = new Group();
        Platform.runLater(() -> {
            Iterator<Square> iterator = ls.iterator();
            while (iterator.hasNext()) {
                Square s = iterator.next();
                Rectangle r = new Rectangle(s.getX() * 25, s.getY() * 25, 25, 25);
                Color c = Color.web(s.getColor());
                r.setFill(c);
                r.setId("" + s.getNumber());
                this.allRectangles.add(r);
                g.getChildren().add(r);
            }
            g.setId("activeGroup");
            pane.getChildren().add(g);
        });
        timeline.play();
        return g;
    }

    private void printAllPieces(List<Piece> lp) {
        Iterator<Piece> iterator = lp.iterator();
        while (iterator.hasNext()) {
            Piece p = iterator.next();
            if (p.getNumber() != Game.getActivePieceNumber() && p.getNumber() != Game.getNextPieceNumber()) {
                Group g = new Group();
                g.setId("" + p.getNumber());
                Iterator<Square> is = p.getSquares().iterator();
                while (is.hasNext()) {
                    Square s = is.next();
                    Rectangle r = new Rectangle(s.getX() * 25, s.getY() * 25, 25, 25);
                    Color c = Color.web(s.getColor());
                    r.setFill(c);
                    r.setId("" + s.getNumber());
                    r.setVisible(true);
                    this.allRectangles.add(r);
                    g.getChildren().add(r);
                }
                pane.getChildren().add(g);
            }
        }
    }

    private void movePiece(Group g) {
        Iterator iterR = g.getChildren().iterator();
        if (iterR.hasNext()) {
            Rectangle r = (Rectangle) iterR.next();
            Bounds rBounds = r.localToScene(r.getBoundsInLocal());
            if (!(rBounds.getMinX() < 0 || rBounds.getMinX() + translateX >= paneWidth || rBounds.getMinY() < 0 || !(rBounds.getMinY() <= paneHeight))) {
                if (PieceInBoard.getActivePiece().getActive()) {
                    PieceInBoard.updateCoordinates(PieceInBoard.getActivePiece().getSquares(), (int) translateX / 25, (int) (translateY / 25));
                    g.getTransforms().add(new Translate(translateX, translateY));
                }
            }
        }
    }

    public void rotateGroup() {
        Group g = (Group) pane.getChildren().get((pane.getChildren().size()) - 1);
        List<Rectangle> lr = new ArrayList<>();
        Iterator gi = g.getChildren().iterator();
        while (gi.hasNext()) {
            Rectangle r = (Rectangle) gi.next();
            lr.add(r);
        }
        this.allRectangles.removeAll(lr);
        g.setVisible(false);
        g.getChildren().clear();
        pane.getChildren().remove(g);
        this.activeGroup = new Group();
        Piece piece = PieceInBoard.getActivePiece();
        Iterator<Square> iterator = PieceInBoard.getActivePiece().getSquares().iterator();
        while (iterator.hasNext()) {
            Square s = iterator.next();
            Rectangle r = new Rectangle(s.getX() * 25, s.getY() * 25, 25, 25);
            r.setId("" + s.getNumber());
            Color c = Color.web(s.getColor());
            r.setFill(c);
            this.allRectangles.add(r);
            this.activeGroup.getChildren().add(r);
        }
        this.activeGroup.setId("activeGroup");
        pane.getChildren().add(this.activeGroup);
        timeline.play();
    }

    private void rotateLeft() {
        for (int i = 0; i < PieceInBoard.getActivePiece().getSquares().size(); i++) {
            pane.getChildren().get(pane.getChildren().size() - 1 - i).getTransforms().add(new Rotate(-90));
        }
    }

    private void rotateRight() {
        for (int i = 0; i < PieceInBoard.getActivePiece().getSquares().size(); i++) {
            pane.getChildren().get(pane.getChildren().size() - 1 - i).getTransforms().add(new Rotate(90));
        }
    }

    private void removeLines(List<Integer> linesCompleted) {
        //removeAllSquareNumbersAndRectangleNumbersAlone();
        List<Rectangle> lr = new ArrayList<>();
        List<Square> ls = new ArrayList<>();
        List<Piece> lp = new ArrayList<>();
        Iterator iterLine = linesCompleted.iterator();
        while (iterLine.hasNext()) {
            int line = (int) iterLine.next();
            Iterator iterPiece = PieceInBoard.getAllPieces().iterator();
            while (iterPiece.hasNext()) {
                Piece p = (Piece) iterPiece.next();
                Iterator iterSquare = p.getSquares().iterator();
                while (iterSquare.hasNext()) {
                    Square s = (Square) iterSquare.next();
                    if (s.getY() == line) {
                        Rectangle r = (Rectangle) scene.lookup("#" + s.getNumber());
                        if (r != null) {
                            r.setVisible(false);
                            lr.add(r);
                            ls.add(s);
                        } else {
                            ls.add(s);
                        }
                    }
                }
            }
            iterPiece = PieceInBoard.getAllPieces().iterator();
            while (iterPiece.hasNext()) {
                Piece p = (Piece) iterPiece.next();
                p.removeSquares(ls);
                if (p.getSquares().isEmpty()) {
                    lp.add(p);
                }
            }
            PieceInBoard.removePieces(lp);
            PieceInBoard.removeSquares(ls);
            this.allRectangles.removeAll(lr);
            Iterator iterSquare = PieceInBoard.getAllSquares().iterator();
            while (iterSquare.hasNext()) {
                Square s = (Square) iterSquare.next();
                if (s.getY() <= line) {
                    Rectangle r = (Rectangle) scene.lookup("#" + s.getNumber());
                    if (r != null) {
                        s.setY(s.getY() + 1);
                        r.setTranslateY(25 + r.getTranslateY());
                    }
                }
            }
            Game.moveLineDownInBoard(line);
        }
    }

    public void showNewHighScore() {
        Text text = new Text();
        text.setText("NEW HIGHSCORE!");
        text.setFont(Font.font(15));
        text.getStyleClass().add("gameover");
        StackPane stack = new StackPane();
        stack.getChildren().add(text);
        VBox vboxBottom = (VBox) scene.lookup("#vboxBottom");
        stack.setLayoutY((vboxBottom.getHeight() / 3) - text.getBoundsInParent().getMaxY());
        stack.setMinWidth(vboxBottom.getWidth());
        vboxBottom.getChildren().add(stack);
        Platform.runLater(() -> {
            Sound.playSound("sounds/highScore.wav");
        });
    }

    public void gameOver() {
        Sound.playSound("sounds/gameOver.wav");
        gameOverText();
        gameOverAnimation();
        if (Game.checkHighScore()) {
            showHighScoreInGameOver();
            Game.setHighScore();
            Game.writeHighScore();
        }
    }

    private void showHighScoreInGameOver() {
        Text text = new Text();
        text.setText("NEW HIGHSCORE!");
        text.setFont(Font.font(50));
        text.getStyleClass().add("gameover");
        StackPane stack1 = new StackPane();
        stack1.getChildren().add(text);
        stack1.setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));
        stack1.setLayoutY((pane.getHeight() / 3) - text.getBoundsInParent().getMaxY());
        stack1.setMinWidth(pane.getWidth());
        pane.getChildren().add(stack1);
        Text pointsText = new Text();
        pointsText.setText("" + Game.getPoints());
        pointsText.setFont(Font.font(50));
        pointsText.getStyleClass().add("gameover");
        StackPane stack2 = new StackPane();
        stack2.getChildren().add(pointsText);
        stack2.setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));
        stack2.setLayoutY((pane.getHeight() / 3) - text.getBoundsInParent().getMaxY() + 100.0);
        stack2.setMinWidth(pane.getWidth());
        pane.getChildren().add(stack2);
    }

    private void gameOverText() {
        Text text = new Text();
        text.setText("GAME OVER");
        text.setFont(Font.font(60));
        text.getStyleClass().add("gameover");
        StackPane stack = new StackPane();
        stack.getChildren().add(text);
        pane.getChildren().add(stack);
        stack.setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));
        stack.setLayoutY((paneHeight / 5) - text.getBoundsInParent().getMaxY());
        stack.setMinWidth(paneWidth);
    }

    private void gameOverAnimation() {
        Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(1000));
                setInterpolator(Interpolator.EASE_OUT);
            }

            @Override
            protected void interpolate(double frac) {
                Color vColor = new Color(1, 0, 0, 1 - frac);
                pane.setBackground(new Background(new BackgroundFill(vColor, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        };
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }

    private void addKeyboardEvents() {
        this.down = false;
        this.music = false;
        eventGame = (KeyEvent key) -> {
            switch (key.getCode()) {
                case Q:
                    quit();
                    break;
                case M:
                    player.setCycleCount(INDEFINITE);
                    player.setVolume(0.3);
                    if (music) {
                        music = false;
                        Platform.runLater(() -> {
                            player.pause();
                        });
                    } else {
                        music = true;
                        Platform.runLater(() -> {
                            player.play();
                        });
                    }
                    break;
                case N:
                    newGame();
                    break;
                case B:
                    Game.boardPrint();
                    printAllSquares();
                    break;
                case S:
                    timeline.pause();
                    Platform.runLater(() -> {
                        Game.save();
                    });
                    timeline.play();
                    showMessageInHBox("Saved game!");
                    break;
                case L:
                    timeline.pause();
                    Platform.runLater(() -> {
                        Game.load();
                        load();
                    });
                    timeline.play();
                    break;
            }
        };
        scene.addEventHandler(KeyEvent.KEY_PRESSED, eventGame);
    }

    private void addPieceEvents() {
        eventPiece = (KeyEvent key) -> {
            switch (key.getCode()) {
                case DOWN:
                    Piece piece = PieceInBoard.getActivePiece();
                    if (piece.getActive()) {
                        if (PieceInBoard.canMoveDown(piece.getSquares())) {
                            if (timeline.getRate() == Game.getTickFrecuency()) {
                                timeline.pause();
                                setTranslateX(0);
                                setTranslateY(25);
                                movePiece(activeGroup);
                                timeline.play();
                            }
                        }
                    }
                    break;
                case RIGHT:
                    piece = PieceInBoard.getActivePiece();
                    if (piece.getActive()) {
                        if (PieceInBoard.canMoveRight(piece)) {
                            timeline.pause();
                            setTranslateX(25);
                            setTranslateY(0);
                            movePiece(activeGroup);
                            setTranslateX(0);
                            setTranslateY(25);
                            timeline.play();
                        }
                    }
                    break;
                case LEFT:
                    piece = PieceInBoard.getActivePiece();
                    if (piece.getActive()) {
                        if (PieceInBoard.canMoveLeft(piece)) {
                            timeline.pause();
                            setTranslateX(-25);
                            setTranslateY(0);
                            movePiece(activeGroup);
                            setTranslateX(0);
                            setTranslateY(25);
                            timeline.play();
                        }
                    }
                    break;
                case UP:
                    timeline.pause();
                    if (PieceInBoard.getActivePiece().getActive()) {
                        if (PieceInBoard.canRotate(true)) {
                            rotateGroup();
                        } else {
                            showMessageInHBox("Cant rotate piece bounds overlap with something");
                            Sound.playSound("sounds/cantrotate.wav");
                        }
                    }
                    timeline.play();
                    break;
                case Z:
                    if (PieceInBoard.getActivePiece().getActive()) {
                        timeline.pause();
                        if (PieceInBoard.canRotate(false)) {
                            rotateGroup();
                            timeline.play();
                        } else {
                            showMessageInHBox("Cant rotate piece bounds overlap with something");
                            Sound.playSound("sounds/cantrotate.wav");
                        }
                    }
                    break;
                case SPACE:
                    piece = PieceInBoard.getActivePiece();
                    if (piece.getActive()) {
                        if (PieceInBoard.canMoveDown(piece.getSquares())) {
                            timeline.pause();
                            timeline.setRate((Game.getTickFrecuency() + 8));
                            timeline.play();
                        }
                    }
                    break;
                case P:
                    if (Game.getPause()) {
                        Game.setPause(false);
                        showPauseText(false);
                    } else {
                        Game.setPause(true);
                        timeline.pause();
                        showPauseText(true);
                    }
                    break;
                case F1:
                    if (!Game.getPause()) {
                        Game.setPause(true);
                        timeline.pause();
                        showPauseText(true);
                    }
                    showHelp();
                    break;
                case ESCAPE:
                    if (Game.getPause()) {
                        popup.hide();
                        showPauseText(false);
                        if (!Game.getGameOver()) {
                            Game.setPause(false);
                        }
                    }
                    break;
                case C:
                    PieceInBoard.printActivePieceCoords();
                    break;

            }
        };
        scene.addEventHandler(KeyEvent.KEY_PRESSED, eventPiece);
    }

    public void showHelp() {
        Text text0 = setTextForHelpPopup("ESC: Close this popup");
        Text text1 = setTextForHelpPopup("F1: Show this");
        Text text2 = setTextForHelpPopup("LEFT: Move left");
        Text text3 = setTextForHelpPopup("RIGHT: Move right");
        Text text4 = setTextForHelpPopup("DOWN: Go down one more position");
        Text text5 = setTextForHelpPopup("UP: Rotate right");
        Text text6 = setTextForHelpPopup("Z: Rotate left");
        Text text7 = setTextForHelpPopup("P: Pause/Resume");
        Text text8 = setTextForHelpPopup("S: Save game");
        Text text9 = setTextForHelpPopup("L: Load game");
        Text text10 = setTextForHelpPopup("SPACE: Make piece go down faster");
        Text text11 = setTextForHelpPopup("M: Play/Stop music");
        Text text12 = setTextForHelpPopup("Q: Quit");
        Text text13 = setTextForHelpPopup("N: New game");
        VBox vboxHelp = new VBox();
        vboxHelp.setId("vboxHelp");
        vboxHelp.setPrefSize(scene.getWidth() - 200, height - 400);
        vboxHelp.setAlignment(Pos.CENTER);
        vboxHelp.setBackground(new Background(new BackgroundFill(Color.web("#000000"), CornerRadii.EMPTY, Insets.EMPTY)));
        vboxHelp.getChildren().addAll(text0, text1, text2, text3, text4, text5, text6, text7, text8, text9, text10, text11, text12, text13);
        popup.getContent().addAll(vboxHelp);
        popup.show(scene.getWindow());
        popup.setHideOnEscape(false);
    }

    public Text setTextForHelpPopup(String string) {
        Text text = new Text();
        text.setText(string);
        text.setFont(Font.font(20));
        text.getStyleClass().add("help");
        return text;
    }

    public void showPauseText(boolean show) {
        StackPane stackPaused = new StackPane();
        Text pausedText = new Text();
        pausedText.setText("PAUSED");
        pausedText.setFont(Font.font(100));
        pausedText.getStyleClass().add("paused");
        stackPaused.setId("stackPaused");
        stackPaused.setPrefSize(paneWidth, paneHeight);
        stackPaused.getChildren().add(pausedText);
        StackPane.setAlignment(pausedText, Pos.CENTER);
        if (show) {
            pane.getChildren().add(stackPaused);
        } else {
            stackPaused = (StackPane) scene.lookup("#stackPaused");
            pane.getChildren().remove(stackPaused);
            this.timeline.play();
        }
    }

    private void showMessageInHBox(String text) {
        Text tetrisGame = (Text) scene.lookup("#theTetrisGame");
        Timeline timelineShowMessage = new Timeline();
        timelineShowMessage.getKeyFrames().add(new KeyFrame(Duration.seconds(3), (ActionEvent evt) -> {
            tetrisGame.setText("The TETRIS game! (Press F1 for help)");
        }));
        timelineShowMessage.setCycleCount(1);
        timelineShowMessage.setDelay(Duration.seconds(3));
        timelineShowMessage.play();
        tetrisGame.setText(text);
    }

    private void removeAllSquareNumbersAndRectangleNumbersAlone() {
        List<Rectangle> lr = new ArrayList<>();
        List<Square> ls = new ArrayList<>();
        List<Piece> lp = new ArrayList<>();
        Iterator iR = this.allRectangles.iterator();
        while (iR.hasNext()) {
            boolean alone = true;
            Rectangle r = (Rectangle) iR.next();
            Iterator iS = this.PieceInBoard.getAllSquares().iterator();
            while (iS.hasNext()) {
                Square s = (Square) iS.next();
                if (r.getId().equals("" + s.getNumber())) {
                    alone = false;
                }
                if (!alone) {
                    break;
                }
            }
            if (alone) {
                lr.add(r);
                r.setVisible(false);
            }
        }
        Iterator iS = this.PieceInBoard.getAllSquares().iterator();
        while (iS.hasNext()) {
            boolean alone = true;
            Square s = (Square) iS.next();
            iR = this.allRectangles.iterator();
            while (iR.hasNext()) {
                Rectangle r = (Rectangle) iR.next();
                if (r.getId().equals("" + s.getNumber())) {
                    alone = false;
                }
                if (!alone) {
                    break;
                }
            }
            if (alone) {
                ls.add(s);
                Piece p = PieceInBoard.getPieceBySquareNumber((int) s.getNumber());
                lp.add(p);
            }
        }
        PieceInBoard.getAllSquares().removeAll(ls);
        PieceInBoard.getAllPieces().removeAll(lp);
        this.allRectangles.removeAll(lr);

    }

    private void printAllSquares() {
        System.out.println("Print matrix of squares");
        List<String> lp = new ArrayList<>();
        for (Square s : PieceInBoard.getAllSquares()) {
            String x = "";
            String y = "";
            if (s.getX() <= 9) {
                x = "0" + s.getX();
            } else {
                x = "" + s.getX();
            }
            if (s.getY() <= 9) {
                y = "0" + s.getY();
            } else {
                y = "" + s.getY();
            }
            int z = Game.getBoardValue(s.getX(), s.getY());
            String str = "" + x + "_" + y + "=" + z;
            lp.add(str);
        }
        lp = lp.stream().sorted().collect(Collectors.toList());
        String[][] strArr = new String[Game.getBoardWidth()][Game.getBoardHeight()];
        for (int j = 0; j < Game.getBoardHeight(); j++) {
            for (int i = 0; i < Game.getBoardWidth(); i++) {
                strArr[i][j] = "0";
                Iterator itr = lp.iterator();
                while (itr.hasNext()) {
                    String str = (String) itr.next();
                    if (i == Integer.parseInt(str.split("_")[0]) && j == Integer.parseInt(str.split("_")[1].split("=")[0])) {
                        strArr[i][j] = str.split("_")[1].split("=")[1];
                    }
                }
            }
        }
        for (int y = 0; y < Game.getBoardHeight(); y++) {
            for (int x = 0; x < Game.getBoardWidth(); x++) {
                System.out.print(strArr[x][y] + "  ");
            }
            System.out.println(" = " + y);
        }
    }

    private void load() {
        timeline.stop();
        pane.getChildren().remove(0, pane.getChildren().size());
        this.allRectangles.clear();
        setPoints(Game.getPoints());
        setLevel(Game.getLevel());
        setHighScore(Game.getHighScore());
        setLines(Game.getLines());
        printRectangleInVBox();
        printAllPieces(PieceInBoard.getAllPieces());
        Piece p = PieceInBoard.getPieceByNumber(Game.getActivePieceNumber());
        if (p != null) {
            p.setActive(true);
            activeGroup = printSquares(PieceInBoard.getPieceByNumber(Game.getActivePieceNumber()).getSquares());
        }
        timeline.play();
        showMessageInHBox("Loaded game!");
    }
}
