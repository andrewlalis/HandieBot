package handiebot.command.types;

import handiebot.command.CommandContext;

/**
 * @author Andrew Lalis
 * Type of command which requires a guild to function properly.
 */
public abstract class ContextCommand extends Command {

    public ContextCommand(String s) {
        super(s);
    }

    public abstract void execute(CommandContext context);

}
