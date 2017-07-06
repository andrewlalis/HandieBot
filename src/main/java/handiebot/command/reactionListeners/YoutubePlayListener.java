package handiebot.command.reactionListeners;

import handiebot.HandieBot;
import handiebot.lavaplayer.playlist.UnloadedTrack;
import handiebot.view.BotLog;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

import static handiebot.HandieBot.log;

/**
 * @author Andrew Lalis
 * Specific Listener for choices in the Play command, where songs chosen are added to the active queue.
 */
public class YoutubePlayListener extends YoutubeChoiceListener {

    public YoutubePlayListener(IMessage message, IUser user, List<String> urls) {
        super(message, user, urls);
    }

    @Override
    protected void onChoice(int choice) {
        try {
            HandieBot.musicPlayer.addToQueue(message.getGuild(), new UnloadedTrack(urls.get(choice)), this.user);
            log.log(BotLog.TYPE.MUSIC, message.getGuild(), this.user.getName()+" chose item "+(choice+1)+" from the Youtube query.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
