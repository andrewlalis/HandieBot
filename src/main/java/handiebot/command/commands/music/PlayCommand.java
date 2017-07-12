package handiebot.command.commands.music;

import com.google.api.services.youtube.model.Video;
import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.ReactionHandler;
import handiebot.command.reactionListeners.YoutubePlayListener;
import handiebot.command.types.ContextCommand;
import handiebot.lavaplayer.playlist.UnloadedTrack;
import handiebot.utils.MessageUtils;
import handiebot.utils.YoutubeSearch;
import sx.blah.discord.handle.obj.IMessage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static handiebot.HandieBot.resourceBundle;
import static handiebot.utils.MessageUtils.sendMessage;
import static handiebot.utils.YoutubeSearch.WATCH_URL;

/**
 * @author Andrew Lalis
 * Command to play a song from the queue or load a new song.
 */
public class PlayCommand extends ContextCommand {

    public PlayCommand() {
        super("play",
                "[URL|QUERY]",
                resourceBundle.getString("commands.command.play.description"),
                0);
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs() == null || context.getArgs().length == 0){
            HandieBot.musicPlayer.playQueue(context.getGuild());
        } else {
            //Check if an actual URL is used, and if not, create a youtube request.
            if (context.getArgs()[0].startsWith("http")) {
                try {
                    HandieBot.musicPlayer.addToQueue(context.getGuild(), new UnloadedTrack(context.getArgs()[0]), context.getUser());
                } catch (Exception e) {
                    sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.play.songAddError"), context.getArgs()[0]), context.getChannel());
                    e.printStackTrace();
                }
            } else {
                //Construct a Youtube song choice.
                List<Video> videos = YoutubeSearch.query(MessageUtils.getTextFromArgs(context.getArgs(), 0));
                if (videos != null) {
                    List<String> urls = new ArrayList<>(videos.size());
                    videos.forEach((video) -> urls.add(WATCH_URL+video.getId()));
                    IMessage message = YoutubeSearch.displayChoicesDialog(videos, context.getChannel());
                    ReactionHandler.addListener(new YoutubePlayListener(message, context.getUser(), urls));
                }
            }
        }
    }

}
