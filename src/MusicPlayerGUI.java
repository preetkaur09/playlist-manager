import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class MusicPlayerGUI extends JFrame {
    //color configuration
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    private MusicPlayer musicPlayer;

    //allow us to use file explorer in our app
    private JFileChooser jFileChooser;

    private JLabel songTitle, songArtist;
    private JPanel playbackBtns;

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
        JSlider playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
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
        songMenu.add(loadSong);

        //now we will add the playlist menu
        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        //then add the items to the playlist menu
        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        playlistMenu.add(createPlaylist);

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        playlistMenu.add(loadPlaylist);

        add(toolBar);
    }

    private void addPlaybackBtns(){
        playbackBtns = new JPanel();
        playbackBtns.setBounds(0, 435, getWidth() - 10, 80);
        playbackBtns.setBackground(null);

        //previous button
        JButton prevButton = new JButton(loadImage("src/assets/drive-download-20250416T121646Z-001/previous.png"));
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
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        playbackBtns.add(nextButton);

        add(playbackBtns);
    }


    private void updateSongTitleAndArtist(Song song){
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());
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
}









