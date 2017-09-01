package handiebot.command.commands.music;

import com.google.api.services.youtube.model.Video;
import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.ReactionHandler;
import handiebot.command.reactionListeners.YoutubeChoiceListener;
import handiebot.command.types.ContextCommand;
import handiebot.lavaplayer.TrackScheduler;
import handiebot.lavaplayer.playlist.UnloadedTrack;
import handiebot.utils.MessageUtils;
import handiebot.utils.YoutubeSearch;
import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;
import java.util.List;

import static handiebot.HandieBot.resourceBundle;
import static handiebot.utils.MessageUtils.sendMessage;
import static handiebot.utils.YoutubeSearch.WATCH_URL;

/**
 * @author Andrew Lalis
 * Command to make a song play immediately.
 */
public class PlayNowCommand extends ContextCommand {
//TODO: Externalize strings.
    public PlayNowCommand() {
        super("playnow",
                "<URL,QUERY>",
                resourceBundle.getString("commands.command.playnow.description"),
                8);
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length < 1){
            sendMessage("You must provide a URL or series of words to search.", context.getChannel());
        }
        if (context.getArgs()[0].startsWith("http")) {
            //The user has given only a URL.
            try {
                playTrackNow(new UnloadedTrack(context.getArgs()[0]), context);
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage("Unable to load the song.", context.getChannel());
            }
        } else {
            //The user has given a search query.
            List<Video> videos = YoutubeSearch.query(MessageUtils.getTextFromArgs(context.getArgs(), 0));
            if (videos != null) {
                List<String> urls = new ArrayList<>(videos.size());
                videos.forEach((video) -> urls.add(WATCH_URL+video.getId()));
                IMessage message = YoutubeSearch.displayChoicesDialog(videos, context.getChannel());
                ReactionHandler.addListener(new YoutubeChoiceListener(message, context.getUser(), urls) {
                    @Override
                    protected void onChoice(int choice) {
                        try {
                            playTrackNow(new UnloadedTrack(urls.get(choice)), context);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    /**
     * Plays an unloaded track immediately.
     * @param track The unloaded track.
     * @param context The context of the action.
     */
    private void playTrackNow(UnloadedTrack track, CommandContext context){
        TrackScheduler scheduler = HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler;
        scheduler.getActivePlaylist().getTracks().add(0, track);
        scheduler.nextTrack();
    }
}
