package handiebot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import handiebot.command.CommandHandler;
import handiebot.utils.DisappearingMessage;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Andrew Lalis
 * This class is a container for all the music related functions, and contains methods for easy playback and queue
 * management.
 */
public class MusicPlayer {

    //Name for the message and voice channels dedicated to this bot.
    public static String CHANNEL_NAME = "HandieBotMusic";

    private final AudioPlayerManager playerManager;

    /*
    Mappings of music managers, channels and voice channels for each guild.
     */
    private Map<IGuild, GuildMusicManager> musicManagers;
    private Map<IGuild, IChannel> chatChannels;
    private Map<IGuild, IVoiceChannel> voiceChannels;

    public MusicPlayer(){
        //Initialize player manager.
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerLocalSource(playerManager);
        AudioSourceManagers.registerRemoteSources(playerManager);

        //Initialize all maps.
        this.musicManagers = new HashMap<>();
        this.chatChannels = new HashMap<>();
        this.voiceChannels = new HashMap<>();
    }

    /**
     * Gets the music manager specific to a particular guild.
     * @param guild The guild to get the music manager for.
     * @return The music manager for a guild.
     */
    private GuildMusicManager getMusicManager(IGuild guild){
        if (!this.musicManagers.containsKey(guild)){
            System.out.println("Registering guild, creating audio provider.");
            this.musicManagers.put(guild, new GuildMusicManager(this.playerManager, guild));
            guild.getAudioManager().setAudioProvider(this.musicManagers.get(guild).getAudioProvider());
        }
        return this.musicManagers.get(guild);
    }

    /**
     * Gets the chat channel specific to a particular guild. This channel is used send updates about playback and
     * responses to people's commands. If none exists, the bot will attempt to make a channel.
     * @param guild The guild to get the channel from.
     * @return A message channel on a particular guild that is specifically for music.
     */
    private IChannel getChatChannel(IGuild guild){
        if (!this.chatChannels.containsKey(guild)){
            List<IChannel> channels = guild.getChannelsByName(CHANNEL_NAME.toLowerCase());
            if (channels.isEmpty()){
                System.out.println("Found "+channels.size()+" matches for message channel, creating new one.");
                this.chatChannels.put(guild, guild.createChannel(CHANNEL_NAME.toLowerCase()));
            } else {
                this.chatChannels.put(guild, channels.get(0));
            }
        }
        return this.chatChannels.get(guild);
    }

    /**
     * Gets the voice channel associated with a particular guild. This channel is used for audio playback. If none
     * exists, the bot will attempt to make a voice channel.
     * @param guild The guild to get the channel from.
     * @return The voice channel on a guild that is for this bot.
     */
    private IVoiceChannel getVoiceChannel(IGuild guild){
        if (!this.voiceChannels.containsKey(guild)){
            List<IVoiceChannel> channels = guild.getVoiceChannelsByName(CHANNEL_NAME);
            if (channels.isEmpty()){
                System.out.println("Found "+channels.size()+" matches for voice channel, creating new one.");
                this.voiceChannels.put(guild, guild.createVoiceChannel(CHANNEL_NAME));
            } else {
                this.voiceChannels.put(guild, channels.get(0));
            }
        }
        return this.voiceChannels.get(guild);
    }

    /**
     * Sends a formatted message to the guild about the first few items in a queue.
     */
    public void showQueueList(IGuild guild){
        List<AudioTrack> tracks = getMusicManager(guild).scheduler.queueList();
        if (tracks.size() == 0) {
            new DisappearingMessage(getChatChannel(guild), "The queue is empty. Use **"+ CommandHandler.PREFIX+"play** *URL* to add songs.", 3000);
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withColor(255, 0, 0);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < (tracks.size() <= 10 ? tracks.size() : 10); i++) {
                sb.append(i+1);
                sb.append(". ");
                sb.append('[');
                sb.append(tracks.get(i).getInfo().title);
                sb.append("](");
                sb.append(tracks.get(i).getInfo().uri);
                sb.append(") [");
                int seconds = (int) (tracks.get(i).getInfo().length/1000);
                int minutes = seconds / 60;
                sb.append(minutes);
                sb.append(":");
                sb.append(seconds % 60);
                sb.append("]\n");
            }
            builder.withTimestamp(System.currentTimeMillis());
            builder.appendField("Showing " + (tracks.size() <= 10 ? tracks.size() : "the first 10") + " track"+(tracks.size() > 1 ? "s" : "")+".", sb.toString(), false);
            IMessage message = getChatChannel(guild).sendMessage(builder.build());
            DisappearingMessage.deleteMessageAfter(6000, message);
        }
    }

    /**
     * Loads a URL to the queue, or outputs an error message if it fails.
     * @param trackURL A string representing a youtube/soundcloud URL.
     */
    public void loadToQueue(IGuild guild, String trackURL){
        this.playerManager.loadItemOrdered(getMusicManager(guild), trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                System.out.println("Track successfully loaded: "+audioTrack.getInfo().title);
                addToQueue(guild, audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if (audioPlaylist.getTracks().size() > 0){
                    AudioTrack firstTrack = audioPlaylist.getSelectedTrack();
                    if (firstTrack == null){
                        firstTrack = audioPlaylist.getTracks().get(0);
                    }
                    addToQueue(guild, firstTrack);
                }
            }

            @Override
            public void noMatches() {
                new DisappearingMessage(getChatChannel(guild), "Unable to find a result for: "+trackURL, 3000);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                new DisappearingMessage(getChatChannel(guild), "Unable to load. "+e.getMessage(), 3000);
            }
        });
    }

    /**
     * Adds a track to the queue and sends a message to the appropriate channel notifying users.
     * @param track The track to queue.
     */
    public void addToQueue(IGuild guild, AudioTrack track){
        IVoiceChannel voiceChannel = getVoiceChannel(guild);
        if (voiceChannel != null){
            if (!voiceChannel.isConnected()) {
                voiceChannel.join();
            }
            long timeUntilPlay = getMusicManager(guild).scheduler.getTimeUntilDone();
            getMusicManager(guild).scheduler.queue(track);
            //Build message.
            StringBuilder sb = new StringBuilder();
            sb.append("Added **").append(track.getInfo().title).append("** to the queue.");
            //If there's some tracks in the queue, get the time until this one plays.
            if (timeUntilPlay != 0){
                sb.append(String.format("\nTime until play: %d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(timeUntilPlay),
                        TimeUnit.MILLISECONDS.toSeconds(timeUntilPlay) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeUntilPlay))
                ));
            }
            IMessage message = getChatChannel(guild).sendMessage(sb.toString());
            DisappearingMessage.deleteMessageAfter(3000, message);
        }

    }

    /**
     * If possible, try to begin playing from the track scheduler's queue.
     */
    public void playQueue(IGuild guild){
        IVoiceChannel vc = this.getVoiceChannel(guild);
        if (!vc.isConnected()){
            vc.join();
        }
        getMusicManager(guild).scheduler.nextTrack();
    }

    /**
     * Skips the current track.
     */
    public void skipTrack(IGuild guild){
        getMusicManager(guild).scheduler.nextTrack();
        new DisappearingMessage(getChatChannel(guild), "Skipping the current track.", 3000);
    }

    /**
     * Stops playback and disconnects from the voice channel, to cease music actions.
     * @param guild The guild to quit from.
     */
    public void quit(IGuild guild){
        getMusicManager(guild).scheduler.quit();
        IVoiceChannel vc = this.getVoiceChannel(guild);
        if (vc.isConnected()){
            vc.leave();
        }
    }

}
