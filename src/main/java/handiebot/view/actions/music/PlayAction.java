package handiebot.view.actions.music;

import handiebot.HandieBot;
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
        if (this.args == null || this.args.length < 1){
            HandieBot.musicPlayer.playQueue(this.guild);
        } else {
            HandieBot.musicPlayer.loadToQueue(this.guild, this.args[0]);
        }
    }
}
