package handiebot;

import handiebot.command.CommandHandler;
import handiebot.lavaplayer.MusicPlayer;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.util.logging.Logger;

/**
 * @author Andrew Lalis
 * Main Class for the discord bot. Contains client loading information and general event processing.
 */
public class HandieBot {

    public static Logger log = Logger.getLogger("HandieBotLog");

    private static final String TOKEN = "MjgzNjUyOTg5MjEyNjg4Mzg0.C45A_Q.506b0G6my1FEFa7_YY39lxLBHUY";

    private static IDiscordClient client;

    private CommandHandler commandHandler;
    private MusicPlayer musicPlayer;

    private HandieBot() {
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

    public static void main(String[] args) throws DiscordException, RateLimitException {
        System.out.println("Logging bot in.");
        client = new ClientBuilder().withToken(TOKEN).build();
        client.getDispatcher().registerListener(new HandieBot());
        client.login();
    }

}
