import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String name;
    private List<String> songPaths;

    public Playlist(String name) {
        this.name = name;
        this.songPaths = new ArrayList<>();
    }

    public void addSong(Song song) {
        songPaths.add(song.getFilePath());
    }

    public void removeSong(Song song) {
        songPaths.remove(song.getFilePath());
    }

    public List<String> getSongPaths() {
        return songPaths;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }
}
