package handiebot.command.commands;

import handiebot.command.CommandContext;
import handiebot.command.Commands;
import handiebot.command.types.Command;
import handiebot.command.types.ContextCommand;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;

/**
 * @author Andrew Lalis
 * Class for sending help/command info to a user if they so desire it.
 */
public class HelpCommand extends ContextCommand {
//TODO: Finish the help class.
    public HelpCommand() {
        super("help",
                "",
                "Displays a list of commands and what they do.");
    }

    @Override
    public void execute(CommandContext context) {
        IPrivateChannel pm = context.getUser().getOrCreatePMChannel();
        EmbedBuilder builder = new EmbedBuilder();

        builder.withAuthorName("HandieBot");
        builder.withAuthorUrl("https://github.com/andrewlalis/HandieBot");
        builder.withAuthorIcon("https://github.com/andrewlalis/HandieBot/blob/master/src/main/resources/icon.png");

        builder.withColor(new Color(255, 0, 0));
        builder.withDescription("I'm a discord bot that can manage music, as well as some other important functions which will be implemented later on. Some commands are shown below.");

        //Music Commands:

        StringBuilder sb = new StringBuilder();
        for (Command cmd : Commands.commands){
            sb.append('`');
            if (cmd instanceof ContextCommand){
                sb.append(((ContextCommand)cmd).getUsage(context.getGuild()));
            } else {
                sb.append(cmd.getUsage());
            }
            sb.append("`\n").append(cmd.getDescription()).append('\n');
        }

        builder.appendField("Commands:", sb.toString(), false);

        pm.sendMessage(builder.build());
    }
}
