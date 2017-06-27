package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;
import handiebot.lavaplayer.playlist.UnloadedTrack;

/**
 * @author Andrew Lalis
 * Command to play a song from the queue or load a new song.
 */
public class PlayCommand extends ContextCommand {

    public PlayCommand() {
        super("play",
                "[URL]",
                "Plays a song, or adds it to the queue.",
                0);
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs() == null || context.getArgs().length == 0){
            HandieBot.musicPlayer.playQueue(context.getGuild());
        } else {
            try {
                HandieBot.musicPlayer.addToQueue(context.getGuild(), new UnloadedTrack(context.getArgs()[0]));
            } catch (Exception e) {
                context.getChannel().sendMessage("Unable to add song to queue: "+context.getArgs()[0]+".");
                e.printStackTrace();
            }
        }
    }

}
