package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;

import static handiebot.HandieBot.resourceBundle;

/**
 * @author Andrew Lalis
 * Command to stop playback of music on a server.
 */
public class StopCommand extends ContextCommand {

    public StopCommand(){
        super("stop",
                "",
                resourceBundle.getString("commands.command.stop.description"),
                8);
    }

    @Override
    public void execute(CommandContext context) {
        HandieBot.musicPlayer.stop(context.getGuild());
    }
}
