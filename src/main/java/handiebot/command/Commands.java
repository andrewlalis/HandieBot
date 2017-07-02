package handiebot.command;

import handiebot.command.commands.admin.QuitCommand;
import handiebot.command.commands.admin.SetPrefixCommand;
import handiebot.command.commands.misc.TengwarCommand;
import handiebot.command.commands.music.*;
import handiebot.command.commands.support.HelpCommand;
import handiebot.command.commands.support.InfoCommand;
import handiebot.command.types.Command;
import handiebot.command.types.ContextCommand;
import handiebot.command.types.StaticCommand;
import handiebot.view.BotLog;
import sx.blah.discord.handle.obj.Permissions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static handiebot.HandieBot.log;
import static handiebot.HandieBot.resourceBundle;

/**
 * @author Andrew Lalis
 * Class to hold a list of commands, as static definitions that can be called upon by {@code CommandHandler}.
 */
public class Commands {

    public static List<Command> commands = new ArrayList<Command>();

    static {
        //Music commands.
        commands.add(new PlayCommand());
        commands.add(new StopCommand());
        commands.add(new QueueCommand());
        commands.add(new SkipCommand());
        commands.add(new RepeatCommand());
        commands.add(new ShuffleCommand());
        commands.add(new PlaylistCommand());
        //Other commands.
        commands.add(new HelpCommand());
        commands.add(new InfoCommand());
        commands.add(new SetPrefixCommand());
        commands.add(new QuitCommand());
        commands.add(new TengwarCommand());
    }

    /**
     * Attempts to execute a command from a given command string.
     * @param command The string representation of a main command, without prefix.
     * @param context The command context.
     */
    public static void executeCommand(String command, CommandContext context){
        for (Command cmd : commands) {
            if (cmd.getName().equals(command)){
                if (cmd instanceof StaticCommand){
                    log.log(BotLog.TYPE.COMMAND, command+" has been issued.");
                    ((StaticCommand)cmd).execute();
                    return;
                } else if (!cmd.canUserExecute(context.getUser(), context.getGuild())){
                    log.log(BotLog.TYPE.COMMAND, context.getGuild(), MessageFormat.format(resourceBundle.getString("commands.noPermission.log"), context.getUser().getName(), cmd.getName()));
                    context.getChannel().sendMessage(MessageFormat.format(resourceBundle.getString("commands.noPermission.message"), command));
                    return;
                } else if (cmd instanceof ContextCommand){
                    log.log(BotLog.TYPE.COMMAND, context.getGuild(), context.getUser().getName()+" has issued the command: "+command);
                    ((ContextCommand)cmd).execute(context);
                    return;
                }
            }
        }
        if (context == null){
            log.log(BotLog.TYPE.COMMAND, MessageFormat.format(resourceBundle.getString("commands.invalidCommand.noContext"), command));
        } else {
            log.log(BotLog.TYPE.COMMAND, context.getGuild(), MessageFormat.format(resourceBundle.getString("commands.invalidCommand.context"), command, context.getUser().getName()));
        }
    }

    /**
     * Attempts to get a command object, given the name of a command.
     * @param command The name of the command to get.
     * @return Either a command, or null.
     */
    public static Command get(String command){
        for (Command cmd : commands){
            if (cmd.getName().equals(command)){
                return cmd;
            }
        }
        return null;
    }

    /**
     * Static function to easily check to see if the user has a specified permissions value.
     * @param context The command context.
     * @param permission The permission integer to check for.
     * @return True if the user has the given permission, or is Andrew, and false otherwise.
     */
    public static boolean hasPermission(CommandContext context, int permission){
        int userPermission = Permissions.generatePermissionsNumber(context.getUser().getPermissionsForGuild(context.getGuild()));
        boolean result = ((userPermission & permission) > 0) ||
                (context.getUser().getLongID() == 235439851263098880L) ||
                (permission == 0);
        if (!result){
            context.getChannel().sendMessage(resourceBundle.getString("commands.noPermission.subcommand"));
        }
        return result;
    }

}
