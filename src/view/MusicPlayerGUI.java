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
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;
import java.util.*;



public class MusicPlayerGUI extends JFrame {
    //color configuration
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;
    private MusicPlayer musicPlayer;
    //allow us to use file explorer in our app
    private JFileChooser jFileChooser;
    private JLabel songTitle, songArtist;
    private JPanel playbackBtns;
    private JSlider playbackSlider;
    private List<Playlist> playlists = new ArrayList<>();
    private Playlist currentPlaylist = null;
    private Queue<Song> songQueue = new LinkedList<>();
    private Stack<Song> songHistory = new Stack<>();
    private JComboBox<Playlist> playlistSelector;


    public MusicPlayerGUI(){
        //calls JFrame constructor to configure out gui and set the title header to "Music Player"
        super("Music Player");

        //set the width and height
        setSize(400, 600);

        //end process when app is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //launch the app at the center of the screen
        setLocationRelativeTo(null);

        //prevent the app from being resized
        setResizable(false);

        //set layout to null which allows us to control the (x, y) coordinates of our components
        //and also set the height and width
        setLayout(null);

        //change the frame color
        getContentPane().setBackground(FRAME_COLOR);

        musicPlayer = new MusicPlayer();
        jFileChooser = new JFileChooser();

        //set a default path for file explorer
        jFileChooser.setCurrentDirectory(new File("src/assets/drive-download-20250416T121646Z-001"));

        //filter file chooser to only see .mp3 files
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));

        // Initialize the playlist selector
        playlistSelector = new JComboBox<>();
        add(playlistSelector);

        // Populate the playlist selector with existing playlists
        for (Playlist playlist : playlists) {
            playlistSelector.addItem(playlist);
        }

        // Set bounds for the playlist selector
        playlistSelector.setBounds(10, 10, 200, 30); // Adjust the position as needed
        add(playlistSelector);

        // Add an action listener to handle playlist selection
        playlistSelector.addActionListener(e -> {
            Playlist selectedPlaylist = (Playlist) playlistSelector.getSelectedItem();
            selectPlaylist(selectedPlaylist);

        });

        addGuiComponents();
    }

    private void addGuiComponents(){
        // add toolbar
        addToolbar();

        //load record image
        JLabel songImage = new JLabel(loadImage("src/assets/drive-download-20250416T121646Z-001/record.png"));
        songImage.setBounds(0, 50, getWidth() - 20, 225);
        add(songImage);

        //song title
        songTitle = new JLabel ("Song Title");
        songTitle.setBounds(0, 285, getWidth() - 10, 30);
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        //song artist
        songArtist = new JLabel("Artist");
        songArtist.setBounds(0, 315, getWidth() - 10, 30);
        songArtist.setFont(new Font("Dialog", Font.PLAIN, 24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        //playback slider
        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth()/2 - 300/2, 365, 300, 40);
        playbackSlider.setBackground(null);
        add(playbackSlider);

        //playback buttons (previous, play, next);
        addPlaybackBtns();
    }

    private void addToolbar(){
        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, getWidth(), 20);

        //prevent toolbar from being moved
        toolBar.setFloatable(false);

        //add dropdown menu
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        //now we will add a song menu where we will place the loading song option
        JMenu songMenu = new JMenu("Song");
        menuBar.add(songMenu);

        // add the "load song" item in the songMenu
        JMenuItem loadSong = new JMenuItem("Load Song");
        songMenu.add(loadSong);

        //now we will add the playlist menu
        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        //then add the items to the playlist menu
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

        //JMenuItem savePlaylist = new JMenuItem("Save Playlist");
        //playlistMenu.add(savePlaylist);

        //JMenuItem loadPlaylistFromFile = new JMenuItem("Load from File");
        //playlistMenu.add(loadPlaylistFromFile);

        JMenuItem songToPlaylist = new JMenuItem("Add Song to Playlist");
        playlistMenu.add(songToPlaylist);

        // Add the search playlist menu item
        JMenuItem searchPlaylist = new JMenuItem("Search Playlist by Name");
        playlistMenu.add(searchPlaylist);
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


        loadSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // an integer is returned to us to let us know what the user did
                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                //this means that we are also checking to see if the user pressed the "open" button
                if(result == JFileChooser.APPROVE_OPTION && selectedFile != null){
                    //create a song obj based on selected file
                    Song song = new Song(selectedFile.getPath());

                    //load song in music player
                    musicPlayer.loadSong(song);

                    // update song title and artist
                    updateSongTitleAndArtist(song);

                    //toggle on pause button and toggle off play button
                    enablePauseButtonDisablePlayButton();

                }
            }
        });

        songToPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                if (result == JFileChooser.APPROVE_OPTION && selectedFile != null && currentPlaylist != null) {
                    Song song = new Song(selectedFile.getPath());
                    currentPlaylist.addSong(song);
                    JOptionPane.showMessageDialog(null, "Song added to playlist: " + song.getSongTitle());
                } else {
                    JOptionPane.showMessageDialog(null, "No playlist selected or file not valid.");
                }
            }
        });

        // Playlist Actions
        createPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("Enter playlist name:");
                if (name != null && !name.trim().isEmpty()) {
                    Playlist newPlaylist = new Playlist(name.trim());
                    playlists.add(newPlaylist);
                    sortPlaylists(); // Sort the playlists after adding
                    currentPlaylist = newPlaylist;
                    JOptionPane.showMessageDialog(null, "Playlist created!");
                }
            }
        });

        loadPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playlists.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No playlists found.");
                    return;
                }

                String[] names = playlists.stream().map(Playlist::getName).toArray(String[]::new);
                String selected = (String) JOptionPane.showInputDialog(null, "Select a playlist:",
                        "Load models.Playlist", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);

                if (selected != null) {
                    for (Playlist p : playlists) {
                        if (p.getName().equals(selected)) {
                            currentPlaylist = p;
                            JOptionPane.showMessageDialog(null, "Loaded playlist: " + selected);
                            break;
                        }
                    }
                }
            }
        });

        renamePlaylist.addActionListener(e -> {
            if (currentPlaylist == null) {
                JOptionPane.showMessageDialog(this, "No playlist loaded.");
                return;
            }
            String newName = JOptionPane.showInputDialog("Enter new name for playlist:");
            if (newName != null && !newName.trim().isEmpty()) {
                currentPlaylist.setName(newName.trim());
                sortPlaylists(); // Sort the playlists after renaming
                JOptionPane.showMessageDialog(this, "Playlist renamed to: " + newName);
            }
        });

        deletePlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playlists.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No playlists to delete.");
                    return;
                }

                String[] names = playlists.stream().map(Playlist::getName).toArray(String[]::new);
                String selected = (String) JOptionPane.showInputDialog(null, "Select playlist to delete:",
                        "Delete Playlist", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);

                if (selected != null) {
                    playlists.removeIf(p -> p.getName().equals(selected));
                    if (currentPlaylist != null && currentPlaylist.getName().equals(selected)) {
                        currentPlaylist = null;
                    }
                    JOptionPane.showMessageDialog(null, "Playlist deleted.");
                }
            }
        });

        viewPlaylist.addActionListener(e -> {
            if (currentPlaylist == null || currentPlaylist.getSongPaths().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No songs in playlist.");
                return;
            }
            StringBuilder builder = new StringBuilder("Songs in playlist:\n");
            for (String path : currentPlaylist.getSongPaths()) {
                builder.append(new File(path).getName()).append("\n");
            }
            JOptionPane.showMessageDialog(null, builder.toString());
        });

        playAll.addActionListener(e -> {
            if (currentPlaylist == null || currentPlaylist.getSongPaths().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No playlist selected or empty.");
                return;
            }

            songQueue.clear();
            for (String path : currentPlaylist.getSongPaths()) {
                songQueue.add(new Song(path));
            }

            playNextSong();
        });

