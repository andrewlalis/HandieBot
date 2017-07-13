package handiebot.view;

import handiebot.HandieBot;
import handiebot.lavaplayer.playlist.Playlist;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.*;

import static handiebot.HandieBot.resourceBundle;

/**
 * @author Andrew Lalis
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
        java.util.List<String> playlists = Playlist.getAvailablePlaylists();
        String labels[] = new String[playlists.size()];
        int i=0;
        for (String playlist : playlists) {
            labels[i] = playlist;
            i++;
        }
        JList<String> list = new JList<>(labels);
        /*list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

            }
        });*/
        JScrollPane jScrollPane = new JScrollPane(list);
        jScrollPane.setPreferredSize(new Dimension(100, 200));
        getContentPane().add(jScrollPane, BorderLayout.EAST);

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
        setJMenuBar(new MenuBar());
        setPreferredSize(new Dimension(800, 600));
        pack();
        setVisible(true);
    }

    //private void selected

    public JTextPane getOutputArea(){
        return this.outputArea;
    }

}
