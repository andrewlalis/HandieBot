package handiebot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import handiebot.command.CommandHandler;
import handiebot.lavaplayer.GuildMusicManager;
import handiebot.lavaplayer.MusicPlayer;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Lalis
 * Main Class for the discord bot. Contains client loading information and general event processing.
 */
public class HandieBot {

    private static final String TOKEN = "MjgzNjUyOTg5MjEyNjg4Mzg0.C45A_Q.506b0G6my1FEFa7_YY39lxLBHUY";

    private static IDiscordClient client;

    private CommandHandler commandHandler;

    public static void main(String[] args) throws DiscordException, RateLimitException {
        System.out.println("Logging bot in...");
        client = new ClientBuilder().withToken(TOKEN).build();
        client.getDispatcher().registerListener(new HandieBot());
        client.login();
    }

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;
    private MusicPlayer musicPlayer;

    private HandieBot() {
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        this.commandHandler = new CommandHandler(this);
        this.musicPlayer = new MusicPlayer();
    }

    public MusicPlayer getMusicPlayer(){
        return this.musicPlayer;
    }

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
        this.commandHandler.handleCommand(event);
    }


}
