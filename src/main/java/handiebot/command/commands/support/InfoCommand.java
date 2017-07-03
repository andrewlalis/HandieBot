package handiebot.command.commands.support;

import handiebot.command.CommandContext;
import handiebot.command.commands.music.PlayCommand;
import handiebot.command.commands.music.QueueCommand;
import handiebot.command.types.ContextCommand;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;

import static handiebot.HandieBot.resourceBundle;

/**
 * @author Andrew Lalis
 * Command to display information about the bot, and some common commands.
 */
public class InfoCommand extends ContextCommand {

    public InfoCommand() {
        super("info",
                "",
                resourceBundle.getString("commands.command.info.description"),
                0);
    }

    @Override
    public void execute(CommandContext context) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withColor(new Color(255, 0, 0));
        builder.withDescription(resourceBundle.getString("commands.command.info.embed.description"));
        builder.appendField("`"+new HelpCommand().getUsage(context.getGuild())+"`", resourceBundle.getString("commands.command.info.embed.helpCommand"), false);
        builder.appendField("`"+new PlayCommand().getUsage(context.getGuild())+"`", resourceBundle.getString("commands.command.info.embed.playCommand"), false);
        builder.appendField("`"+new QueueCommand().getUsage(context.getGuild())+"`", resourceBundle.getString("commands.command.info.embed.queueCommand"), false);
        context.getChannel().sendMessage(builder.build());
    }
}
