package handiebot.command;

import handiebot.HandieBot;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

/**
 * @author Andrew Lalis
 * Class which handles user reactions to songs and performs necessary actions.
 */
public class ReactionHandler {

    public static final String thumbsUp = "\uD83D\uDC4D";
    public static final String thumbsDown = "\uD83D\uDC4E";

    /**
     * Processes a reaction.
     * @param event The reaction event to process.
     */
    public static void handleReaction(ReactionEvent event){
        IMessage message = event.getMessage();
        IReaction reaction = event.getReaction();
        CommandContext context = new CommandContext(event.getUser(), event.getChannel(), event.getGuild(), new String[]{});
        if (reaction.toString().equals(thumbsDown)){
            onDownvote(context, message);
        }
    }

    /**
     * What to do if someone downvotes a song.
     * If more than half of the people in the voice channel dislike the song, it will be skipped.
     * If not, then the bot will tell how many more people need to downvote.
     * @param context The context of the reaction.
     * @param message The messages that received a reaction.
     */
    private static void onDownvote(CommandContext context, IMessage message){
        List<IUser> usersHere = HandieBot.musicPlayer.getVoiceChannel(context.getGuild()).getConnectedUsers();
        usersHere.removeIf(user -> user.getLongID() == HandieBot.client.getOurUser().getLongID());
        int userCount = usersHere.size();
        int userDownvotes = 0;
        IReaction reaction = message.getReactionByUnicode(thumbsDown);
        for (IUser user : reaction.getUsers()){
            if (usersHere.contains(user)){
                userDownvotes++;
            }
        }
        System.out.println("Valid downvotes: "+userDownvotes+" out of "+userCount+" people present.");
        if (userDownvotes > (userCount/2)){
            HandieBot.musicPlayer.skipTrack(context.getGuild());
        } else if (userDownvotes > 0) {
            context.getChannel().sendMessage((((userCount/2)+1) - userDownvotes)+" more people must downvote before the track is skipped.");
        }
    }

}
