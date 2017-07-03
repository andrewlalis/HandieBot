package handiebot.lavaplayer.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import handiebot.HandieBot;
import handiebot.view.BotLog;

import java.util.concurrent.ExecutionException;

import static handiebot.HandieBot.log;

/**
 * @author Andrew Lalis
 * Class for describing a track without the actual audio track.
 * This is useful for quickly loading playlists and only loading a track when it is needed.
 */
public class UnloadedTrack implements Cloneable {

    private String title;
    private String url;
    private long duration;

    /**
     * Constructs a new unloaded track.
     * This assumes that the url is known to be error free, so it will avoid a time consuming validation check.
     * @param title The title of the track.
     * @param url The url of the track, used when loading.
     * @param duration The duration, in milliseconds(ms) of the song.
     */
    public UnloadedTrack(String title, String url, long duration){
        this.title = title;
        this.url = url;
        this.duration = duration;
    }

    /**
     * Constructs a new unloaded track from a given url.
     * Therefore, this method will take time to query youtube/soundcloud to receive a valid audio track.
     * This is meant to ensure that this unloaded track is reliable.
     * @param songURL The url to load from.
     */
    public UnloadedTrack(String songURL) throws Exception {
        this.title = null;
        this.url = null;
        this.duration = 0;
        try {
            HandieBot.musicPlayer.getPlayerManager().loadItem(songURL, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack audioTrack) {
                    title = audioTrack.getInfo().title;
                    url = audioTrack.getInfo().uri;
                    duration = audioTrack.getDuration();
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {
                    log.log(BotLog.TYPE.ERROR, "Attempt to load playlist to create unloaded track.");
                }

                @Override
                public void noMatches() {
                    log.log(BotLog.TYPE.ERROR, "No matches found for " + songURL);
                }

                @Override
                public void loadFailed(FriendlyException e) {
                    log.log(BotLog.TYPE.ERROR, "Loading track failed for " + songURL);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            log.log(BotLog.TYPE.ERROR, "Exception occurred while loading item from URL: "+songURL);
            e.printStackTrace();
        }
        if (this.title == null){
            throw new Exception("Invalid URL: "+songURL);
        }
    }

    /**
     * Constructs a new unloaded track from an already existing audio track.
     * @param track The track to use.
     */
    public UnloadedTrack(AudioTrack track){
        this.title = track.getInfo().title;
        this.url = track.getInfo().uri;
        this.duration = track.getDuration();
    }

    public String getTitle(){
        return this.title;
    }

    public String getURL(){
        return this.url;
    }

    public long getDuration(){
        return this.duration;
    }

    /**
     * Loads the real audio track from the internet, and returns it.
     * @return an AudioTrack representing this track.
     */
    public AudioTrack loadAudioTrack(){
        final AudioTrack[] track = {null};
        try {
            HandieBot.musicPlayer.getPlayerManager().loadItem(this.url, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack audioTrack) {
                    track[0] = audioTrack;
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {
                    log.log(BotLog.TYPE.ERROR, "Attempt to load playlist to create unloaded track.");
                }

                @Override
                public void noMatches() {
                    log.log(BotLog.TYPE.ERROR, "No matches found for " + url);
                }

                @Override
                public void loadFailed(FriendlyException e) {
                    log.log(BotLog.TYPE.ERROR, "Loading track failed for " + url);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            log.log(BotLog.TYPE.ERROR, "Exception occurred while loading item from URL: "+url);
            e.printStackTrace();
        }
        return track[0];
    }

    /**
     * Returns the duration of the track in an aesthetically pleasing way.
     * Format is as follows: [mm:ss]
     * @return A string representation of the duration of a track.
     */
    public String getFormattedDuration(){
        int seconds = (int) (this.duration / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("[%d:%02d]", minutes, seconds);
    }

    @Override
    public String toString(){
        return this.title + " / " + this.url + " / " + Long.toString(this.duration);
    }

    /**
     * Creates a clone of this track.
     * @return A clone of this track.
     */
    public UnloadedTrack clone(){
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new UnloadedTrack(this.title, this.url, this.duration);
    }

}
