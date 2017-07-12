package handiebot.command.commands.admin;

import handiebot.command.CommandContext;
import handiebot.command.CommandHandler;
import handiebot.command.types.ContextCommand;
import handiebot.view.BotLog;

import java.text.MessageFormat;

import static handiebot.HandieBot.log;
import static handiebot.HandieBot.resourceBundle;
import static handiebot.utils.MessageUtils.sendMessage;

/**
 * @author Andrew Lalis
 * Command to set the prefix used for a particular server.
 */
public class SetPrefixCommand extends ContextCommand {

    public SetPrefixCommand() {
        super("setprefix",
                "<PREFIX>",
                resourceBundle.getString("commands.command.setPrefix.description"),
                8);
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length == 1) {
            CommandHandler.PREFIXES.put(context.getGuild(), context.getArgs()[0]);
            CommandHandler.saveGuildPrefixes();
            String response = MessageFormat.format(resourceBundle.getString("commands.command.setPrefix.changed"), context.getArgs()[0]);
            sendMessage(response, context.getChannel());
            log.log(BotLog.TYPE.INFO, response);
        } else {
            sendMessage(resourceBundle.getString("commands.command.setPrefix.noPrefixError"), context.getChannel());
        }
    }
}
