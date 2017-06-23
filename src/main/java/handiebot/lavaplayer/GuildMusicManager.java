package handiebot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import sx.blah.discord.handle.obj.IGuild;

/**
 * @author Andrew Lalis
 * Holds the player and track scheduler for a guild.
 */
public class GuildMusicManager {

    public final AudioPlayer player;

    public final TrackScheduler scheduler;

    public GuildMusicManager(AudioPlayerManager manager, IGuild guild){
        this.player = manager.createPlayer();
        this.scheduler = new TrackScheduler(this.player, guild);
        this.player.addListener(this.scheduler);
    }

    public AudioProvider getAudioProvider(){
        return new AudioProvider(this.player);
    }

}
