package view;

import models.MusicPlayer;
import models.Playlist;
import models.Song;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class MusicPlayerGUI extends JFrame {

    // Color configuration for the GUI
    public static final Color FRAME_COLOR = Color.BLACK; // Background color of the frame
    public static final Color TEXT_COLOR = Color.WHITE; // Text color for labels
    private MusicPlayer musicPlayer; // Instance of the MusicPlayer class

    // JFileChooser to allow file selection for loading songs
    private JFileChooser jFileChooser;
    private JLabel songTitle, songArtist; // Labels to display song title and artist
    private JPanel playbackBtns; // Panel to hold playback buttons
    private JSlider playbackSlider; // Slider for song playback position
    private List<Playlist> playlists = new ArrayList<>(); // List to hold playlists
    private Playlist currentPlaylist = null; // Currently selected playlist
    private Queue<Song> songQueue = new LinkedList<>(); // Queue for songs to be played
    private Stack<Song> songHistory = new Stack<>(); // Stack to keep track of played songs
    private JComboBox<Playlist> playlistSelector; // Dropdown for selecting playlists

    public MusicPlayerGUI() {
        // Calls JFrame constructor to configure the GUI and set the title header to "Music Player"
        super("Music Player");

        // Set the width and height of the window
        setSize(400, 600);

        // End process when app is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Launch the app at the center of the screen
        setLocationRelativeTo(null);

        // Prevent the app from being resized
        setResizable(false);

        // Set layout to null which allows us to control the (x, y) coordinates of our components
        // and also set the height and width
        setLayout(null);

        // Change the frame color
        getContentPane().setBackground(FRAME_COLOR);

        musicPlayer = new MusicPlayer(); // Initialize the music player
        jFileChooser = new JFileChooser(); // Initialize the file chooser

        // Set a default path for the file explorer
        jFileChooser.setCurrentDirectory(new File("src/assets/drive-download-20250416T121646Z-001"));

        // Filter file chooser to only see .mp3 files
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));

        // Initialize the playlist selector (dropdown)
        playlistSelector = new JComboBox<>();
        add(playlistSelector); // Add the playlist selector to the GUI

        // Populate the playlist selector with existing playlists
        for (Playlist playlist : playlists) {
            playlistSelector.addItem(playlist); // Add each playlist to the dropdown
        }

        addGuiComponents(); // Call method to add other GUI components
    }

    private void addGuiComponents() {
        // Add toolbar to the GUI
        addToolbar();

        // Load record image and set its bounds
        JLabel songImage = new JLabel(loadImage("src/assets/drive-download-20250416T121646Z-001/record.png"));
        songImage.setBounds(0, 50, getWidth() - 20, 225); // Set position and size
        add(songImage); // Add the image label to the GUI

        // Song title label
        songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 285, getWidth() - 10, 30); // Set position and size
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24)); // Set font style and size
        songTitle.setForeground(TEXT_COLOR); // Set text color
        songTitle.setHorizontalAlignment(SwingConstants.CENTER); // Center align the text
        add(songTitle); // Add the title label to the GUI

        // Song artist label
        songArtist = new JLabel("Artist");
        songArtist.setBounds(0, 315, getWidth() - 10, 30); // Set position and size
        songArtist.setFont(new Font("Dialog", Font.PLAIN, 24)); // Set font style and size
        songArtist.setForeground(TEXT_COLOR); // Set text color
        songArtist.setHorizontalAlignment(SwingConstants.CENTER); // Center align the text
        add(songArtist); // Add the artist label to the GUI

        // Playback slider for controlling song position
        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0); // Horizontal slider
        playbackSlider.setBounds(getWidth() / 2 - 300 / 2, 365, 300, 40); // Set position and size
        playbackSlider.setBackground(null); // Set background to transparent
        add(playbackSlider); // Add the slider to the GUI

        // Add playback buttons (previous, play, next)
        addPlaybackBtns();
    }

    private void addToolbar() {
        JToolBar toolBar = new JToolBar(); // Create a toolbar
        toolBar.setBounds(0, 0, getWidth(), 20); // Set position and size

        // Prevent toolbar from being moved
        toolBar.setFloatable(false);

        // Add dropdown menu to the toolbar
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        // Add a song menu where we will place the loading song option
        JMenu songMenu = new JMenu("Song");
        menuBar.add(songMenu);

        // Add the "load song" item in the songMenu
        JMenuItem loadSong = new JMenuItem("Load Song");
        songMenu.add(loadSong);

        // Add the playlist menu
        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        // Add items to the playlist menu
        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        playlistMenu.add(createPlaylist);

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        playlistMenu.add(loadPlaylist);

        JMenuItem renamePlaylist = new JMenuItem("Rename Playlist");
        playlistMenu.add(renamePlaylist);

        JMenuItem deletePlaylist = new JMenuItem("Delete Playlist");
        playlistMenu.add(deletePlaylist);

        JMenuItem viewPlaylist = new JMenuItem("View Songs");
        playlistMenu.add(viewPlaylist);

        JMenuItem playAll = new JMenuItem("Play All");
        playlistMenu.add(playAll);

        JMenuItem songToPlaylist = new JMenuItem("Add Song to Playlist");
        playlistMenu.add(songToPlaylist);

        // Add the search playlist menu item
        JMenuItem searchPlaylist = new JMenuItem("Search Playlist by Name");
        playlistMenu.add(searchPlaylist);

        JMenuItem removeSongFromPlaylist = new JMenuItem("Remove Song from Playlist");
        playlistMenu.add(removeSongFromPlaylist);

        // Action listener for removing a song from the playlist
        removeSongFromPlaylist.addActionListener(e -> {
            if (currentPlaylist == null || currentPlaylist.getSongPaths().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No songs in the playlist to remove.");
                return;
            }

            // Get the names of songs in the current playlist
            String[] songNames = currentPlaylist.getSongPaths().stream()
                    .map(path -> new File(path).getName())
                    .toArray(String[]::new);

            // Show dialog to select a song to remove
            String selectedSong = (String) JOptionPane.showInputDialog(null, "Select a song to remove:",
                    "Remove Song", JOptionPane.QUESTION_MESSAGE, null, songNames, songNames[0]);

            if (selectedSong != null) {
                // Find the path of the selected song
                String songPathToRemove = currentPlaylist.getSongPaths().stream()
                        .filter(path -> new File(path).getName().equals(selectedSong))
                        .findFirst().orElse(null);

                if (songPathToRemove != null) {
                    // Remove the song from the current playlist
                    currentPlaylist.removeSong(new Song(songPathToRemove));
                    JOptionPane.showMessageDialog(null, "Song removed from playlist: " + selectedSong);
                }
            }
        });

        // Action listener for searching a playlist by name
        searchPlaylist.addActionListener(e -> {
            String searchName = JOptionPane.showInputDialog("Enter playlist name to search:");
            if (searchName != null && !searchName.trim().isEmpty()) {
                Playlist foundPlaylist = binarySearchPlaylist(searchName.trim());
                if (foundPlaylist != null) {
                    JOptionPane.showMessageDialog(this, "Found Playlist: " + foundPlaylist.getName());
                } else {
                    JOptionPane.showMessageDialog(this, "Playlist not found.");
                }
            }
        });

        // Action listener for loading a song
        loadSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show file chooser dialog to select a song
                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                // Check if the user pressed the "open" button
                if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                    // Create a song object based on the selected file
                    Song song = new Song(selectedFile.getPath());

                    // Load the song in the music player
                    musicPlayer.loadSong(song);

                    // Update song title and artist labels
                    updateSongTitleAndArtist(song);

                    // Toggle on pause button and toggle off play button
                    enablePauseButtonDisablePlayButton();
                }
            }
        });

        // Action listener for adding a song to the current playlist
        songToPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                if (result == JFileChooser.APPROVE_OPTION && selectedFile != null && currentPlaylist != null) {
                    Song song = new Song(selectedFile.getPath());
                    currentPlaylist.addSong(song); // Add the song to the current playlist
                    JOptionPane.showMessageDialog(null, "Song added to playlist: " + song.getSongTitle());
                } else {
                    JOptionPane.showMessageDialog(null, "No playlist selected or file not valid.");
                }
            }
        });

        // Action listener for creating a new playlist
        createPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("Enter playlist name:");
                if (name != null && !name.trim().isEmpty()) {
                    Playlist newPlaylist = new Playlist(name.trim());
                    playlists.add(newPlaylist); // Add the new playlist to the list
                    sortPlaylists(); // Sort the playlists after adding
                    currentPlaylist = newPlaylist; // Set the current playlist to the new one
                    JOptionPane.showMessageDialog(null, "Playlist created!");
                }
            }
        });

        // Action listener for loading an existing playlist
        loadPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playlists.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No playlists found.");
                    return;
                }

                // Get the names of all playlists
                String[] names = playlists.stream().map(Playlist::getName).toArray(String[]::new);
                String selected = (String) JOptionPane.showInputDialog(null, "Select a playlist:",
                        "Load Playlist", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);

                if (selected != null) {
                    // Load the selected playlist
                    for (Playlist p : playlists) {
                        if (p.getName().equals(selected)) {
                            currentPlaylist = p; // Set the current playlist
                            JOptionPane.showMessageDialog(null, "Loaded playlist: " + selected);
                            break;
                        }
                    }
                }
            }
        });

        // Action listener for renaming a playlist
        renamePlaylist.addActionListener(e -> {
            if (currentPlaylist == null) {
                JOptionPane.showMessageDialog(this, "No playlist loaded.");
                return;
            }
            String newName = JOptionPane.showInputDialog("Enter new name for playlist:");
            if (newName != null && !newName.trim().isEmpty()) {
                currentPlaylist.setName(newName.trim()); // Update the playlist name
                sortPlaylists(); // Sort the playlists after renaming
                JOptionPane.showMessageDialog(this, "Playlist renamed to: " + newName);
            }
        });

        // Action listener for deleting a playlist
        deletePlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playlists.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No playlists to delete.");
                    return;
                }

                // Get the names of all playlists
                String[] names = playlists.stream().map(Playlist::getName).toArray(String[]::new);
                String selected = (String) JOptionPane.showInputDialog(null, "Select playlist to delete:",
                        "Delete Playlist", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);

                if (selected != null) {
                    // Remove the selected playlist
                    playlists.removeIf(p -> p.getName().equals(selected));
                    if (currentPlaylist != null && currentPlaylist.getName().equals(selected)) {
                        currentPlaylist = null; // Clear current playlist if it was deleted
                    }
                    JOptionPane.showMessageDialog(null, "Playlist deleted.");
                }
            }
        });

        // Action listener for viewing songs in the current playlist
        viewPlaylist.addActionListener(e -> {
            if (currentPlaylist == null || currentPlaylist.getSongPaths().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No songs in playlist.");
                return;
            }
            StringBuilder builder = new StringBuilder("Songs in playlist:\n");
            for (String path : currentPlaylist.getSongPaths()) {
                builder.append(new File(path).getName()).append("\n"); // Append song names to the message
            }
            JOptionPane.showMessageDialog(null, builder.toString()); // Show the list of songs
        });

        // Action listener for playing all songs in the current playlist
        playAll.addActionListener(e -> {
            if (currentPlaylist == null || currentPlaylist.getSongPaths().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No playlist selected or empty.");
                return;
            }

            songQueue.clear(); // Clear the song queue
            for (String path : currentPlaylist.getSongPaths()) {
                songQueue.add(new Song(path)); // Add each song in the playlist to the queue
            }

            playNextSong(); // Start playing the first song
        });

        add(toolBar); // Add the toolbar to the GUI
    }

    private void playNextSong() {
        if (!songQueue.isEmpty()) {
            // Stop the current song
            musicPlayer.stopSong();

            Song nextSong = songQueue.poll(); // Get the next song from the queue
            songHistory.push(nextSong); // Add the song to the history stack
            musicPlayer.loadSong(nextSong); // Load the next song
            updateSongTitleAndArtist(nextSong); // Update the displayed song title and artist
            updatePlaylistSlider(nextSong); // Update the playback slider
            enablePauseButtonDisablePlayButton(); // Enable pause button and disable play button
        } else {
            JOptionPane.showMessageDialog(null, "End of playlist."); // Notify user if the end of the playlist is reached
            enablePlayButtonDisablePauseButton(); // Enable play button and disable pause button
        }
    }

    private void addPlaybackBtns() {
        playbackBtns = new JPanel(); // Create a panel for playback buttons
        playbackBtns.setBounds(0, 435, getWidth() - 10, 80); // Set position and size
        playbackBtns.setBackground(null); // Set background to transparent

        // Previous button
        JButton prevButton = new JButton(loadImage("src/assets/drive-download-20250416T121646Z-001/previous.png"));
        prevButton.addActionListener(e -> {
            if (songHistory.size() >= 2) {
                songHistory.pop(); // Remove current song from history
                Song previous = songHistory.pop(); // Get the last song
                musicPlayer.loadSong(previous); // Load the previous song
                updateSongTitleAndArtist(previous); // Update the displayed song title and artist
                enablePauseButtonDisablePlayButton(); // Enable pause button and disable play button
            } else {
                JOptionPane.showMessageDialog(null, "No previous song."); // Notify user if no previous song exists
            }
        });
        prevButton.setBorderPainted(false); // Remove border from button
        prevButton.setBackground(null); // Set background to transparent
        playbackBtns.add(prevButton); // Add previous button to the panel

        // Play button
        JButton playButton = new JButton(loadImage("src/assets/drive-download-20250416T121646Z-001/play.png"));
        playButton.setBorderPainted(false); // Remove border from button
        playButton.setBackground(null); // Set background to transparent
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Toggle on play button and toggle off pause button
                enablePauseButtonDisablePlayButton();

                // Play or resume song
                musicPlayer.playCurrentSong();
            }
        });
        playbackBtns.add(playButton); // Add play button to the panel

        // Pause button
        JButton pauseButton = new JButton(loadImage("src/assets/drive-download-20250416T121646Z-001/pause.png"));
        pauseButton.setBorderPainted(false); // Remove border from button
        pauseButton.setVisible(false); // Initially hide the pause button
        pauseButton.setBackground(null); // Set background to transparent
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Toggle off pause button and toggle on play button
                enablePlayButtonDisablePauseButton();

                // Pause the song
                musicPlayer.pauseSong();
            }
        });
        playbackBtns.add(pauseButton); // Add pause button to the panel

        // Next button
        JButton nextButton = new JButton(loadImage("src/assets/drive-download-20250416T121646Z-001/next.png"));
        nextButton.addActionListener(e -> playNextSong()); // Action to play the next song
        nextButton.setBorderPainted(false); // Remove border from button
        nextButton.setBackground(null); // Set background to transparent
        playbackBtns.add(nextButton); // Add next button to the panel

        add(playbackBtns); // Add the playback buttons panel to the GUI
    }

    private void updateSongTitleAndArtist(Song song) {
        songTitle.setText(song.getSongTitle()); // Update the song title label
        songArtist.setText(song.getSongArtist()); // Update the song artist label
    }

    private void updatePlaylistSlider(Song song) {
        // Update max count for slider based on the song's frame count
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());

        // Create the song length label
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();

        // Beginning will be 00:00
        JLabel labelBeginning = new JLabel(("00:00"));
        labelBeginning.setFont(new Font("Dialog", Font.BOLD, 18));
        labelBeginning.setForeground(TEXT_COLOR);

        // End will vary depending on the song
    }

    private void enablePauseButtonDisablePlayButton() {
        // Retrieve reference to play button from playbackBtns panel
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        // Turn off play button
        playButton.setVisible(false);
        playButton.setEnabled(false);

        // Turn on pause button
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    private void enablePlayButtonDisablePauseButton() {
        // Retrieve reference to play button from playbackBtns panel
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        // Turn on play button
        playButton.setVisible(true);
        playButton.setEnabled(true);

        // Turn off pause button
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }

    private ImageIcon loadImage(String imagePath) {
        try {
            // Read the image file from the given path
            BufferedImage image = ImageIO.read(new File(imagePath));

            // Returns an image icon so that our component can render the image
            return new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace if an error occurs
        }

        // Could not find resource
        return null;
    }

    private void selectPlaylist(Playlist playlist) {
        currentPlaylist = playlist; // Set the current playlist
        // Update UI to reflect the selected playlist
        JOptionPane.showMessageDialog(this, "Selected Playlist: " + currentPlaylist.getName());
    }

    private void sortPlaylists() {
        // Sort the playlists alphabetically by name
        Collections.sort(playlists, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
    }

    private Playlist binarySearchPlaylist(String name) {
        int left = 0;
        int right = playlists.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            Playlist midPlaylist = playlists.get(mid);

            int comparison = midPlaylist.getName().compareToIgnoreCase(name);
            if (comparison == 0) {
                return midPlaylist; // Found the playlist
            } else if (comparison < 0) {
                left = mid + 1; // Search in the right half
            } else {
                right = mid - 1; // Search in the left half
            }
        }
        return null; // Playlist not found
    }
}
