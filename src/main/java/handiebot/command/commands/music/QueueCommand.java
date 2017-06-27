package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;
import handiebot.lavaplayer.playlist.Playlist;
import handiebot.view.BotLog;

import static handiebot.HandieBot.log;

/**
 * @author Andrew Lalis
 * Queue command to display the active queue.
 */
public class QueueCommand extends ContextCommand {
    //TODO: Add specific permissions per argument.
    public QueueCommand() {
        super("queue",
                "[all|clear|save]",
                "Shows the first 10 songs in the queue.\n" +
                        "\t`all` - Shows all songs.\n" +
                        "\t`clear` - Clears the queue and stops playing.\n" +
                        "\t`save <PLAYLIST>` - Saves the queue to a playlist.",
                0);
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length > 0){
            if (context.getArgs()[0].equals("all")){
                HandieBot.musicPlayer.showQueueList(context.getGuild(), true);
            } else if (context.getArgs()[0].equals("clear")){
                HandieBot.musicPlayer.clearQueue(context.getGuild());
                log.log(BotLog.TYPE.MUSIC, context.getGuild(), "Cleared queue.");
            } else if (context.getArgs()[0].equals("save") && context.getArgs().length == 2){
                Playlist p = HandieBot.musicPlayer.getAllSongsInQueue(context.getGuild());
                p.setName(context.getArgs()[1]);
                p.save();
                context.getChannel().sendMessage("Saved "+p.getTrackCount()+" tracks to playlist **"+p.getName()+"**.");
                log.log(BotLog.TYPE.INFO, "Saved queue to playlist ["+p.getName()+"].");
            }
        } else {
            HandieBot.musicPlayer.showQueueList(context.getGuild(), false);
        }
    }

}
