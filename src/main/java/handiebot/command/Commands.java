package handiebot.command;

import handiebot.command.commands.HelpCommand;
import handiebot.command.commands.InfoCommand;
import handiebot.command.commands.SetPrefixCommand;
import handiebot.command.commands.music.*;
import handiebot.command.types.Command;
import handiebot.command.types.ContextCommand;
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
        commands.add(new QueueCommand());
        commands.add(new SkipCommand());
        commands.add(new RepeatCommand());
        commands.add(new ShuffleCommand());
        commands.add(new PlaylistCommand());
        //Other commands.
        commands.add(new HelpCommand());
        commands.add(new InfoCommand());
        commands.add(new SetPrefixCommand());
    }

    /**
     * Attempts to execute a command from a given command string.
     * @param command The string representation of a main command, without prefix.
     */
    public static void executeCommand(String command, CommandContext context){
        for (Command cmd : commands) {
            if (cmd.getName().equals(command)){
                if (cmd instanceof ContextCommand){
                    ((ContextCommand)cmd).execute(context);
                    return;
                }
            }
        }
        log.log(BotLog.TYPE.ERROR, context.getGuild(), "Invalid command: "+command+" issued by "+context.getUser().getName());
    }

}
