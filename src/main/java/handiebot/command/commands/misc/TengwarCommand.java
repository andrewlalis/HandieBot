package handiebot.command.commands.misc;

import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;
import handiebot.utils.MessageUtils;
import net.agspace.TengwarImageGenerator;
import net.agspace.Translator;

import static handiebot.HandieBot.resourceBundle;
import static handiebot.utils.MessageUtils.sendFile;
import static handiebot.utils.MessageUtils.sendMessage;

/**
 * @author Andrew Lalis
 */
public class TengwarCommand extends ContextCommand {

    public TengwarCommand() {
        super("tengwar",
                "<to|from> <TEXT>",
                resourceBundle.getString("commands.command.tengwar.description"),
                0);
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length == 0){
            sendMessage(this.getUsage(context.getGuild()), context.getChannel());
        } else if (context.getArgs().length >= 2){
            String input = MessageUtils.getTextFromArgs(context.getArgs(), 1);
            if (context.getArgs()[0].equalsIgnoreCase("to")){
                String result = Translator.translateToTengwar(input);
                sendFile(TengwarImageGenerator.generateImage(result,
                        600,
                        24f,
                        false,
                        false,
                        System.getProperty("user.home")+"/.handiebot/tengwarTemp.png"),
                        "Raw text: `" +result+'`',
                        context.getChannel());
            } else if (context.getArgs()[0].equalsIgnoreCase("from")){
                sendMessage(Translator.translateToEnglish(input), context.getChannel());
            }
        } else {
            sendMessage(this.getUsage(context.getGuild()), context.getChannel());
        }
    }

}
