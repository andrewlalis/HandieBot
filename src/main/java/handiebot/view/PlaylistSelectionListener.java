package handiebot.view;

import handiebot.lavaplayer.playlist.Playlist;
import handiebot.lavaplayer.playlist.UnloadedTrack;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.List;

/**
 * @author Andrew Lalis
 * Listens for if the user selects a playlist from the list.
 */
public class PlaylistSelectionListener implements ListSelectionListener {

    private DefaultListModel<String> songsListModel;

    public PlaylistSelectionListener(DefaultListModel<String> songsListModel){
        this.songsListModel = songsListModel;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()){
            updatePlaylistData((JList<String>) e.getSource());
        }
    }

    /**
     * Updates the list of songs for a selected playlist.
     * Does not update the list of playlists.
     */
    private void updatePlaylistData(JList<String> playlistNamesList) {
        String selectedValue = playlistNamesList.getSelectedValue();
        if (selectedValue != null && Playlist.playlistExists(selectedValue)){
            Playlist playlist = new Playlist(selectedValue);
            playlist.load();
            List<UnloadedTrack> tracks = playlist.getTracks();
            songsListModel.clear();
            for (int i = 0; i < playlist.getTrackCount(); i++){
                songsListModel.addElement(tracks.get(i).getTitle());
            }
        }
    }

}
