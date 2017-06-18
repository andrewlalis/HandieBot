package handiebot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Andrew Lalis
 */
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;

    /**
     * Constructs a new track scheduler with the given player.
     * @param player The audio player this scheduler uses.
     */
    public TrackScheduler(AudioPlayer player){
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Add the next track to the queue or play right away if nothing is in the queue.
     * @param track The track to play or add to the queue.
     */
    public void queue(AudioTrack track){
        if (!player.startTrack(track, true)){
            System.out.println("Unable to start track immediately, adding to queue.");
            queue.offer(track);
        }
    }

    /**
     * Starts the next track, stopping the current one if it's playing.
     */
    public void nextTrack(){
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
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
