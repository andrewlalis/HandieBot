package handiebot.command.commands.interfaceActions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static handiebot.HandieBot.resourceBundle;

/**
 * Author: Zino Holwerda
 * Date: 7/14/2017.
 */
public class PlaylistAction extends AbstractAction {

    private static final String DEFAULT_SOURCE_CHOICE_LABEL = "Available Playlists";

    public PlaylistAction() {
        super(resourceBundle.getString("action.menu.playlist"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame editPlaylist = new JFrame(resourceBundle.getString("action.menu.playlist"));
        editPlaylist.setLayout(new BorderLayout(5,5));

        JPanel topSide = new JPanel(new FlowLayout());

        JPanel leftSide = new JPanel(new BorderLayout(5,5));
        JLabel sourceLabel = new JLabel(DEFAULT_SOURCE_CHOICE_LABEL);
        JList<String> playlistList = new JList<>();
        leftSide.add(sourceLabel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(playlistList);
        scrollPane.setPreferredSize(new Dimension(200, 200));
        leftSide.add(scrollPane, BorderLayout.SOUTH);
        topSide.add(leftSide);

        JPanel rightSide = new JPanel(new BorderLayout(5,5));
        JButton addButton = new JButton(resourceBundle.getString("action.menu.playlist.add"));
        rightSide.add(addButton, BorderLayout.NORTH);
        addButton.addActionListener(null);
        JButton deleteButton = new JButton(resourceBundle.getString("action.menu.playlist.delete"));
        rightSide.add(deleteButton, BorderLayout.AFTER_LINE_ENDS);
        deleteButton.addActionListener(null);
        JButton editButton = new JButton(resourceBundle.getString("action.menu.playlist.edit"));
        rightSide.add(editButton, BorderLayout.AFTER_LAST_LINE);
        editButton.addActionListener(null);
        topSide.add(rightSide);
        editPlaylist.add(topSide, BorderLayout.PAGE_START);

        JPanel bottomSide = new JPanel();
        bottomSide.add(new JScrollPane(playlistList));
        editPlaylist.add(bottomSide, BorderLayout.PAGE_END);

        editPlaylist.pack();
        editPlaylist.setLocationRelativeTo(null);
        editPlaylist.setVisible(true);



    }
}
