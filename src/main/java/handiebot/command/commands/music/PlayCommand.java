package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;
import handiebot.lavaplayer.playlist.UnloadedTrack;
import handiebot.utils.DisappearingMessage;

/**
 * @author Andrew Lalis
 * Command to play a song from the queue or load a new song.
 */
public class PlayCommand extends ContextCommand {

    public PlayCommand() {
        super("play");
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs() == null || context.getArgs().length == 0){
            HandieBot.musicPlayer.playQueue(context.getGuild());
        } else {
            try {
                HandieBot.musicPlayer.addToQueue(context.getGuild(), new UnloadedTrack(context.getArgs()[0]));
            } catch (Exception e) {
                new DisappearingMessage(context.getChannel(), "Unable to queue track: "+context.getArgs()[0], 3000);
                e.printStackTrace();
            }
        }
    }

}
