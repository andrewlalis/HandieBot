package handiebot.view;

import handiebot.HandieBot;
import handiebot.command.SelectionController;
import handiebot.lavaplayer.playlist.Playlist;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import static handiebot.HandieBot.resourceBundle;

/**
 * @author Andrew Lalis & Zino Holwerda
 * This class inherits JFrame and simplifies the creation of a window.
 */
public class BotWindow extends JFrame {

    private JTextPane outputArea;

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
        JPanel playlistSub = new JPanel(new BorderLayout());
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        JScrollPane playlist = new JScrollPane(textPane);
        playlist.setPreferredSize(new Dimension(250, 200));
        playlistSub.add(playlist, BorderLayout.PAGE_END);

        //PlaylistList maker
        JList<String> list = setPlayListListArea(textPane);
        JScrollPane playlistList = new JScrollPane(list);
        playlistList.setPreferredSize(new Dimension(250, 1000));
        playlistSub.add(playlistList, BorderLayout.CENTER);
        getContentPane().add(playlistSub, BorderLayout.EAST);

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
        try {
            setIconImage(ImageIO.read(getClass().getClassLoader().getResourceAsStream("avatarIcon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setJMenuBar(new MenuBar(this));
        SelectionController controller = new SelectionController();
        setPreferredSize(new Dimension(800, 600));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }


    private JList<String> setPlayListListArea(JTextPane pane) {
        List<String> playlistList = Playlist.getAvailablePlaylists();
        String labels[] = new String[playlistList.size()];
        int i=0;
        for (String playlist : playlistList) {
            labels[i] = playlist;
            i++;
        }
        JList<String> list = new JList<>(labels);
        list.addListSelectionListener(e -> {
            String name = list.getSelectedValue();
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
        });
        return list;
    }

    public JTextPane getOutputArea(){
        return this.outputArea;
    }

}
