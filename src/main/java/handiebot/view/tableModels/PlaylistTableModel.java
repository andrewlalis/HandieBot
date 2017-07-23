package handiebot.view.tableModels;

import handiebot.lavaplayer.playlist.Playlist;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author Andrew Lalis
 * Class for a model of data to be supplied to a table.
 */
public class PlaylistTableModel extends AbstractTableModel {

    private static final String[] columnNames = {
            "Playlist",
            "Tracks"
    };

    private String[][] data;

    public PlaylistTableModel(){
        List<String> playlistNames = Playlist.getAvailablePlaylists();
        data = new String[playlistNames.size()][columnNames.length];
        for (int i = 0; i < playlistNames.size(); i++){
            data[i][0] = playlistNames.get(i);
            Playlist p = new Playlist(playlistNames.get(i));
            p.load();
            data[i][1] = Integer.toString(p.getTrackCount());
        }
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data[rowIndex][columnIndex] = (String)aValue;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }
}
