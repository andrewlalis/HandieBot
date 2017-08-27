package handiebot.command.commands.misc;

import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;

/**
 * @author Andrew Lalis
 * Command to fetch rocket league stats and display them in a nice way.
 */
public class RLCommand extends ContextCommand {
//TODO Finish this command, and register it with the list of commands.
    public RLCommand() {
        super("rl",
                "<stats|rank> <steamID> [PLAYLIST]",
                "Get Rocket League stats or specific competitive playlists.",
                0);
    }

    @Override
    public void execute(CommandContext context) {

    }
}