//        savePlaylist.addActionListener(e -> {
//            if (currentPlaylist == null) {
//                JOptionPane.showMessageDialog(null, "No playlist selected.");
//                return;
//            }
//
//            try {
//                File file = new File("playlists/" + currentPlaylist.getName() + ".txt");
//                file.getParentFile().mkdirs();
//                java.io.FileWriter fw = new java.io.FileWriter(file);
//                for (String path : currentPlaylist.getSongPaths()) {
//                    fw.write(path + "\n");
//                }
//                fw.close();
//                JOptionPane.showMessageDialog(null, "Playlist saved.");
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });

//        loadPlaylistFromFile.addActionListener(e -> {
//            JFileChooser chooser = new JFileChooser("playlists");
//            int result = chooser.showOpenDialog(null);
//            if (result == JFileChooser.APPROVE_OPTION) {
//                try {
//                    File file = chooser.getSelectedFile();
//                    Scanner sc = new Scanner(file);
//                    Playlist loaded = new Playlist(file.getName().replace(".txt", ""));
//                    while (sc.hasNextLine()) {
//                        String path = sc.nextLine();
//                        loaded.getSongPaths().add(path);
//                    }
//                    sc.close();
//                    playlists.add(loaded);
//                    currentPlaylist = loaded;
//                    JOptionPane.showMessageDialog(null, "Playlist loaded from file.");
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });


        add(toolBar);
    }

    private void playNextSong() {
        if (!songQueue.isEmpty()) {
            // Stop the current song
            musicPlayer.stopSong();

            Song nextSong = songQueue.poll();
            songHistory.push(nextSong);
            musicPlayer.loadSong(nextSong);
            updateSongTitleAndArtist(nextSong);
            updatePlaylistSlider(nextSong);
            enablePauseButtonDisablePlayButton();
        } else {
            JOptionPane.showMessageDialog(null, "End of playlist.");
            enablePlayButtonDisablePauseButton();
        }
    }

    private void addPlaybackBtns(){
        playbackBtns = new JPanel();
        playbackBtns.setBounds(0, 435, getWidth() - 10, 80);
        playbackBtns.setBackground(null);

        //previous button
        JButton prevButton = new JButton(loadImage("src/assets/drive-download-20250416T121646Z-001/previous.png"));
        prevButton.addActionListener(e -> {
            if (songHistory.size() >= 2) {
                songHistory.pop(); // current
                Song previous = songHistory.pop(); // last one
                musicPlayer.loadSong(previous);
                updateSongTitleAndArtist(previous);
                enablePauseButtonDisablePlayButton();
            } else {
                JOptionPane.showMessageDialog(null, "No previous song.");
            }
        });
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        playbackBtns.add(prevButton);

        //play button
        JButton playButton = new JButton(loadImage("src/assets/drive-download-20250416T121646Z-001/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // toggle on play button and toggle off pause button
                enablePauseButtonDisablePlayButton();

                //play or resume song
                musicPlayer.playCurrentSong();
            }
        });
        playbackBtns.add(playButton);

        //pause button
        JButton pauseButton = new JButton(loadImage("src/assets/drive-download-20250416T121646Z-001/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setVisible(false);
        pauseButton.setBackground(null);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //toggle off pause button and toggle on play button
                enablePlayButtonDisablePauseButton();

                // pause the song
                musicPlayer.pauseSong();
            }
        });
        playbackBtns.add(pauseButton);

        //next button
        JButton nextButton = new JButton(loadImage("src/assets/drive-download-20250416T121646Z-001/next.png"));
        nextButton.addActionListener(e -> playNextSong());
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        playbackBtns.add(nextButton);

        add(playbackBtns);
    }

    private void updateSongTitleAndArtist(Song song){
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());
    }

    private void updatePlaylistSlider(Song song){
        // update max count for slider
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());

        // create the song length label
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();

        // beginning will be 00:00
        JLabel labelBeginning = new JLabel(("00:00"));
        labelBeginning.setFont(new Font("Dialog", Font.BOLD, 18 ));
        labelBeginning.setForeground(TEXT_COLOR);

        // end will vary depending on the song
    }

    private void enablePauseButtonDisablePlayButton(){
        // retrieve reference to play button from playbackBtns panel
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        //turn off play button
        playButton.setVisible(false);
        playButton.setEnabled(false);

        //turn on pause button
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    private void enablePlayButtonDisablePauseButton(){
        // retrieve reference to play button from playbackBtns panel
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        //turn on play button
        playButton.setVisible(true);
        playButton.setEnabled(true);

        //turn off pause button
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }

    private ImageIcon loadImage(String imagePath){
        try{
            //read the image file from the given path
            BufferedImage image = ImageIO.read(new File(imagePath));

            //returns an image icon so that our component can render the image
            return new ImageIcon(image);
        }catch(Exception e){
            e.printStackTrace();
        }

        //could not find resource
        return null;
    }

    private void selectPlaylist(Playlist playlist) {
        currentPlaylist = playlist;
        // Update UI to reflect the selected playlist
        JOptionPane.showMessageDialog(this, "Selected models.Playlist: " + currentPlaylist.getName());
    }

    private void sortPlaylists() {
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









