/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris.view;

import java.io.File;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import static javafx.scene.media.MediaPlayer.INDEFINITE;

/**
 *
 * @author mauricio
 */
public class Sound {

    MediaPlayer player;

    public Sound() {

    }

    public void playSound(String soundFile) {
        Platform.runLater(() -> {
            try {
                MediaPlayer player = createPlayer(soundFile);
                player.setCycleCount(1);
                player.setVolume(1.0);
                player.play();
            } catch (Exception e) {
                System.out.println(e);
            }
        });
    }

    public Media createMedia(String soundFile) {
        Media sound = new Media(new File(soundFile).toURI().toString());
        return sound;
    }

    public MediaPlayer createPlayer(String soundFile) {
        Media sound = createMedia(soundFile);
        MediaPlayer player = new MediaPlayer(sound);
        return player;
    }

    public void volume(double volume) {
        player.setVolume(volume);
    }

    public void cycleCount(int cycle) {
        player.setCycleCount(cycle);
    }

    public void cycleCountIndefinite() {
        player.setCycleCount(INDEFINITE);
    }

    public void stop() {
        Platform.runLater(() -> {
            player.stop();
        });
    }

    public void pause() {
        Platform.runLater(() -> {
            player.pause();
        });
    }

    public void play() {
        Platform.runLater(() -> {
            player.play();
        });
    }
}
