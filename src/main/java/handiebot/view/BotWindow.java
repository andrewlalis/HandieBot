package handiebot.view;

import handiebot.HandieBot;
import handiebot.lavaplayer.playlist.Playlist;

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
    private JPanel playlistDisplayPanel;

    public BotWindow(){
        super(HandieBot.APPLICATION_NAME);
        //Setup GUI
        //Output area.
        outputArea = new JTextPane();
        outputArea.setBackground(Color.white);
        outputArea.setEditable(false);
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

        updatePlaylistNames();
        this.playlistNamesList.addListSelectionListener(new PlaylistSelectionListener(this.currentPlaylistModel));

        //Create the panel to hold both of the sub-panels.
        playlistDisplayPanel = new JPanel();
        playlistDisplayPanel.setPreferredSize(new Dimension(250, 0));
        playlistDisplayPanel.setLayout(new BorderLayout());

        //Playlist name scroll pane.
        JScrollPane playlistNamesScrollPane = new JScrollPane(playlistNamesList);
        playlistNamesScrollPane.setColumnHeaderView(new JLabel("Playlists"));
        playlistDisplayPanel.add(playlistNamesScrollPane, BorderLayout.PAGE_START);

        //Song names scroll pane.
        JScrollPane songNamesScrollPane = new JScrollPane(this.currentPlaylistList);
        songNamesScrollPane.setColumnHeaderView(new JLabel("Selected Playlist"));
        playlistDisplayPanel.add(songNamesScrollPane, BorderLayout.CENTER);

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
        setPreferredSize(new Dimension(800, 600));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Updates the list of playlist names.
     */
    public void updatePlaylistNames(){
        List<String> playlistNames = Playlist.getAvailablePlaylists();
        this.playlistNamesModel.clear();
        for (String name : playlistNames){
            this.playlistNamesModel.addElement(name);
        }
    }

    /**
     * Sets the playlists panel as visible or invisible.
     */
    public void togglePlaylistsVisibility(){
        this.playlistDisplayPanel.setVisible(!this.playlistDisplayPanel.isVisible());
    }

    public JTextPane getOutputArea(){
        return this.outputArea;
    }

}
