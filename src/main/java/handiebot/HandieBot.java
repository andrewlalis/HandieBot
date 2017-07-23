package handiebot;

import handiebot.command.CommandHandler;
import handiebot.command.ReactionHandler;
import handiebot.lavaplayer.MusicPlayer;
import handiebot.utils.FileUtil;
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

import java.io.*;
import java.util.*;

/**
 * @author Andrew Lalis
 * Main Class for the discord bot. Contains client loading information and general event processing.
 * Most variables are static here because this is the main file for the Bot across many possible guilds it could
 * be runnnig on, so it is no problem to have only one copy.
 */
public class HandieBot {

    //Application name is the name of application as it appears on display window.
    public static final String APPLICATION_NAME = "HandieBot";

    //The token required for logging into Discord. This is secure and must not be in the source code on GitHub.
    private static final String TOKEN;
    static {
        TOKEN = readToken();
        if (TOKEN.isEmpty()){
            System.out.println("You do not have the token required to start the bot. Shutting down.");
            System.exit(-1);
        }
    }
    //Variable to enable or disable GUI.
    private static boolean USE_GUI = true;

    //Settings for the bot. Tries to load the settings, or if that doesn't work, it will load defaults.
    public static Properties settings;
    static{
        try {
            Properties defaultSettings = new Properties();
            defaultSettings.load(HandieBot.class.getClassLoader().getResourceAsStream("default_settings"));
            settings = new Properties(defaultSettings);
            settings.load(new FileInputStream(FileUtil.getDataDirectory()+"settings"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Resource bundle for localized strings.
    public static ResourceBundle resourceBundle = ResourceBundle.getBundle("Strings", Locale.forLanguageTag(settings.getProperty("language")));

    //Discord client object.
    public static IDiscordClient client;

    //Display objects.
    public static BotWindow window;
    public static BotLog log;

    //The cross-guild music player.
    public static MusicPlayer musicPlayer;

    //List of all permissions needed to operate this bot.
    private static final int permissionsNumber;
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

        List<String> argsList = Arrays.asList(args);

        if (argsList.contains("-nogui")) {
            System.out.println("Starting with no GUI.");
            USE_GUI = false;
            log = new BotLog(null);
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
     * Reads the private discord token necessary to start the bot. If this fails, the bot will shut down.
     * @return The string token needed to log in.
     */
    private static String readToken(){
        String path = FileUtil.getDataDirectory()+"token.txt";
        String result = "";
        try(BufferedReader reader = new BufferedReader(new FileReader(path))){
            result = reader.readLine();
        } catch (IOException e) {
            System.err.println("Unable to find the token file. You are unable to start the bot without this.");
        }
        return result;
    }

    /**
     * Safely shuts down the bot on all guilds.
     */
    public static void quit(){
        log.log(BotLog.TYPE.INFO, resourceBundle.getString("log.shuttingDown"));
        musicPlayer.quitAll();
        client.logout();
        window.dispose();
        try {
            settings.store(new FileWriter(FileUtil.getDataDirectory()+"settings"), "Settings for HandieBot");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
