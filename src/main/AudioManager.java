package main;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.net.URL;
import java.nio.file.Paths;

public class AudioManager {
    private static AudioManager ourInstance = new AudioManager();
    private Media sound;
    private MediaPlayer soundPlayer;
    private String previousTrack;

    public static AudioManager getInstance() {
        return ourInstance;
    }

    private AudioManager() { }

    public void loopBackgroundMusic(String file) {
        if(sound != null) {
            previousTrack = sound.getSource();
        }

        if(file.startsWith("file:")) {
            file = file.substring(5);
            System.out.println(file);
        }
        URL resource = getClass().getResource(file);
        sound = new Media(Paths.get(file).toUri().toString());
        soundPlayer = new MediaPlayer(sound);
        soundPlayer.setVolume(GameStage.musicVolume);
        soundPlayer.setOnEndOfMedia(() -> soundPlayer.seek(Duration.ZERO));
        soundPlayer.play();
    }

    public void returnToOldTrack() {
        URL resource = getClass().getResource(previousTrack);
        sound = new Media(resource.toString());
        soundPlayer = new MediaPlayer(sound);
        soundPlayer.setVolume(GameStage.musicVolume);
        soundPlayer.setOnEndOfMedia(() -> soundPlayer.seek(Duration.ZERO));
        soundPlayer.play();
        previousTrack = "";
    }

    public void endEnemySong(String song) {
        soundPlayer.stop();
        returnToOldTrack();
    }

    public void stopAllAudio() {
        if(soundPlayer != null) soundPlayer.stop();
    }

    public void playSound(String file) {
        Media sound = new Media(Paths.get(file).toUri().toString());
        MediaPlayer soundPlayer = new MediaPlayer(sound);
        soundPlayer.setVolume(GameStage.effectVolume);
        soundPlayer.play();
    }
}
