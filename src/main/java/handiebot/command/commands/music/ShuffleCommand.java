package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;
import handiebot.utils.DisappearingMessage;
import handiebot.view.BotLog;

import static handiebot.HandieBot.log;

/**
 * @author Andrew Lalis
 * Command to set shuffling of the active playlist.
 */
public class ShuffleCommand extends ContextCommand {

    public ShuffleCommand(){
        super("shuffle");
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length == 1){
            boolean shouldShuffle = Boolean.getBoolean(context.getArgs()[0].toLowerCase());
            HandieBot.musicPlayer.setShuffle(context.getGuild(), shouldShuffle);
        } else {
            HandieBot.musicPlayer.toggleShuffle(context.getGuild());
        }
        log.log(BotLog.TYPE.MUSIC, context.getGuild(), "Set shuffle to "+Boolean.toString(HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler.isShuffling()));
        new DisappearingMessage(context.getChannel(), "Set shuffle to "+Boolean.toString(HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler.isShuffling()), 3000);
    }
}
