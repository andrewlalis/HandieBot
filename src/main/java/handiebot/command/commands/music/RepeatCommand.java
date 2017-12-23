package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.Commands;
import handiebot.command.types.ContextCommand;
import handiebot.utils.FileUtil;

import java.text.MessageFormat;

import static handiebot.HandieBot.resourceBundle;
import static handiebot.HandieBot.settings;
import static handiebot.utils.MessageUtils.sendMessage;

/**
 * @author Andrew Lalis
 * Command to toggle repeating of the active playlist.
 */
public class RepeatCommand extends ContextCommand {

    public RepeatCommand(){
        super("repeat",
                "[true|false]",
                resourceBundle.getString("commands.command.repeat.description"),
                0);
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length == 1 && Commands.hasPermission(context, 8)){
            boolean shouldRepeat = (context.getArgs()[0].toLowerCase().equals("true"));
            HandieBot.musicPlayer.setRepeat(context.getGuild(), shouldRepeat);
            HandieBot.settings.setProperty(context.getGuild().getName()+"_repeat", Boolean.toString(shouldRepeat));
            FileUtil.saveSettings(settings);
        } else {
            sendMessage(MessageFormat.format(resourceBundle.getString("player.getRepeat"), HandieBot.musicPlayer.isRepeating(context.getGuild())), context.getChannel());
        }
    }
}
