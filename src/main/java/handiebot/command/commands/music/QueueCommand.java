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
        super("queue",
                "[all|clear]",
                "Shows the first 10 songs in the queue.\n" +
                        "\tall - Shows all songs.\n" +
                        "\tclear - Clears the queue and stops playing.");
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length == 1){
            if (context.getArgs()[0].equals("all")){
                HandieBot.musicPlayer.showQueueList(context.getGuild(), true);
            } else if (context.getArgs()[0].equals("clear")){
                HandieBot.musicPlayer.clearQueue(context.getGuild());
            }
        } else {
            HandieBot.musicPlayer.showQueueList(context.getGuild(), false);
        }
    }

}
