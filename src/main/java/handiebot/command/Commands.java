package handiebot.command;

import handiebot.command.commands.admin.QuitCommand;
import handiebot.command.commands.admin.SetPrefixCommand;
import handiebot.command.commands.music.*;
import handiebot.command.commands.support.HelpCommand;
import handiebot.command.commands.support.InfoCommand;
import handiebot.command.types.Command;
import handiebot.command.types.ContextCommand;
import handiebot.command.types.StaticCommand;
import handiebot.utils.DisappearingMessage;
import handiebot.view.BotLog;

import java.util.ArrayList;
import java.util.List;

import static handiebot.HandieBot.log;

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
    }

    /**
     * Attempts to execute a command from a given command string.
     * @param command The string representation of a main command, without prefix.
     * @param context The command context.
     */
    public static void executeCommand(String command, CommandContext context){
        for (Command cmd : commands) {
            if (cmd.getName().equals(command)){
                if (!cmd.canUserExecute(context.getUser(), context.getGuild())){
                    log.log(BotLog.TYPE.ERROR, context.getGuild(), "User "+context.getUser().getName()+" does not have permission to execute "+cmd.getName());
                    new DisappearingMessage(context.getChannel(), "You do not have permission to use that command.", 5000);
                }
                if (cmd instanceof ContextCommand){
                    ((ContextCommand)cmd).execute(context);
                    return;
                } else if (cmd instanceof StaticCommand){
                    ((StaticCommand)cmd).execute();
                    return;
                }
            }
        }
        log.log(BotLog.TYPE.ERROR, context.getGuild(), "Invalid command: "+command+" issued by "+context.getUser().getName());
    }

    /**
     * Attempts to execute a command.
     * @param command The command to execute.
     * @param context The command context.
     */
    public static void executeCommand(Command command, CommandContext context){
        if (command instanceof ContextCommand && context != null){
            ((ContextCommand)command).execute(context);
        } else if (command instanceof StaticCommand){
            ((StaticCommand)command).execute();
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

}
