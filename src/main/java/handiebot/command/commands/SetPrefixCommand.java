package handiebot.command.commands;

import handiebot.command.CommandContext;
import handiebot.command.CommandHandler;
import handiebot.command.types.ContextCommand;
import handiebot.utils.DisappearingMessage;
import handiebot.view.BotLog;

import static handiebot.HandieBot.log;

/**
 * @author Andrew Lalis
 * Command to set the prefix used for a particular server.
 */
public class SetPrefixCommand extends ContextCommand {

    public SetPrefixCommand() {
        super("setprefix",
                "<PREFIX>",
                "Sets the prefix for commands.");
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length == 1) {
            CommandHandler.PREFIXES.put(context.getGuild(), context.getArgs()[0]);
            CommandHandler.saveGuildPrefixes();
            new DisappearingMessage(context.getChannel(), "Changed command prefix to \""+context.getArgs()[0]+"\"", 6000);
            log.log(BotLog.TYPE.INFO, "Changed command prefix to \""+context.getArgs()[0]+"\"");
        } else {
            new DisappearingMessage(context.getChannel(), "You must provide a new prefix.", 3000);
        }
    }
}
