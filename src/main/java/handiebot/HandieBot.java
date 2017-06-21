package handiebot;

import handiebot.command.CommandHandler;
import handiebot.lavaplayer.MusicPlayer;
import handiebot.view.BotLog;
import handiebot.view.BotWindow;
import handiebot.view.View;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

/**
 * @author Andrew Lalis
 * Main Class for the discord bot. Contains client loading information and general event processing.
 */
public class HandieBot {

    public static final String APPLICATION_NAME = "HandieBot";
    private static final String TOKEN = "MjgzNjUyOTg5MjEyNjg4Mzg0.C45A_Q.506b0G6my1FEFa7_YY39lxLBHUY";

    public static IDiscordClient client;
    public static View view;
    public static BotWindow window;
    public static BotLog log;

    private CommandHandler commandHandler;
    public static MusicPlayer musicPlayer;

    private HandieBot() {
        this.commandHandler = new CommandHandler(this);

    }

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
        this.commandHandler.handleCommand(event);
    }

    public static void main(String[] args) throws DiscordException, RateLimitException {

        musicPlayer = new MusicPlayer();

        view = new View();
        log = new BotLog(view.getOutputArea());
        window = new BotWindow(view);

        log.log(BotLog.TYPE.INFO, "Logging client in.");
        client = new ClientBuilder().withToken(TOKEN).build();
        client.getDispatcher().registerListener(new HandieBot());
        client.login();

    }

    /**
     * Safely shuts down the bot on all guilds.
     */
    public static void quit(){
        musicPlayer.quitAll();
        client.logout();
        window.dispose();

    }

}
