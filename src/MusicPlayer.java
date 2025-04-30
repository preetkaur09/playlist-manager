import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MusicPlayer extends PlaybackListener {
    // we will need a way to store our song's details, so ww will be creating a song class
    private Song currentSong;

    //use JLayer Library to create an AdvancedPlayer obj which will handle playing the music
    private AdvancedPlayer advancedPlayer;

    // pause boolean flag used to indicate whether the player has been paused
    private boolean isPaused;

    //stores in the last frame when the playback is finished(used for pausing and resuming)
    private int currentFrame;

    //constructor
    public MusicPlayer(){

    }

    public void loadSong(Song song){
        currentSong = song;

        // play the current song if not null
        if(currentSong != null){
            playCurrentSong();
        }
    }

    public void pauseSong(){
        if(advancedPlayer != null){
            //update isPaused flag
            isPaused = true;

            //then we want to stop the player
            stopSong();
        }
    }

    public void stopSong(){
        if(advancedPlayer != null){
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void playCurrentSong(){
        try{
            //read mp3 audio data
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            //create a new advanced player
            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            //start music
            startMusicThread();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //create a thread that will handle playing the music
    private void startMusicThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(isPaused){
                        //resume music from the last frame
                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);

                    }else{
                        //play music from the beginning
                        advancedPlayer.play();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        //this method gets called in the beginning of the song
        System.out.println("Playback Started");
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        //this method gets called when the song finishes or if the player gets closed
        System.out.println("Playback Finished");
        if(isPaused ){
            currentFrame = evt.getFrame();
        }
    }
}

