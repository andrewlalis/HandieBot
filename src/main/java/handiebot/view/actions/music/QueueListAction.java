package handiebot.view.actions.music;

import handiebot.HandieBot;
import sx.blah.discord.handle.obj.IGuild;

import java.awt.event.ActionEvent;

/**
 * @author Andrew Lalis
 */
public class QueueListAction extends MusicAction {

    private boolean showAll = false;

    public QueueListAction(IGuild guild, boolean showAll){
        super(guild);
        this.showAll = showAll;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        HandieBot.musicPlayer.showQueueList(this.guild, this.showAll);
    }
}
