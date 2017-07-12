package handiebot.command.commands.support;

import handiebot.command.CommandContext;
import handiebot.command.Commands;
import handiebot.command.types.Command;
import handiebot.command.types.ContextCommand;
import sx.blah.discord.handle.obj.IPrivateChannel;

import static handiebot.HandieBot.resourceBundle;
import static handiebot.utils.MessageUtils.sendMessage;

/**
 * @author Andrew Lalis
 * Class for sending help/command info to a user if they so desire it.
 */
public class HelpCommand extends ContextCommand {

    public HelpCommand() {
        super("help",
                "",
                resourceBundle.getString("commands.command.help.description"),
                0);
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
                sendMessage(sb.toString(), pm);
                sb = commandText;
            } else {
                sb.append(commandText);
            }
        }
        sendMessage(sb.toString(), pm);
    }
}
