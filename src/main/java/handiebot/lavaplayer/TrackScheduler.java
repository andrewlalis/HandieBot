package handiebot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import handiebot.HandieBot;
import handiebot.view.BotLog;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.RequestBuffer;

import java.util.List;

import static handiebot.HandieBot.log;

/**
 * @author Andrew Lalis
 */
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;

    private Playlist activePlaylist;

    private boolean repeat = true;
    private boolean shuffle = false;

    private IGuild guild;

    /**
     * Constructs a new track scheduler with the given player.
     * @param player The audio player this scheduler uses.
     */
    public TrackScheduler(AudioPlayer player, IGuild guild, AudioPlayerManager playerManager){
        this.player = player;
        this.guild = guild;
        //this.activePlaylist = new Playlist("HandieBot Active Playlist", 283652989212688384L);
        this.activePlaylist = new Playlist("HandieBot Active Playlist", playerManager);
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
        AudioTrack currentTrack = this.player.getPlayingTrack();
        if (currentTrack != null){
            this.player.stopTrack();
        }
        AudioTrack track = (this.repeat ? this.activePlaylist.getNextTrackAndRequeue(this.shuffle) : this.activePlaylist.getNextTrackAndRemove(this.shuffle));
        if (track != null) {
            IVoiceChannel voiceChannel = HandieBot.musicPlayer.getVoiceChannel(this.guild);
            if (!voiceChannel.isConnected()){
                voiceChannel.join();
            }
            player.startTrack(track, false);
        } else {
            this.quit();
        }
    }

    /**
     * If the user wishes to quit, stop the currently played track.
     */
    public void quit(){
        IVoiceChannel voiceChannel = HandieBot.musicPlayer.getVoiceChannel(this.guild);
        if (voiceChannel.isConnected()){
            voiceChannel.leave();
        }
        this.player.stopTrack();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        log.log(BotLog.TYPE.MUSIC, this.guild, "Started audio track: "+track.getInfo().title);
        List<IChannel> channels = this.guild.getChannelsByName(MusicPlayer.CHANNEL_NAME.toLowerCase());
        if (channels.size() > 0){
            IMessage message = channels.get(0).sendMessage("Now playing: **"+track.getInfo().title+"**\n"+track.getInfo().uri);
            RequestBuffer.request(() -> {message.addReaction(":thumbsup:");}).get();
            RequestBuffer.request(() -> {message.addReaction(":thumbsdown:");});
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext){
            nextTrack();
        } else {
            log.log(BotLog.TYPE.MUSIC, this.guild, "Unable to go to the next track. Reason: "+endReason.name());
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception){
        exception.printStackTrace();
    }

}
