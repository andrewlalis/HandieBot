package handiebot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * @author Andrew Lalis
 */
public class AudioLoadResultHandler implements com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler {

    private TrackScheduler scheduler;

    public AudioLoadResultHandler(TrackScheduler scheduler){
        this.scheduler = scheduler;
    }

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        System.out.println("Adding to queue "+ audioTrack.getInfo().title);
        scheduler.queue(audioTrack);
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        System.out.println("Adding playlist to queue.");
        audioPlaylist.getTracks().forEach(track -> this.scheduler.queue(track));
    }

    @Override
    public void noMatches() {
        System.out.println("No matches!");
    }

    @Override
    public void loadFailed(FriendlyException e) {
        System.out.println("Load failed.");
        e.printStackTrace();
    }
}
