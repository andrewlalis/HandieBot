package handiebot.command.commands.music;

import com.google.api.services.youtube.model.Video;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.Commands;
import handiebot.command.ReactionHandler;
import handiebot.command.reactionListeners.YoutubePlayListener;
import handiebot.command.types.ContextCommand;
import handiebot.lavaplayer.playlist.UnloadedTrack;
import handiebot.utils.MessageUtils;
import handiebot.utils.YoutubeSearch;
import handiebot.view.BotLog;
import sx.blah.discord.handle.obj.IMessage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static handiebot.HandieBot.log;
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
                if (context.getArgs()[0].contains("list") && Commands.hasPermission(context, 8)){
                    try {
                        HandieBot.musicPlayer.getPlayerManager().loadItem(context.getArgs()[0], new AudioLoadResultHandler() {
                            @Override
                            public void trackLoaded(AudioTrack track) {
                                //This should not happen.
                                HandieBot.log.log(BotLog.TYPE.ERROR, "Loaded song while attempting to load playlist.");
                            }

                            @Override
                            public void playlistLoaded(AudioPlaylist playlist) {
                                //This is expected to happen.
                                HandieBot.log.log(BotLog.TYPE.MUSIC, "Loading a playlist named: "+playlist.getName()+" with "+playlist.getTracks().size()+" tracks.");
                                MessageUtils.sendMessage("Songs from the playlist **"+playlist.getName()+"** have been added to the queue.", context.getChannel());
                                HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler.clearQueue();
                                for (AudioTrack track : playlist.getTracks()){
                                    HandieBot.log.log(BotLog.TYPE.MUSIC, "Added song from playlist: "+track.getInfo().title);
                                    HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler.getActivePlaylist().addTrack(new UnloadedTrack(track));
                                }
                            }

                            @Override
                            public void noMatches() {
                                //Error that nothing was found.
                                HandieBot.log.log(BotLog.TYPE.ERROR, "No matches while loading playlist.");
                            }

                            @Override
                            public void loadFailed(FriendlyException exception) {
                                //Error that loading failed.
                                HandieBot.log.log(BotLog.TYPE.ERROR, "Loading failed while loading playlist.");
                            }
                        }).get();
                        HandieBot.musicPlayer.playQueue(context.getGuild());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        HandieBot.musicPlayer.addToQueue(context.getGuild(), new UnloadedTrack(context.getArgs()[0]), context.getUser());
                    } catch (Exception e) {
                        sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.play.songAddError"), context.getArgs()[0]), context.getChannel());
                        e.printStackTrace();
                    }
                }
            } else {
                //Construct a Youtube song choice.
                List<Video> videos = YoutubeSearch.query(MessageUtils.getTextFromArgs(context.getArgs(), 0));
                if (videos != null) {
                    List<String> urls = new ArrayList<>(videos.size());
                    videos.forEach((video) -> urls.add(WATCH_URL+video.getId()));
                    IMessage message = YoutubeSearch.displayChoicesDialog(videos, context.getChannel());
                    ReactionHandler.addListener(new YoutubePlayListener(message, context.getUser(), urls));
                } else {
                    log.log(BotLog.TYPE.ERROR, "YouTube query returned a null list of videos.");
                }
            }
        }
    }

}
