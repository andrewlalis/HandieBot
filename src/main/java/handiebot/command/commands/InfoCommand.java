package handiebot.command.commands;

import handiebot.command.CommandContext;
import handiebot.command.commands.music.PlayCommand;
import handiebot.command.commands.music.QueueCommand;
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
        super("info",
                "",
                "Displays some common commands and information about the bot.");
    }

    @Override
    public void execute(CommandContext context) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withColor(new Color(255, 0, 0));
        builder.withDescription("HandieBot is a Discord bot created by Andrew Lalis. It can play music, manage playlists, and provide other assistance to users. Some useful commands are shown below.");
        builder.appendField("`"+new HelpCommand().getUsage(context.getGuild())+"`", "Receive a message with a detailed list of all commands and how to use them.", false);
        builder.appendField("`"+new PlayCommand().getUsage(context.getGuild())+"`", "Play a song, or add it to the queue if one is already playing. A URL can be a YouTube or SoundCloud link.", false);
        builder.appendField("`"+new QueueCommand().getUsage(context.getGuild())+"`", "Show a list of songs that will soon be played.", false);
        DisappearingMessage.deleteMessageAfter(10000, context.getChannel().sendMessage(builder.build()));
    }
}
