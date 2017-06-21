package handiebot.view.actions.music;

import sx.blah.discord.handle.obj.IGuild;

import java.awt.event.ActionListener;

/**
 * @author Andrew Lalis
 */
public abstract class MusicAction implements ActionListener {

    protected IGuild guild;

    public MusicAction(IGuild guild) {
        this.guild = guild;
    }

}
