package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;

/**
 * @author Andrew Lalis
 * Skips the current song, if there is one playing.
 */
public class SkipCommand extends ContextCommand {

    public SkipCommand() {
        super("skip",
                "",
                "Skips the current song.",
                8);
    }

    @Override
    public void execute(CommandContext context) {
        HandieBot.musicPlayer.skipTrack(context.getGuild());
    }

}
