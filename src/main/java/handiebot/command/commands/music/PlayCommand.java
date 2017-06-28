package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;
import handiebot.lavaplayer.playlist.UnloadedTrack;

import java.text.MessageFormat;

import static handiebot.HandieBot.resourceBundle;

/**
 * @author Andrew Lalis
 * Command to play a song from the queue or load a new song.
 */
public class PlayCommand extends ContextCommand {

    public PlayCommand() {
        super("play",
                "[URL]",
                resourceBundle.getString("commands.command.play.description"),
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
                context.getChannel().sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.play.songAddError"), context.getArgs()[0]));
                e.printStackTrace();
            }
        }
    }

}
