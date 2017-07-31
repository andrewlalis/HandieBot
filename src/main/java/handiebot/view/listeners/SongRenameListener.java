package handiebot.view.listeners;

import handiebot.lavaplayer.playlist.Playlist;
import handiebot.utils.TableColumnAdjuster;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Andrew Lalis
 * Listener to check if the user has double-clicked on a song's name, and give them an option to rename that song.
 */
public class SongRenameListener implements MouseListener {
//TODO: Externalize strings.
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2){
            JTable table = (JTable) e.getSource();
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            if (col == 1){
                String oldValue = (String) table.getModel().getValueAt(row, col);
                String result = JOptionPane.showInputDialog(table, "Enter a modified name for the song.", oldValue);
                if (result != null){
                    table.getModel().setValueAt(result, row, col);
                    new TableColumnAdjuster(table).adjustColumns();
                    Playlist playlist = new Playlist(table.getName());
                    playlist.load();
                    playlist.getTracks().get(row).setTitle(result);
                    playlist.save();
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
