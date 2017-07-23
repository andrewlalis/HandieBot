package handiebot.view.tableModels;

import handiebot.lavaplayer.playlist.Playlist;

import javax.swing.table.AbstractTableModel;

/**
 * @author Andrew Lalis
 */
public class SongsTableModel extends AbstractTableModel {

    private static final String[] columnNames = {
            "#",
            "Song",
            "Time"
    };

    private String[][] data;

    public SongsTableModel(Playlist playlist){
        setPlaylist(playlist);
    }

    public SongsTableModel(){
        this.data = new String[0][0];
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
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    public void setPlaylist(Playlist playlist){
        this.data = new String[playlist.getTrackCount()][columnNames.length];
        for (int i = 0; i < playlist.getTrackCount(); i++){
            this.data[i][0] = Integer.toString(i+1);
            this.data[i][1] = playlist.getTracks().get(i).getTitle();
            this.data[i][2] = playlist.getTracks().get(i).getFormattedDuration().replace("[","").replace("]","");
        }
        this.fireTableDataChanged();
    }

}
