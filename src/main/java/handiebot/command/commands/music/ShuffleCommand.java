package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.Commands;
import handiebot.command.types.ContextCommand;

import java.text.MessageFormat;

import static handiebot.HandieBot.resourceBundle;

/**
 * @author Andrew Lalis
 * Command to set shuffling of the active playlist.
 */
public class ShuffleCommand extends ContextCommand {

    public ShuffleCommand(){
        super("shuffle",
                "[true|false]",
                resourceBundle.getString("commands.command.shuffle.description"),
                0);
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length == 1 && Commands.hasPermission(context, 8)){
            boolean shouldShuffle = Boolean.getBoolean(context.getArgs()[0].toLowerCase());
            HandieBot.musicPlayer.setShuffle(context.getGuild(), shouldShuffle);
        } else {
            context.getChannel().sendMessage(MessageFormat.format(resourceBundle.getString("player.getShuffle"), HandieBot.musicPlayer.isShuffling(context.getGuild())));
        }
    }
}
