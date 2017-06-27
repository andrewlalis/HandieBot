package handiebot;

import handiebot.command.CommandHandler;
import handiebot.command.ReactionHandler;
import handiebot.lavaplayer.MusicPlayer;
import handiebot.view.BotLog;
import handiebot.view.BotWindow;
import handiebot.view.View;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
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

    public static ResourceBundle resourceBundle = ResourceBundle.getBundle("Strings");

    //Discord client object.
    public static IDiscordClient client;

    //Display objects.
    public static View view;
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
        log.log(BotLog.TYPE.INFO, "HandieBot initialized.");
        //client.changeAvatar(Image.forStream("png", getClass().getClassLoader().getResourceAsStream("avatarIcon.png")));
    }

    public static void main(String[] args) throws DiscordException, RateLimitException {

        musicPlayer = new MusicPlayer();

        view = new View();
        log = new BotLog(view.getOutputArea());
        window = new BotWindow(view);

        log.log(BotLog.TYPE.INFO, "Logging client in...");
        client = new ClientBuilder().withToken(TOKEN).build();
        client.getDispatcher().registerListener(new HandieBot());
        client.login();
    }

    /**
     * Gets the integer value representing all permission flags.
     * @param guild The guild to get permissions for.
     * @return int representing permissions.
     */
    private int getClientPermissions(IGuild guild){
        List<IRole> roles = client.getOurUser().getRolesForGuild(guild);
        int allPermissions = 0;
        for (IRole role : roles) {
            allPermissions = allPermissions | Permissions.generatePermissionsNumber(role.getPermissions());
        }
        return allPermissions;
    }

    /**
     * Returns whether or not the user has a certain permission.
     * @param user The user to check for permission.
     * @param guild The guild to get the permissions for.
     * @return True if the bot has this permission, false if not.
     */
    boolean hasPermission(IUser user, IGuild guild){
        return Permissions.getAllowedPermissionsForNumber(getClientPermissions(guild)).contains(user.getPermissionsForGuild(guild));
    }

    /**
     * Safely shuts down the bot on all guilds.
     */
    public static void quit(){
        log.log(BotLog.TYPE.INFO, "Shutting down the bot.");
        musicPlayer.quitAll();
        client.logout();
        window.dispose();
        System.exit(0);
    }

}
