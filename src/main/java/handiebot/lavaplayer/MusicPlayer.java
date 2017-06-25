package handiebot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import handiebot.HandieBot;
import handiebot.command.CommandHandler;
import handiebot.lavaplayer.playlist.Playlist;
import handiebot.lavaplayer.playlist.UnloadedTrack;
import handiebot.utils.DisappearingMessage;
import handiebot.utils.Pastebin;
import handiebot.view.BotLog;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static handiebot.HandieBot.log;

/**
 * @author Andrew Lalis
 * This class is a container for all the music related functions, and contains methods for easy playback and queue
 * management.
 * The goal is to abstract all functions to this layer, rather than have the bot interact directly with any schedulers.
 */
public class MusicPlayer {

    //Name for the message and voice channels dedicated to this bot.
    static String CHANNEL_NAME = "HandieBotMusic";

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

    public AudioPlayerManager getPlayerManager(){
        return this.playerManager;
    }

    /**
     * Gets the music manager specific to a particular guild.
     * @param guild The guild to get the music manager for.
     * @return The music manager for a guild.
     */
    public GuildMusicManager getMusicManager(IGuild guild){
        if (!this.musicManagers.containsKey(guild)){
            log.log(BotLog.TYPE.MUSIC, guild, "Creating new music manager and audio provider for guild: "+guild.getName());
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
    public IChannel getChatChannel(IGuild guild){
        if (!this.chatChannels.containsKey(guild)){
            List<IChannel> channels = guild.getChannelsByName(CHANNEL_NAME.toLowerCase());
            if (channels.isEmpty()){
                log.log(BotLog.TYPE.MUSIC, guild, "No chat channel found, creating a new one.");
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
    public IVoiceChannel getVoiceChannel(IGuild guild){
        if (!this.voiceChannels.containsKey(guild)){
            List<IVoiceChannel> channels = guild.getVoiceChannelsByName(CHANNEL_NAME);
            if (channels.isEmpty()){
                log.log(BotLog.TYPE.MUSIC, guild, "No voice channel found, creating a new one.");
                this.voiceChannels.put(guild, guild.createVoiceChannel(CHANNEL_NAME));
            } else {
                this.voiceChannels.put(guild, channels.get(0));
            }
        }
        return this.voiceChannels.get(guild);
    }

    /**
     * Toggles the repeating of songs for a particular guild.
     * @param guild The guild to repeat for.
     */
    public void toggleRepeat(IGuild guild){
        setRepeat(guild, !getMusicManager(guild).scheduler.isRepeating());
    }

    /**
     * Sets the repeating of songs for a particular guild.
     * @param guild The guild to set repeat for.
     * @param value True to repeat, false otherwise.
     */
    public void setRepeat(IGuild guild, boolean value){
        getMusicManager(guild).scheduler.setRepeat(value);
        log.log(BotLog.TYPE.MUSIC, guild, "Set repeat to "+getMusicManager(guild).scheduler.isRepeating());
        new DisappearingMessage(getChatChannel(guild), "Set repeat to "+getMusicManager(guild).scheduler.isRepeating(), 3000);
    }

    /**
     * Toggles shuffling for a specific guild.
     * @param guild The guild to toggle shuffling for.
     */
    public void toggleShuffle(IGuild guild){
        setShuffle(guild, !getMusicManager(guild).scheduler.isShuffling());
    }

    /**
     * Sets shuffling for a specific guild.
     * @param guild The guild to set shuffling for.
     * @param value The value to set. True for shuffling, false for linear play.
     */
    public void setShuffle(IGuild guild, boolean value){
        getMusicManager(guild).scheduler.setShuffle(value);
        log.log(BotLog.TYPE.MUSIC, guild, "Set shuffle to "+Boolean.toString(HandieBot.musicPlayer.getMusicManager(guild).scheduler.isShuffling()));
        new DisappearingMessage(getChatChannel(guild), "Set shuffle to "+Boolean.toString(HandieBot.musicPlayer.getMusicManager(guild).scheduler.isShuffling()), 3000);
    }

    /**
     * Sends a formatted message to the guild about the first few items in a queue.
     */
    public void showQueueList(IGuild guild, boolean showAll) {
        List<UnloadedTrack> tracks = getMusicManager(guild).scheduler.queueList();
        if (tracks.size() == 0) {
            new DisappearingMessage(getChatChannel(guild), "The queue is empty. Use **"+ CommandHandler.PREFIXES.get(guild)+"play** *URL* to add songs.", 3000);
        } else {
            if (tracks.size() > 10 && showAll) {
                String result = Pastebin.paste("Current queue for discord server: "+guild.getName()+".", getMusicManager(guild).scheduler.getActivePlaylist().toString());
                if (result != null && result.startsWith("https://pastebin.com/")){
                    log.log(BotLog.TYPE.INFO, guild, "Queue uploaded to pastebin: "+result);
                    new DisappearingMessage(getChatChannel(guild), "You may view the full queue by following the link: "+result, 600000);
                } else {
                    log.log(BotLog.TYPE.ERROR, guild, "Unable to upload to pastebin: "+result);
                }
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.withColor(255, 0, 0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < (tracks.size() <= 10 ? tracks.size() : 10); i++) {
                    sb.append(i + 1).append(". [").append(tracks.get(i).getTitle()).append("](");
                    sb.append(tracks.get(i).getURL()).append(")");
                    sb.append(tracks.get(i).getFormattedDuration()).append('\n');
                }
                builder.appendField("Showing " + (tracks.size() <= 10 ? tracks.size() : "the first 10") + " track" + (tracks.size() > 1 ? "s" : "") + " out of "+tracks.size()+".", sb.toString(), false);
                IMessage message = getChatChannel(guild).sendMessage(builder.build());
                DisappearingMessage.deleteMessageAfter(6000, message);
            }
        }
    }

    /**
     * Adds a track to the queue and sends a message to the appropriate channel notifying users.
     * @param track The track to queue.
     */
    public void addToQueue(IGuild guild, UnloadedTrack track){
        IVoiceChannel voiceChannel = getVoiceChannel(guild);
        if (voiceChannel != null){
            if (!voiceChannel.isConnected()) {
                voiceChannel.join();
            }
            long timeUntilPlay = getMusicManager(guild).scheduler.getTimeUntilDone();
            getMusicManager(guild).scheduler.queue(track);
            //Build message.
            StringBuilder sb = new StringBuilder();
            if (timeUntilPlay > 0) {
                sb.append("Added **").append(track.getTitle()).append("** to the queue.");
            }
            //If there's some tracks in the queue, get the time until this one plays.
            if (timeUntilPlay > 0){
                sb.append(String.format("\nTime until play: %d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(timeUntilPlay),
                        TimeUnit.MILLISECONDS.toSeconds(timeUntilPlay) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeUntilPlay))
                ));
            }
            if (sb.length() > 0) {
                IMessage message = getChatChannel(guild).sendMessage(sb.toString());
                DisappearingMessage.deleteMessageAfter(3000, message);
            }
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

    public void clearQueue(IGuild guild){
        getMusicManager(guild).scheduler.clearQueue();
        new DisappearingMessage(getChatChannel(guild), "Cleared the queue.", 5000);
    }

    /**
     * Skips the current track.
     */
    public void skipTrack(IGuild guild){
        getMusicManager(guild).scheduler.nextTrack();
        log.log(BotLog.TYPE.MUSIC, guild, "Skipping the current track. ");
        new DisappearingMessage(getChatChannel(guild), "Skipping the current track.", 3000);
    }

    /**
     * Stops playback and disconnects from the voice channel, to cease music actions.
     * @param guild The guild to stop from.
     */
    public void stop(IGuild guild){
        getMusicManager(guild).scheduler.stop();
        new DisappearingMessage(getChatChannel(guild), "Stopped playing music.", 5000);
        log.log(BotLog.TYPE.MUSIC, guild, "Stopped playing music.");
    }

    /**
     * Returns a playlist of all songs either in the queue or being played now.
     * @param guild The guild to get songs from.
     * @return A list of songs in the form of a playlist.
     */
    public Playlist getAllSongsInQueue(IGuild guild){
        GuildMusicManager musicManager = getMusicManager(guild);
        Playlist p = new Playlist("Active Queue");
        p.copy(musicManager.scheduler.getActivePlaylist());
        UnloadedTrack track = musicManager.scheduler.getPlayingTrack();
        if (track != null){
            p.addTrack(track);
        }
        return p;
    }

    /**
     * Performs the same functions as stop, but with every guild.
     */
    public void quitAll(){
        this.musicManagers.forEach((guild, musicManager) -> {
            musicManager.scheduler.stop();
        });
        this.playerManager.shutdown();
    }

}
