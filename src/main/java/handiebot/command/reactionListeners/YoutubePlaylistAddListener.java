package handiebot.command.reactionListeners;

import handiebot.lavaplayer.playlist.Playlist;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

import static handiebot.utils.MessageUtils.sendMessage;

/**
 * @author Andrew Lalis
 * Specific Listener for adding songs to a playlist that must be saved.
 */
public class YoutubePlaylistAddListener extends YoutubeChoiceListener {
    //TODO: externalize strings
    private Playlist playlist;

    public YoutubePlaylistAddListener(IMessage message, IUser user, List<String> urls, Playlist playlist) {
        super(message, user, urls);
        this.playlist = playlist;
    }

    @Override
    protected void onChoice(int choice) {
        this.playlist.loadTrack(this.urls.get(choice));
        this.playlist.save();
        sendMessage("Added song to *"+this.playlist.getName()+"*.", message.getChannel());
    }
}
