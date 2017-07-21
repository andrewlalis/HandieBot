package handiebot.view;

import handiebot.HandieBot;
import handiebot.lavaplayer.playlist.Playlist;
import handiebot.lavaplayer.playlist.UnloadedTrack;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

import static handiebot.HandieBot.resourceBundle;

/**
 * @author Andrew Lalis & Zino Holwerda
 * This class inherits JFrame and simplifies the creation of a window.
 */
public class BotWindow extends JFrame {

    //Console output panel.
    private JTextPane outputArea;

    //Playlist display variables.
    private DefaultListModel<String> playlistNamesModel;
    private DefaultListModel<String> currentPlaylistModel;
    private JList<String> playlistNamesList;
    private JList<String> currentPlaylistList;
    private ListSelectionListener playlistListener;

    public BotWindow(){
        super(HandieBot.APPLICATION_NAME);
        //Setup GUI
        //Output area.
        outputArea = new JTextPane();
        outputArea.setBackground(Color.white);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(outputArea);
        scrollPane.setAutoscrolls(true);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        //Playlist shower
        this.playlistNamesModel = new DefaultListModel<>();
        this.currentPlaylistModel = new DefaultListModel<>();
        this.playlistNamesList = new JList<>(this.playlistNamesModel);
        this.playlistNamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.currentPlaylistList = new JList<>(this.currentPlaylistModel);
        this.currentPlaylistList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.playlistListener = e -> {
            System.out.println("user updated list.");
            updatePlaylistData();
        };
        this.playlistNamesList.addListSelectionListener(this.playlistListener);
        updatePlaylistData();

        JPanel playlistDisplayPanel = new JPanel(new BorderLayout());

        //Song names scroll pane.
        JScrollPane songNamesScrollPane = new JScrollPane(this.currentPlaylistList);
        songNamesScrollPane.setPreferredSize(new Dimension(250, 200));
        playlistDisplayPanel.add(songNamesScrollPane, BorderLayout.PAGE_END);

        //Playlist name scroll pane.
        JScrollPane playlistNamesScrollPane = new JScrollPane(playlistNamesList);
        playlistNamesScrollPane.setPreferredSize(new Dimension(250, 1000));
        playlistDisplayPanel.add(playlistNamesScrollPane, BorderLayout.CENTER);

        getContentPane().add(playlistDisplayPanel, BorderLayout.EAST);

        //Command field.
        JTextField commandField = new JTextField();
        commandField.setFont(new Font("Courier New", Font.PLAIN, 16));
        commandField.addKeyListener(new CommandLineListener());
        getContentPane().add(commandField, BorderLayout.PAGE_END);

        //Standard JFrame setup code.
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //Add a listener to override the user attempting to close the program.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog((JFrame) e.getSource(), resourceBundle.getString("window.close.question"),
                        resourceBundle.getString("window.close.title"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                    HandieBot.quit();
                }
            }
        });

        //Attempt to set the icon of the window.
        try {
            setIconImage(ImageIO.read(getClass().getClassLoader().getResourceAsStream("avatarIcon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setJMenuBar(new MenuBar(this));
        //SelectionController controller = new SelectionController();
        setPreferredSize(new Dimension(800, 600));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Sets the playlist data in the window.
     */
    private void updatePlaylistData() {
        List<String> playlistNames = Playlist.getAvailablePlaylists();
        this.playlistNamesList.removeListSelectionListener(this.playlistListener);
        this.playlistNamesModel.clear();
        for (String name : playlistNames){
            this.playlistNamesModel.addElement(name);
        }
        this.playlistNamesList.addListSelectionListener(this.playlistListener);
        String selectedValue = this.playlistNamesList.getSelectedValue();
        System.out.println("selected value: "+selectedValue);
        if (selectedValue != null && Playlist.playlistExists(selectedValue)){
            Playlist playlist = new Playlist(selectedValue);
            playlist.load();
            List<UnloadedTrack> tracks = playlist.getTracks();
            this.currentPlaylistModel.clear();
            for (int i = 0; i < playlist.getTrackCount(); i++){
                this.currentPlaylistModel.addElement(tracks.get(i).getTitle());
            }
        }
        /*
        this.playlistNamesList.addListSelectionListener(e -> {
            String name = playlistNamesList.getSelectedValue();
            String path = System.getProperty("user.home") + "/.handiebot/playlist/" + name + ".txt";
            File playlistFile = new File(path);
            if (playlistFile.exists()) {
                try {
                    List<String> lines = Files.readAllLines(Paths.get(playlistFile.toURI()));
                    int trackCount = Integer.parseInt(lines.remove(0));
                    String[] words = {"A"};
                    StringBuilder sb = new StringBuilder();
                    for (int i1 = 0; i1 < trackCount; i1++) {
                        words = lines.remove(0).split(" / ");
                        sb.append(i1 +1).append(". ").append(words[0]).append("\n");
                    }
                    if(!words[0].equals("A")) {
                        pane.setText(sb.toString());
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        });*/
    }

    public JTextPane getOutputArea(){
        return this.outputArea;
    }

}
