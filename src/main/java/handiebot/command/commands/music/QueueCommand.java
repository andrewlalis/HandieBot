package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.Commands;
import handiebot.command.types.ContextCommand;
import handiebot.lavaplayer.playlist.Playlist;
import handiebot.lavaplayer.playlist.UnloadedTrack;
import handiebot.view.BotLog;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                "[all|clear|save|remove|move]",
                resourceBundle.getString("commands.command.queue.description.main")+"\n" +
                        "\t`all` - "+resourceBundle.getString("commands.command.queue.description.all")+"\n" +
                        "\t`clear` - "+resourceBundle.getString("commands.command.queue.description.clear")+"\n" +
                        "\t`save <PLAYLIST>` - "+resourceBundle.getString("commands.command.queue.description.save")+"\n"+
                        "\t`remove <INDEX| INDEX2...>` - "+resourceBundle.getString("commands.command.queue.description.remove")+"\n"+
                        "\t`move <INDEX> <INDEX> - "+resourceBundle.getString("commands.command.queue.description.move"),
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
                    if (context.getArgs().length == 2 && Commands.hasPermission(context, 8)) {
                        Playlist p = HandieBot.musicPlayer.getAllSongsInQueue(context.getGuild());
                        p.setName(context.getArgs()[1]);
                        p.save();
                        sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.queue.save.message"), p.getTrackCount(), p.getName()), context.getChannel());
                        log.log(BotLog.TYPE.INFO, MessageFormat.format(resourceBundle.getString("commands.command.queue.save.log"), p.getName()));
                    } else {
                        sendMessage(resourceBundle.getString("commands.command.queue.error.save"), context.getChannel());
                    }
                    break;
                case ("remove"):
                    if (context.getArgs().length > 1 && Commands.hasPermission(context, 8)){
                        List<Integer> songsToRemove = new ArrayList<>();
                        for (int i = 1; i < context.getArgs().length; i++){
                            songsToRemove.add(Integer.parseInt(context.getArgs()[i]));
                        }
                        songsToRemove.sort(Collections.reverseOrder());
                        for (Integer i : songsToRemove){
                            HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler.remove(i-1);
                        }
                        sendMessage(resourceBundle.getString("commands.command.queue.remove.message"), context.getChannel());
                        log.log(BotLog.TYPE.MUSIC, context.getGuild(), resourceBundle.getString("commands.command.queue.remove.message"));
                    } else {
                        sendMessage(resourceBundle.getString("commands.command.queue.remove.error"), context.getChannel());
                    }
                case ("move"):
                    if (context.getArgs().length == 3 && Commands.hasPermission(context, 8)){
                        int startIndex = Integer.parseInt(context.getArgs()[1]);
                        int newIndex = Integer.parseInt(context.getArgs()[2]);
                        Playlist playlist = HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler.getActivePlaylist();
                        int trackCount = playlist.getTrackCount();
                        //Test if the indices are valid, and if not, show an error message.
                        if ((startIndex > 0) && (startIndex <= trackCount) && (newIndex > 0) && (newIndex <= trackCount)){
                            List<UnloadedTrack> tracks = playlist.getTracks();
                            UnloadedTrack track = tracks.remove(startIndex-1);
                            tracks.add(newIndex-1, track);
                            sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.queue.move.success"), track.getTitle(), startIndex, newIndex), context.getChannel());
                        } else {
                            sendMessage(resourceBundle.getString("commands.command.queue.move.indexError"), context.getChannel());
                        }
                    } else {
                        sendMessage(resourceBundle.getString("commands.command.queue.move.error"), context.getChannel());
                    }
            }
        } else {
            HandieBot.musicPlayer.showQueueList(context.getGuild(), false);
        }
    }

}
