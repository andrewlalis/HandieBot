package handiebot.command.types;

import handiebot.command.CommandContext;
import handiebot.command.CommandHandler;
import sx.blah.discord.handle.obj.IGuild;

/**
 * @author Andrew Lalis
 * Type of command which requires a guild to function properly.
 */
public abstract class ContextCommand extends Command {

    public ContextCommand(String name, String usage, String description) {
        super(name, usage, description);
    }

    public abstract void execute(CommandContext context);

    /**
     * Gets the usage of a command, including the guild, so that the prefix is known.
     * @param guild The guild the command should be used on.
     * @return A string representing the usage for this command.
     */
    public String getUsage(IGuild guild){
        return CommandHandler.PREFIXES.get(guild)+this.getUsage();
    }

}
