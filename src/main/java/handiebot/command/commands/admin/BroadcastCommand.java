package handiebot.command.commands.admin;

import handiebot.command.CommandContext;
import handiebot.command.types.CommandLineCommand;
import handiebot.command.types.ContextCommand;
import handiebot.utils.MessageUtils;
import sx.blah.discord.handle.obj.IGuild;

import static handiebot.HandieBot.*;
import static handiebot.utils.MessageUtils.sendMessage;

/**
 * @author Andrew Lalis
 * Command to broadcast a message to all guilds the bot is connected to.
 */
public class BroadcastCommand extends ContextCommand implements CommandLineCommand {

    public BroadcastCommand() {
        super("broadcast",
                "<message>",
                resourceBundle.getString("commands.command.broadcast.description"),
                8);
    }

    @Override
    public void execute(CommandContext context) {
        String message = MessageUtils.getTextFromArgs(context.getArgs(), 0);
        for (IGuild guild : client.getGuilds()){
            sendMessage(message, musicPlayer.getChatChannel(guild));
        }
    }

}
