package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;

/**
 * @author Andrew Lalis
 * Command to set shuffling of the active playlist.
 */
public class ShuffleCommand extends ContextCommand {

    public ShuffleCommand(){
        super("shuffle",
                "[true|false]",
                "Sets shuffling.");
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length == 1){
            boolean shouldShuffle = Boolean.getBoolean(context.getArgs()[0].toLowerCase());
            HandieBot.musicPlayer.setShuffle(context.getGuild(), shouldShuffle);
        } else {
            HandieBot.musicPlayer.toggleShuffle(context.getGuild());
        }
    }
}