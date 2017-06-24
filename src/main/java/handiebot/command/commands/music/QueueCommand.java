package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;

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
        HandieBot.musicPlayer.showQueueList(context.getGuild(), (context.getArgs().length == 1 && context.getArgs()[0].equals("all")));
    }

}
