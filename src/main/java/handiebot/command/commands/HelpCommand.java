package handiebot.command.commands;

import handiebot.command.CommandContext;
import handiebot.command.Commands;
import handiebot.command.types.Command;
import handiebot.command.types.ContextCommand;
import sx.blah.discord.handle.obj.IPrivateChannel;

/**
 * @author Andrew Lalis
 * Class for sending help/command info to a user if they so desire it.
 */
public class HelpCommand extends ContextCommand {
//TODO: Finish the help class.
    public HelpCommand() {
        super("help",
                "",
                "Displays a list of commands and what they do.");
    }

    @Override
    public void execute(CommandContext context) {
        IPrivateChannel pm = context.getUser().getOrCreatePMChannel();

        StringBuilder sb = new StringBuilder("HandieBot Commands:\n");
        for (Command cmd : Commands.commands){
            StringBuilder commandText = new StringBuilder();
            commandText.append("- `");
            if (cmd instanceof ContextCommand){
                commandText.append(((ContextCommand)cmd).getUsage(context.getGuild()));
            } else {
                commandText.append(cmd.getUsage());
            }
            commandText.append("`\n").append(cmd.getDescription()).append("\n\n");
            if (sb.length() + commandText.length() > 2000){
                pm.sendMessage(sb.toString());
                sb = commandText;
            } else {
                sb.append(commandText);
            }
        }

        pm.sendMessage(sb.toString());
    }
}
