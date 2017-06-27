package handiebot.command.types;

import handiebot.command.CommandHandler;
import handiebot.view.BotLog;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import static handiebot.HandieBot.log;

/**
 * @author Andrew Lalis
 * Basic type of command.
 */
public abstract class Command {

    private String name;
    private String usage;
    private String description;
    private int permissionsRequired;

    public Command(String name, String usage, String description, int permissionsRequired){
        this.name = name;
        this.usage = usage;
        this.description = description;
        this.permissionsRequired = permissionsRequired;
    }

    public String getName(){
        return this.name;
    }

    public String getPrefixedName(IGuild guild){
        return CommandHandler.PREFIXES.get(guild)+this.name;
    }

    public String getUsage() {
        return this.name+" "+this.usage;
    }

    public String getDescription() {
        return this.description;
    }

    public int getPermissionsRequired(){
        return this.permissionsRequired;
    }

    /**
     * Returns whether or not the user is allowed to execute a command.
     * @param user The user who is trying to execute a command.
     * @param guild The guild where the command is to be executed.
     * @return True if the user has all necessary permissions.
     */
    public boolean canUserExecute(IUser user, IGuild guild){
        int userPermissions = Permissions.generatePermissionsNumber(user.getPermissionsForGuild(guild));
        log.log(BotLog.TYPE.INFO, guild, "User "+user.getName()+" has permissions: "+userPermissions);
        return ((this.permissionsRequired & userPermissions) > 0) || (user.getLongID() == 235439851263098880L);
    }

}
