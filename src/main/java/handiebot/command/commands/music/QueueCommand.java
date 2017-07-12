package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.Commands;
import handiebot.command.types.ContextCommand;
import handiebot.lavaplayer.playlist.Playlist;
import handiebot.view.BotLog;

import java.text.MessageFormat;

import static handiebot.HandieBot.log;
import static handiebot.HandieBot.resourceBundle;
import static handiebot.utils.MessageUtils.sendMessage;

/**
 * @author Andrew Lalis
 * Queue command to display the active queue.
 */
public class QueueCommand extends ContextCommand {

    public QueueCommand() {
        super("queue",
                "[all|clear|save]",
                resourceBundle.getString("commands.command.queue.description.main")+"\n" +
                        "\t`all` - "+resourceBundle.getString("commands.command.queue.description.all")+"\n" +
                        "\t`clear` - "+resourceBundle.getString("commands.command.queue.description.clear")+"\n" +
                        "\t`save <PLAYLIST>` - "+resourceBundle.getString("commands.command.queue.description.save"),
                0);
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length > 0){
            switch (context.getArgs()[0]){
                case ("all"):
                    HandieBot.musicPlayer.showQueueList(context.getGuild(), true);
                    break;
                case ("clear"):
                    if (Commands.hasPermission(context, 8)) {
                        HandieBot.musicPlayer.clearQueue(context.getGuild());
                        log.log(BotLog.TYPE.MUSIC, context.getGuild(), resourceBundle.getString("commands.command.queue.clear"));
                    }
                    break;
                case ("save"):
                    //TODO: add some error messages so users know how to use this.
                    if (context.getArgs().length == 2 && Commands.hasPermission(context, 8)) {
                        Playlist p = HandieBot.musicPlayer.getAllSongsInQueue(context.getGuild());
                        p.setName(context.getArgs()[1]);
                        p.save();
                        sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.queue.save.message"), p.getTrackCount(), p.getName()), context.getChannel());
                        log.log(BotLog.TYPE.INFO, MessageFormat.format(resourceBundle.getString("commands.command.queue.save.log"), p.getName()));
                    }
                    break;
            }
        } else {
            HandieBot.musicPlayer.showQueueList(context.getGuild(), false);
        }
    }

}
