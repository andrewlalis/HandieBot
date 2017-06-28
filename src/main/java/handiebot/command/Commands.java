package handiebot.command;

import handiebot.command.commands.admin.QuitCommand;
import handiebot.command.commands.admin.SetPrefixCommand;
import handiebot.command.commands.music.*;
import handiebot.command.commands.support.HelpCommand;
import handiebot.command.commands.support.InfoCommand;
import handiebot.command.types.Command;
import handiebot.command.types.ContextCommand;
import handiebot.command.types.StaticCommand;
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
                if (cmd instanceof StaticCommand){
                    ((StaticCommand)cmd).execute();
                } else if (!cmd.canUserExecute(context.getUser(), context.getGuild())){
                    log.log(BotLog.TYPE.ERROR, context.getGuild(), "User "+context.getUser().getName()+" does not have permission to execute "+cmd.getName());
                    context.getChannel().sendMessage("You do not have permission to use the command `"+command+"`.");
                } else if (cmd instanceof ContextCommand){
                    ((ContextCommand)cmd).execute(context);
                }
            }
        }
        if (context == null){
            log.log(BotLog.TYPE.ERROR, "Invalid command issued: "+command);
        } else {
            log.log(BotLog.TYPE.ERROR, context.getGuild(), "Invalid command: " + command + " issued by " + context.getUser().getName());
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
