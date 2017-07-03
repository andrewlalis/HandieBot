package handiebot.command.commands.misc;

import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;
import net.agspace.TengwarImageGenerator;
import net.agspace.Translator;

import java.io.FileNotFoundException;

import static handiebot.HandieBot.resourceBundle;

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
            context.getChannel().sendMessage(this.getUsage(context.getGuild()));
        } else if (context.getArgs().length >= 2){
            String input = readTextFromArgs(context.getArgs());
            if (context.getArgs()[0].equalsIgnoreCase("to")){
                String result = Translator.translateToTengwar(input);
                try {
                    context.getChannel().sendFile("Raw text: `" +result+'`', TengwarImageGenerator.generateImage(result,
                            600,
                            24f,
                            false,
                            false,
                            System.getProperty("user.home")+"/.handiebot/tengwarTemp.png"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (context.getArgs()[0].equalsIgnoreCase("from")){
                context.getChannel().sendMessage(Translator.translateToEnglish(input));
            }
        } else {
            context.getChannel().sendMessage(this.getUsage(context.getGuild()));
        }
    }

    private String readTextFromArgs(String[] args){
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++){
            sb.append(args[i]).append(' ');
        }
        return sb.toString().trim();
    }
}
