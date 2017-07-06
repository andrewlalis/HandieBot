package handiebot.command.reactionListeners;

import handiebot.command.ReactionHandler;
import handiebot.command.types.ReactionListener;
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
public abstract class YoutubeChoiceListener implements ReactionListener {

    protected final IMessage message;
    protected final IUser user;
    protected final List<String> urls;
    protected static final long timeout = 30000;//Time until the choice times out and deletes itself.

    private static final String[] choices = {
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
                    onChoice(i);
                    break;
                }
            }
            cleanup();
        }
    }

    /**
     * Method to delete the large, unwieldy message and remove the listener for this set of videos.
     */
    private void cleanup(){
        RequestBuffer.request(message::delete);
        ReactionHandler.removeListener(this);
    }

    /**
     * What to do when a choice is made.
     * @param choice An integer value from 0 to 4, describing which choice the player has chosen.
     */
    protected abstract void onChoice(int choice);
}
