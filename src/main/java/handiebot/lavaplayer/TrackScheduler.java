package handiebot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import handiebot.view.BotLog;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

import java.util.List;

import static handiebot.HandieBot.log;

/**
 * @author Andrew Lalis
 */
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;

    private Playlist activePlaylist;

    private boolean repeat = false;
    private boolean shuffle = false;

    private IGuild guild;

    /**
     * Constructs a new track scheduler with the given player.
     * @param player The audio player this scheduler uses.
     */
    public TrackScheduler(AudioPlayer player, IGuild guild){
        this.player = player;
        this.guild = guild;
        this.activePlaylist = new Playlist("HandieBot Active Playlist", 283652989212688384L);
        //this.activePlaylist = new Playlist("HandieBot Active Playlist");
    }

    /**
     * Sets whether or not songs get placed back into the queue once they're played.
     * @param value True if the playlist should repeat.
     */
    public void setRepeat(boolean value){
        this.repeat = value;
    }

    /**
     * Returns whether or not repeating is enabled.
     * @return True if repeating, false otherwise.
     */
    public boolean isRepeating(){
        return this.repeat;
    }

    /**
     * Sets whether or not to randomize the next track to be played.
     * @param value True if shuffled should become active.
     */
    public void setShuffle(boolean value){
        this.shuffle = value;
    }

    /**
     * Returns whether or not shuffling is active.
     * @return True if shuffling is active, false otherwise.
     */
    public boolean isShuffling(){
        return this.shuffle;
    }

    /**
     * Returns the time until the bot is done playing sound, at the current rate.
     * @return The milliseconds until music stops.
     */
    public long getTimeUntilDone(){
        long t = 0;
        AudioTrack currentTrack = this.player.getPlayingTrack();
        if (currentTrack != null){
            t += currentTrack.getDuration() - currentTrack.getPosition();
        }
        for (AudioTrack track : this.queueList()){
            t += track.getDuration();
        }
        return t;
    }

    /**
     * Returns a list of tracks in the queue.
     * @return A list of tracks in the queue.
     */
    public List<AudioTrack> queueList(){
        return this.activePlaylist.getTracks();
    }

    /**
     * Add the next track to the queue or play right away if nothing is in the queue.
     * @param track The track to play or add to the queue.
     */
    public void queue(AudioTrack track){
        if (player.getPlayingTrack() == null){
            player.startTrack(track, false);
        } else {
            this.activePlaylist.addTrack(track);
        }
    }

    /**
     * Starts the next track, stopping the current one if it's playing.
     */
    public void nextTrack(){
        AudioTrack track = (this.repeat ? this.activePlaylist.getNextTrackAndRequeue(this.shuffle) : this.activePlaylist.getNextTrackAndRemove(this.shuffle));
        if (track != null) {
            player.startTrack(track, false);
        } else {
            player.stopTrack();
        }
    }

    /**
     * If the user wishes to quit, stop the currently played track.
     */
    public void quit(){
        this.player.stopTrack();
        this.player.destroy();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        log.log(BotLog.TYPE.MUSIC, "Started audio track: "+track.getInfo().title);
        List<IChannel> channels = this.guild.getChannelsByName(MusicPlayer.CHANNEL_NAME.toLowerCase());
        if (channels.size() > 0){
            IMessage message = channels.get(0).sendMessage("Now playing: **"+track.getInfo().title+"**.");
            RequestBuffer.request(() -> {message.addReaction(":thumbsup:");}).get();
            RequestBuffer.request(() -> {message.addReaction(":thumbsdown:");});
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        System.out.println("Track ended.");
        if (endReason.mayStartNext){
            System.out.println("Moving to next track.");
            nextTrack();
        } else {
            log.log(BotLog.TYPE.ERROR, "Unable to go to the next track. Reason: "+endReason.name());
            System.out.println(endReason.toString());
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception){
        exception.printStackTrace();
    }

}
