package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;

/**
 * @author Andrew Lalis
 * Command to stop playback of music on a server.
 */
public class StopCommand extends ContextCommand {

    public StopCommand(){
        super("stop",
                "",
                "Stops playing music.");
    }

    @Override
    public void execute(CommandContext context) {
        HandieBot.musicPlayer.stop(context.getGuild());
    }
}
