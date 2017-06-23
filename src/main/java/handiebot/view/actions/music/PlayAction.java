package handiebot.view.actions.music;

import sx.blah.discord.handle.obj.IGuild;

import java.awt.event.ActionEvent;

/**
 * @author Andrew Lalis
 */
public class PlayAction extends MusicAction {

    private String[] args = null;

    public PlayAction(IGuild guild) {
        super(guild);
    }

    public PlayAction(IGuild guild, String[] args){
        super(guild);
        this.args = args;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Play action.");
    }
}
