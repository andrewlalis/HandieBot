package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;
import handiebot.utils.DisappearingMessage;

/**
 * @author Andrew Lalis
 * Queue command to display the active queue.
 */
public class QueueCommand extends ContextCommand {
    public QueueCommand() {
        super("queue");
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length == 1){
            if (context.getArgs()[0].equals("all")){
                HandieBot.musicPlayer.showQueueList(context.getGuild(), true);
            } else if (context.getArgs()[0].equals("clear")){
                HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler.clearQueue();
                new DisappearingMessage(context.getChannel(), "Cleared the queue.", 5000);
            }
        } else {
            HandieBot.musicPlayer.showQueueList(context.getGuild(), false);
        }
    }

}
