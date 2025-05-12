import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String name;
    private List<String> songPaths;

    public Playlist(String name) {
        this.name = name;
        this.songPaths = new ArrayList<>();
    }

    public boolean addSong(Song song) {
        if (!songPaths.contains(song.getFilePath())) {
            return songPaths.add(song.getFilePath());
        }
        return false;
    }

    public boolean removeSong(Song song) {
        songPaths.remove(song.getFilePath());
        return false;
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
