package handiebot.command.commands;

import handiebot.command.CommandContext;
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
        super("help");
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
        builder.appendField("Commands:", "play, skip, help", false);

        pm.sendMessage(builder.build());
    }
}
