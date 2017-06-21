package handiebot.view.actions.music;

import handiebot.HandieBot;
import sx.blah.discord.handle.obj.IGuild;

import java.awt.event.ActionEvent;

/**
 * @author Andrew Lalis
 */
public class ToggleRepeatAction extends MusicAction {

    public ToggleRepeatAction(IGuild guild) {
        super(guild);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        HandieBot.musicPlayer.toggleRepeat(this.guild);
    }
}
