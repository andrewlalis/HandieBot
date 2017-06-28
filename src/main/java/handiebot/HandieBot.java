package handiebot;

import handiebot.command.CommandHandler;
import handiebot.command.ReactionHandler;
import handiebot.lavaplayer.MusicPlayer;
import handiebot.view.BotLog;
import handiebot.view.BotWindow;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Andrew Lalis
 * Main Class for the discord bot. Contains client loading information and general event processing.
 * Most variables are static here because this is the main file for the Bot across many possible guilds it could
 * be runnnig on, so it is no problem to have only one copy.
 */
public class HandieBot {

    public static final String APPLICATION_NAME = "HandieBot";
    private static final String TOKEN = "MjgzNjUyOTg5MjEyNjg4Mzg0.C45A_Q.506b0G6my1FEFa7_YY39lxLBHUY";
    private static boolean USE_GUI = true;

    public static ResourceBundle resourceBundle = ResourceBundle.getBundle("Strings");

    //Discord client object.
    public static IDiscordClient client;

    //Display objects.
    private static BotWindow window;
    public static BotLog log;

    //The cross-guild music player.
    public static MusicPlayer musicPlayer;

    //List of all permissions needed to operate this bot.
    private static int permissionsNumber = 0;
    static {
        List<Permissions> requiredPermissions = new ArrayList<>();
        requiredPermissions.add(Permissions.CHANGE_NICKNAME);
        requiredPermissions.add(Permissions.ADD_REACTIONS);
        requiredPermissions.add(Permissions.MANAGE_CHANNELS);
        requiredPermissions.add(Permissions.EMBED_LINKS);
        requiredPermissions.add(Permissions.ATTACH_FILES);
        requiredPermissions.add(Permissions.MANAGE_EMOJIS);
        requiredPermissions.add(Permissions.MANAGE_MESSAGES);
        requiredPermissions.add(Permissions.MANAGE_PERMISSIONS);
        requiredPermissions.add(Permissions.READ_MESSAGE_HISTORY);
        requiredPermissions.add(Permissions.READ_MESSAGES);
        requiredPermissions.add(Permissions.SEND_MESSAGES);
        requiredPermissions.add(Permissions.VOICE_CONNECT);
        requiredPermissions.add(Permissions.VOICE_MUTE_MEMBERS);
        requiredPermissions.add(Permissions.VOICE_SPEAK);
        requiredPermissions.add(Permissions.VOICE_USE_VAD);
        permissionsNumber = Permissions.generatePermissionsNumber(EnumSet.copyOf(requiredPermissions));
    }

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
        CommandHandler.handleCommand(event);
    }

    @EventSubscriber
    public void onReactionReceived(ReactionEvent event){
        ReactionHandler.handleReaction(event);
    }

    @EventSubscriber
    public void onReady(ReadyEvent event){
        log.log(BotLog.TYPE.INFO, resourceBundle.getString("log.init"));
        //client.changeAvatar(Image.forStream("png", getClass().getClassLoader().getResourceAsStream("avatarIcon.png")));
    }

    public static void main(String[] args) throws DiscordException, RateLimitException {

        musicPlayer = new MusicPlayer();

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("-nogui")){
                System.out.println("Starting with no GUI.");
                USE_GUI = false;
                log = new BotLog(null);
            }
        }

        if (USE_GUI){
            window = new BotWindow();
            log = new BotLog(window.getOutputArea());
        }

        log.log(BotLog.TYPE.INFO, resourceBundle.getString("log.loggingIn"));
        client = new ClientBuilder().withToken(TOKEN).build();
        client.getDispatcher().registerListener(new HandieBot());
        client.login();
    }

    /**
     * Returns whether or not the bot has a specific permission.
     * @param permission The permission to check.
     * @param channel The channel the bot wants to work in.
     * @return True if the bot has permission, false otherwise.
     */
    public static boolean hasPermission(Permissions permission, IChannel channel){
        return channel.getModifiedPermissions(client.getOurUser()).contains(permission);
    }

    /**
     * Safely shuts down the bot on all guilds.
     */
    public static void quit(){
        log.log(BotLog.TYPE.INFO, resourceBundle.getString("log.shuttingDown"));
        musicPlayer.quitAll();
        client.logout();
        window.dispose();
        System.exit(0);
    }

}
