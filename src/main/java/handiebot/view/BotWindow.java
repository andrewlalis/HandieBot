package handiebot.view;

import handiebot.HandieBot;
import handiebot.view.tableModels.PlaylistTableModel;
import handiebot.view.tableModels.SongsTableModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import static handiebot.HandieBot.resourceBundle;

/**
 * @author Andrew Lalis & Zino Holwerda
 * This class inherits JFrame and simplifies the creation of a window.
 */
public class BotWindow extends JFrame {

    //Console output panel.
    private JTextPane outputArea;

    //Playlist display variables.
    private PlaylistTableModel playlistTableModel;
    private SongsTableModel songsTableModel;
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

        //updatePlaylistNames();
        playlistDisplayPanel = new JPanel();
        playlistDisplayPanel.setPreferredSize(new Dimension(250, 0));
        playlistDisplayPanel.setLayout(new BorderLayout());

        this.songsTableModel = new SongsTableModel();
        this.playlistTableModel = new PlaylistTableModel();

        JTable songsTable = new JTable(this.songsTableModel);
        JTable playlistTable = new JTable(playlistTableModel);

        //Playlist name scroll pane.
        playlistTable.setRowSelectionAllowed(true);
        playlistTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playlistTable.getSelectionModel().addListSelectionListener(new PlaylistSelectionListener(this.songsTableModel, playlistTable, songsTable));
        JScrollPane playlistNamesScrollPane = new JScrollPane(playlistTable);
        playlistNamesScrollPane.setPreferredSize(new Dimension(250, 200));
        playlistDisplayPanel.add(playlistNamesScrollPane, BorderLayout.PAGE_START);

        //Song names scroll pane.
        songsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        songsTable.setRowSelectionAllowed(true);
        songsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane songNamesScrollPane = new JScrollPane(songsTable);
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
        this.playlistTableModel = new PlaylistTableModel();
    }

    /**
     * Sets the playlists panel as visible or invisible.
     */
    public void togglePlaylistsVisibility(){
        this.playlistDisplayPanel.setVisible(!this.playlistDisplayPanel.isVisible());
    }

    /**
     * Automatically resizes a table to shrink the index column.
     * @param table The table to resize.
     */
    public static void autoSizeTable(JTable table){
        final TableColumnModel columnModel = table.getColumnModel();
        int freeSpace = 230;
        for (int col = 0; col < columnModel.getColumnCount(); col++) {
            int width = 0;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, col);
                Component component = table.prepareRenderer(renderer, row, col);
                width = Math.max(component.getPreferredSize().width + 1, width);
            }
            columnModel.getColumn(col).setPreferredWidth(width);
            freeSpace -= width;
        }
    }

    public JTextPane getOutputArea(){
        return this.outputArea;
    }

}
