package handiebot.command.reactionListeners;

import handiebot.HandieBot;
import handiebot.command.ReactionHandler;
import handiebot.command.types.ReactionListener;
import handiebot.lavaplayer.playlist.UnloadedTrack;
import handiebot.view.BotLog;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.util.List;

import static handiebot.HandieBot.log;
import static java.lang.Thread.sleep;

/**
 * @author Andrew Lalis
 * Interface for youtube search results choices. A new instance of this listener is created every time a youtube
 * query is shown, and is unique for each user.
 */
public class YoutubeChoiceListener implements ReactionListener {

    private final IMessage message;
    private final IUser user;
    private final List<String> urls;
    private static final long timeout = 30000;//Time until the choice times out and deletes itself.

    private String[] choices = {
            "1⃣",
            "2⃣",
            "3⃣",
            "4⃣",
            "5⃣"
    };

    public YoutubeChoiceListener(IMessage message, IUser user, List<String> urls){
        this.message = message;
        this.user = user;
        this.urls = urls;
        YoutubeChoiceListener instance = this;
        new Thread(() -> {
            try {
                sleep(timeout);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            if (!message.isDeleted()){
                log.log(BotLog.TYPE.MUSIC, message.getGuild(), "Youtube Choice timed out.");
                cleanup();
            }
        }).start();
    }

    @Override
    public void onReactionEvent(ReactionEvent event) {
        if ((event.getMessage().getLongID() == this.message.getLongID()) &&
                (this.user.getLongID() == event.getUser().getLongID())){
            for (int i = 0; i < choices.length; i++){
                if (event.getReaction().toString().equals(choices[i])){
                    try {
                        HandieBot.musicPlayer.addToQueue(message.getGuild(), new UnloadedTrack(urls.get(i)), this.user);
                        log.log(BotLog.TYPE.MUSIC, message.getGuild(), this.user.getName()+" chose item "+(i+1)+" from the Youtube query.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            cleanup();
        }
    }

    private void cleanup(){
        RequestBuffer.request(message::delete);
        ReactionHandler.removeListener(this);
    }
}
