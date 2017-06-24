package handiebot.command.commands;

import handiebot.command.CommandContext;
import handiebot.command.CommandHandler;
import handiebot.command.types.ContextCommand;
import handiebot.utils.DisappearingMessage;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;

/**
 * @author Andrew Lalis
 * Command to display information about the bot, and some common commands.
 */
public class InfoCommand extends ContextCommand {

    public InfoCommand() {
        super("info");
    }

    @Override
    public void execute(CommandContext context) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withColor(new Color(255, 0, 0));
        builder.withDescription("HandieBot is a Discord bot created by Andrew Lalis. It can play music, manage playlists, and provide other assistance to users. Some useful commands are shown below.");
        builder.appendField("`"+ CommandHandler.PREFIXES.get(context.getGuild())+"help`", "Receive a message with a detailed list of all commands and how to use them.", false);
        builder.appendField("`"+CommandHandler.PREFIXES.get(context.getGuild())+"setprefix`", "Changed the prefix used at the beginning of each command.", false);
        DisappearingMessage.deleteMessageAfter(10000, context.getChannel().sendMessage(builder.build()));
    }
}
