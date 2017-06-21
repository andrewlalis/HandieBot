package handiebot.view.actions.music;

import handiebot.HandieBot;
import sx.blah.discord.handle.obj.IGuild;

import java.awt.event.ActionEvent;

/**
 * @author Andrew Lalis
 */
public class SkipAction extends MusicAction {

    public SkipAction(IGuild guild) {
        super(guild);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        HandieBot.musicPlayer.skipTrack(this.guild);
    }
}
