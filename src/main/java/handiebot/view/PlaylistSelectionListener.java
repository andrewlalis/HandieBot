package handiebot.view;

import handiebot.lavaplayer.playlist.Playlist;
import handiebot.view.tableModels.SongsTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Andrew Lalis
 * Listens for if the user selects a playlist from the list.
 */
public class PlaylistSelectionListener implements ListSelectionListener {

    //The table model for the songs list.
    private SongsTableModel songsModel;

    //The table that shows the playlist names.
    private JTable table;
    private JTable songsTable;

    public PlaylistSelectionListener(SongsTableModel songsModel, JTable table, JTable songsTable){
        this.songsModel = songsModel;
        this.table = table;
        this.songsTable = songsTable;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()){
            updatePlaylistData();
            BotWindow.autoSizeTable(songsTable);
        }
    }

    /**
     * Updates the list of songs for a selected playlist.
     * Does not update the list of playlists.
     */
    private void updatePlaylistData() {
        String selectedValue = (String) this.table.getModel().getValueAt(this.table.getSelectedRow(), 0);
        if (selectedValue != null && Playlist.playlistExists(selectedValue)){
            Playlist playlist = new Playlist(selectedValue);
            playlist.load();
            this.songsModel.setPlaylist(playlist);
        }
    }

}
