package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;
import handiebot.utils.DisappearingMessage;
import handiebot.view.BotLog;

import static handiebot.HandieBot.log;

/**
 * @author Andrew Lalis
 * Command to toggle repeating of the active playlist.
 */
public class RepeatCommand extends ContextCommand {

    public RepeatCommand(){
        super("repeat");
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length == 1){
            boolean shouldRepeat = Boolean.getBoolean(context.getArgs()[0].toLowerCase());
            HandieBot.musicPlayer.setRepeat(context.getGuild(), shouldRepeat);
        } else {
            HandieBot.musicPlayer.toggleRepeat(context.getGuild());
        }
        log.log(BotLog.TYPE.MUSIC, context.getGuild(), "Set repeat to "+HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler.isRepeating());
        new DisappearingMessage(context.getChannel(), "Set repeat to "+HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler.isRepeating(), 3000);
    }
}
