package handiebot.command;

import com.sun.istack.internal.NotNull;
import handiebot.HandieBot;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;

/**
 * @author Andrew Lalis
 * Class to process commands.
 */
public class CommandHandler {

    private static String PREFIX = "!";

    private final HandieBot bot;

    public CommandHandler(HandieBot bot){
        this.bot = bot;
    }

    /**
     * Main method to handle user messages.
     * @param event The event generated by the message.
     */
    public void handleCommand(MessageReceivedEvent event){
        IMessage message = event.getMessage();
        IUser user = event.getAuthor();
        IChannel channel = event.getChannel();
        IGuild guild = event.getGuild();
        String command = extractCommand(message);
        String[] args = extractArgs(message);
        if (guild != null && command != null){
            if (command.equals("play") && args.length == 1){
                this.bot.getMusicPlayer().loadToQueue(guild, args[0]);
            } else if (command.equals("skip") && args.length == 0){
                this.bot.getMusicPlayer().skipTrack(guild);
            } else if (command.equals("help")){
                this.sendHelpInfo(user);
            } else if (command.equals("playnow") && args.length == 1){

            }
        }
    }

    /**
     * Returns a command word, if one exists, from a given message.
     * @param message The message to get a command from.
     * @return The command word, minus the prefix, or null.
     */
    private String extractCommand(IMessage message){
        String[] words = message.getContent().split(" ");
        if (words[0].startsWith(PREFIX)){
            return words[0].replaceFirst(PREFIX, "").toLowerCase();
        }
        return null;
    }

    /**
     * Extracts a list of arguments from a message, assuming a command exists.
     * @param message The message to parse.
     * @return A list of strings representing args.
     */
    @NotNull
    private String[] extractArgs(IMessage message){
        String[] words = message.getContent().split(" ");
        if (words[0].startsWith(PREFIX)){
            String[] args = new String[words.length-1];
            for (int i = 0; i < words.length-1; i++){
                args[i] = words[i+1];
            }
            return args;
        }
        return new String[0];
    }

    /**
     * Method to send a useful list of commands to any user if they desire.
     * @param user The user to send the message to.
     */
    private void sendHelpInfo(IUser user){
        IPrivateChannel pm = user.getOrCreatePMChannel();
        EmbedBuilder builder = new EmbedBuilder();

        builder.withAuthorName("HandieBot");
        builder.withAuthorUrl("https://github.com/andrewlalis/HandieBot");
        builder.withAuthorIcon("https://github.com/andrewlalis/HandieBot/blob/master/src/main/resources/icon.png");

        builder.withColor(new Color(255, 0, 0));
        builder.withDescription("I'm a discord bot that can manage music, as well as some other important functions which will be implemented later on. Some commands are shown below.");
        builder.appendField("Commands:", "play, skip, help", false);

        pm.sendMessage(builder.build());
    }

    /**
     * Sets the prefix used to identify commands.
     * @param prefix The prefix appended to the beginning of commands.
     */
    public void setPrefix(String prefix){
        PREFIX = prefix;
    }

}
