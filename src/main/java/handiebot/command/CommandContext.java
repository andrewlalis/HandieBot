package handiebot.command;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

/**
 * @author Andrew Lalis
 * Class to hold important data for a command, such as user, channel, and guild.
 */
public class CommandContext {

    private IUser user;
    private IChannel channel;
    private IGuild guild;
    private String[] args;

    public CommandContext(IUser user, IChannel channel, IGuild guild, String[] args){
        this.user = user;
        this.channel = channel;
        this.guild = guild;
        this.args = args;
    }

    public IUser getUser(){
        return this.user;
    }

    public IChannel getChannel(){
        return this.channel;
    }

    public IGuild getGuild(){
        return this.guild;
    }

    public String[] getArgs(){
        return this.args;
    }

}
