package omxplayer.remote.app.utils;

import android.content.Context;
import android.media.MediaPlayer;

public class Sound {

    private MediaPlayer player;

    public Sound() {
        // TODO Auto-generated constructor stub
        player = null;
    }

    /**
     * Method that play a soud given as a aparmater
     *
     * @param c
     * @param sound -->id of sound to be played
     */
    public void play(Context c, int sound) {
        stop();// stop any sound before running the input sound
        player = MediaPlayer.create(c, sound);
        player.start();
    }

    /*
     * stop running sound if any
     */
    private void stop() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

}
