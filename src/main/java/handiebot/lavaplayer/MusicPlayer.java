package handiebot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Lalis
 * This class is a container for all the music related functions, and contains methods for easy playback and queue
 * management.
 */
public class MusicPlayer {

    private static String CHANNEL_NAME = "music";

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    public MusicPlayer(){
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerLocalSource(playerManager);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    /**
     * Loads a URL to the queue, or outputs an error message if it fails.
     * @param guild The guild to load the URL to.
     * @param trackURL A string representing a youtube/soundcloud URL.
     */
    public void loadToQueue(IGuild guild, String trackURL){
        GuildMusicManager musicManager = this.getGuildMusicManager(guild);
        this.playerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                addToQueue(guild, musicManager, audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if (audioPlaylist.getTracks().size() > 0){
                    AudioTrack firstTrack = audioPlaylist.getSelectedTrack();
                    if (firstTrack == null){
                        firstTrack = audioPlaylist.getTracks().get(0);
                    }
                    addToQueue(guild, musicManager,firstTrack);
                }
            }

            @Override
            public void noMatches() {
                getMessageChannel(guild).sendMessage("Unable to find a result for: "+trackURL);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                getMessageChannel(guild).sendMessage("Unable to load. "+e.getMessage());
            }
        });
    }

    /**
     * Adds a track to the queue and sends a message to the appropriate channel notifying users.
     * @param guild The guild to queue the track in.
     * @param musicManager The music manager to use.
     * @param track The track to queue.
     */
    public void addToQueue(IGuild guild, GuildMusicManager musicManager, AudioTrack track){
        IVoiceChannel voiceChannel = this.connectToMusicChannel(guild);
        if (voiceChannel != null){
            musicManager.scheduler.queue(track);
            IChannel channel = this.getMessageChannel(guild);
            channel.sendMessage("Added ["+track.getInfo().title+"] to the queue.");
        }

    }

    /**
     * Skips the current track.
     * @param guild The guild to perform the skip on.
     */
    public void skipTrack(IGuild guild){
        this.getGuildMusicManager(guild).scheduler.nextTrack();
        this.getMessageChannel(guild).sendMessage("Skipping the current track.");
    }

    /**
     * Gets or creates a music manager for a specific guild.
     * @param guild The guild to get a manager for.
     * @return A Music Manager for the guild.
     */
    private synchronized GuildMusicManager getGuildMusicManager(IGuild guild){
        long guildId = Long.parseLong(guild.getStringID());
        GuildMusicManager musicManager = this.musicManagers.get(guildId);
        if (musicManager == null){
            musicManager = new GuildMusicManager(this.playerManager);
            musicManagers.put(guildId, musicManager);
        }
        guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());
        return musicManager;
    }

    /**
     * Searches for and attempts to connect to a channel called 'music'.
     * @param guild the guild to get voice channels from.
     * @return The voice channel the bot is now connected to.
     */
    private IVoiceChannel connectToMusicChannel(IGuild guild){
        for (IVoiceChannel voiceChannel : guild.getVoiceChannelsByName(CHANNEL_NAME)){
            if (!voiceChannel.isConnected()) {
                voiceChannel.join();
                return voiceChannel;
            }
        }
        IVoiceChannel voiceChannel = guild.createVoiceChannel(CHANNEL_NAME);
        voiceChannel.join();
        return voiceChannel;
    }

    /**
     * Returns a 'music' message channel where the bot can post info on playing songs, user requests,
     * etc.
     * @param guild The guild to get channels from.
     * @return The channel with that name.
     */
    private IChannel getMessageChannel(IGuild guild){
        List<IChannel> channels = guild.getChannelsByName(CHANNEL_NAME);
        if (channels.size() == 1){
            return channels.get(0);
        }
        return guild.createChannel(CHANNEL_NAME);
    }

}
