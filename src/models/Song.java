package models;

import com.mpatric.mp3agic.Mp3File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import java.io.File;

//class used to describe a song
public class Song {
    private String songTitle;
    private String songArtist;
    private String songLength;
    private String filePath;
    private Mp3File mp3File;
    private double frameRatePerMilliseconds;

    public Song(String filePath){
        this.filePath = filePath;
        try{
             mp3File = new Mp3File(filePath);
             frameRatePerMilliseconds = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();

            //use the jaudiotagger library to create audiofile obj to read mp3 file's information
            AudioFile audioFile = AudioFileIO.read(new File(filePath));

            //read through the meta data of the audio file
            Tag tag = audioFile.getTag();
            if(tag != null){
                songTitle = tag.getFirst(FieldKey.TITLE);
                songArtist = tag.getFirst(FieldKey.ARTIST);
            }else{
                // could not read through mp3 file's meta data
                songTitle = "N/A";
                songArtist = "N/A";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //getters
    public String getSongTitle() {
        return songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }



    public String getFilePath() {
        return filePath;
    }

    public Mp3File getMp3File(){return mp3File;}
    public double getFrameRatePerMilliseconds() {return frameRatePerMilliseconds;}

    public String getSongLength() {
        if (mp3File != null) {
            long milliseconds = mp3File.getLengthInMilliseconds();
            int seconds = (int) (milliseconds / 1000) % 60;
            int minutes = (int) ((milliseconds / 1000) / 60);
            return String.format("%02d:%02d", minutes, seconds);
        }
        return "N/A";
    }
}
