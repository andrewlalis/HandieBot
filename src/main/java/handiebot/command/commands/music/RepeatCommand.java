package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;

/**
 * @author Andrew Lalis
 * Command to toggle repeating of the active playlist.
 */
public class RepeatCommand extends ContextCommand {

    public RepeatCommand(){
        super("repeat",
                "[true|false]",
                "Sets repeating.",
                8);
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length == 1){
            boolean shouldRepeat = Boolean.getBoolean(context.getArgs()[0].toLowerCase());
            HandieBot.musicPlayer.setRepeat(context.getGuild(), shouldRepeat);
        } else {
            HandieBot.musicPlayer.toggleRepeat(context.getGuild());
        }
    }
}
