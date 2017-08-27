package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.Commands;
import handiebot.command.types.ContextCommand;
import handiebot.utils.FileUtil;

import java.text.MessageFormat;

import static handiebot.HandieBot.resourceBundle;
import static handiebot.utils.MessageUtils.sendMessage;

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
            boolean shouldShuffle = (context.getArgs()[0].equalsIgnoreCase("true"));
            HandieBot.musicPlayer.setShuffle(context.getGuild(), shouldShuffle);
            //Save the settings for this guild to the settings file.
            HandieBot.settings.setProperty(context.getGuild().getName()+"_shuffle", Boolean.toString(shouldShuffle));
            FileUtil.saveSettings();
        } else {
            sendMessage(MessageFormat.format(resourceBundle.getString("player.getShuffle"), HandieBot.musicPlayer.isShuffling(context.getGuild())), context.getChannel());
        }
    }
}
