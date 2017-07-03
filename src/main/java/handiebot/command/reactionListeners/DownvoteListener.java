package handiebot.command.reactionListeners;

import handiebot.HandieBot;
import handiebot.command.ReactionHandler;
import handiebot.command.types.ReactionListener;
import handiebot.view.BotLog;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

import static handiebot.HandieBot.log;

/**
 * @author Andrew Lalis
 * Listen for downvotes on the most recently played song title.
 */
public class DownvoteListener implements ReactionListener {

    private static final String thumbsDown = "\uD83D\uDC4E";

    private final IMessage message;

    public DownvoteListener(IMessage message){
        this.message = message;
    }

    @Override
    public void onReactionEvent(ReactionEvent event) {
        if (event.getReaction().toString().equals(thumbsDown)) {
            IMessage message = event.getMessage();
            //Filter out reactions to previous messages.
            if (message.getLongID() != this.message.getLongID()) {
                return;
            }
            List<IUser> usersHere = HandieBot.musicPlayer.getVoiceChannel(event.getGuild()).getConnectedUsers();
            //Remove the bot from the list of users in the voice channel.
            usersHere.removeIf(user -> (user.getLongID() == HandieBot.client.getOurUser().getLongID()) ||
                    (user.getVoiceStateForGuild(event.getGuild()).isDeafened()) ||
                    (user.getVoiceStateForGuild(event.getGuild()).isSelfDeafened()));

            int userCount = usersHere.size();
            int userDownvotes = 0;
            IReaction reaction = message.getReactionByUnicode(thumbsDown);
            for (IUser user : reaction.getUsers()) {
                if (usersHere.contains(user)) {
                    userDownvotes++;
                }
            }
            if (userDownvotes > (userCount / 2)) {
                log.log(BotLog.TYPE.MUSIC, event.getGuild(), "Users voted to skip the current song.");
                HandieBot.musicPlayer.skipTrack(event.getGuild());
                ReactionHandler.removeListener(this);
            }
        }
    }

}
