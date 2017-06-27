package handiebot.command.commands.admin;

import handiebot.HandieBot;
import handiebot.command.types.StaticCommand;

/**
 * @author Andrew Lalis
 * Command to quit the entire bot. This shuts down every guild's support, and the GUI.
 */
public class QuitCommand extends StaticCommand {

    public QuitCommand() {
        super("quit",
                "",
                "Shuts down the bot on all servers.",
                8);
    }

    @Override
    public void execute() {
        HandieBot.quit();
    }
}
