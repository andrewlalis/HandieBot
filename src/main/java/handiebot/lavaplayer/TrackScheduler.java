package handiebot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Andrew Lalis
 */
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;

    private boolean repeat = false;

    private IGuild guild;

    /**
     * Constructs a new track scheduler with the given player.
     * @param player The audio player this scheduler uses.
     */
    public TrackScheduler(AudioPlayer player, IGuild guild){
        this.player = player;
        this.guild = guild;
        this.queue = new LinkedBlockingQueue<>();
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
        return new ArrayList<>(this.queue);
    }

    /**
     * Add the next track to the queue or play right away if nothing is in the queue.
     * @param track The track to play or add to the queue.
     */
    public void queue(AudioTrack track){
        if (player.getPlayingTrack() == null){
            player.startTrack(track, false);
        } else {
            queue.offer(track);
        }
    }

    /**
     * Starts the next track, stopping the current one if it's playing.
     */
    public void nextTrack(){
        AudioTrack track = queue.poll();
        player.startTrack(track, false);
        if (this.repeat){
            this.queue.add(track);
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        System.out.println("Started audio track: "+track.getInfo().title);
        List<IChannel> channels = this.guild.getChannelsByName(MusicPlayer.CHANNEL_NAME.toLowerCase());
        if (channels.size() > 0){
            channels.get(0).sendMessage("Now playing: **"+track.getInfo().title+"**.");
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        System.out.println("Track ended.");
        if (endReason.mayStartNext){
            nextTrack();
        } else {
            System.out.println(endReason.toString());
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception){
        exception.printStackTrace();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        super.onTrackStuck(player, track, thresholdMs);
    }

}
