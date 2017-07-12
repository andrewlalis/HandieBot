package handiebot.utils;

import handiebot.HandieBot;
import handiebot.view.BotLog;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import java.io.File;
import java.io.FileNotFoundException;

import static handiebot.HandieBot.log;
import static handiebot.HandieBot.resourceBundle;
import static java.lang.Thread.sleep;

/**
 * @author Andrew Lalis
 * Creates a message on a channel that will disappear after some time.
 */
public class MessageUtils {

    private MessageUtils(){}

    /**
     * Sends a message to a channel safely, using a request buffer.
     * @param content The string content of the message.
     * @param channel The channel to send the message on.
     * @return The message object that was sent.
     */
    public static IMessage sendMessage(String content, IChannel channel){
        return RequestBuffer.request(() -> (IMessage)channel.sendMessage(content)).get();
    }

    /**
     * Sends an embed object to a channel safely, using a request buffer.
     * @param embed The content of the message.
     * @param channel The channel to send the message on.
     * @return The message object that was sent.
     */
    public static IMessage sendMessage(EmbedObject embed, IChannel channel){
        return RequestBuffer.request(() -> (IMessage)channel.sendMessage(embed)).get();
    }

    /**
     * Sends a file object to a channel safely, using a request buffer.
     * @param file The file to send.
     * @param channel The channel to send the message on.
     * @return The message that was sent, or null if the file could not be found.
     */
    public static IMessage sendFile(File file, IChannel channel){
        return RequestBuffer.request(() -> {
            IMessage msg = null;
            try {
                msg = channel.sendFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return msg;
        }).get();
    }

    /**
     * Adds a reaction to a message safely, using the request buffer.
     * @param message The message to add a reaction to.
     * @param reaction The reaction to add, in string format.
     */
    public static void addReaction(IMessage message, String reaction){
        RequestBuffer.request(() -> message.addReaction(reaction)).get();
    }

    /**
     * Deletes a message after a set amount of time.
     * @param timeout The delay until deletion, in milliseconds.
     * @param message The message to delete.
     */
    public static void deleteMessageAfter(long timeout, IMessage message){
        new Thread(() -> {
            try {
                sleep(timeout);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            if (canDelete(message))
                RequestBuffer.request(message::delete);
        }).start();
    }

    /**
     * Check to see if it is possible to delete a message before doing so.
     * @param message The message that may be deleted.
     * @return True if it is safe to delete, false otherwise.
     */
    private static boolean canDelete(IMessage message){
        if (HandieBot.hasPermission(Permissions.MANAGE_MESSAGES, message.getChannel())){
            return true;
        } else {
            log.log(BotLog.TYPE.ERROR, message.getGuild(), resourceBundle.getString("log.deleteMessageError"));
            return false;
        }
    }

    /**
     * Converts a number into one or more emojis.
     * @param number The number to convert.
     * @return A string of emojis.
     */
    public static String getNumberEmoji(int number){
        StringBuilder sb = new StringBuilder();
        while (number > 0){
            int digit = number % 10;
            number /= 10;
            sb.append(getDigitEmoji(digit));
            if (number > 0){
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * Gets the 'emoji' for a specific digit. If the digit given is not one digit, zero is used.
     * @param digit The digit to convert.
     * @return A String representation of the emoji.
     */
    private static String getDigitEmoji(int digit){
        switch (digit){
            case 1:
                return ":one:";
            case 2:
                return ":two:";
            case 3:
                return ":three:";
            case 4:
                return ":four:";
            case 5:
                return ":five:";
            case 6:
                return ":six:";
            case 7:
                return ":seven:";
            case 8:
                return ":eight:";
            case 9:
                return ":nine:";
            default:
                return ":zero:";
        }
    }

    /**
     * Generates a space-separated list of words from a given index until the end of an arguments list.
     * @param args The args list, as it is given from a command context.
     * @param firstWordIndex The index of the first word to read as text.
     * @return A string of all the words combined, with spaces between each one, just as they would appear in the
     * user's actual input string.
     */
    public static String getTextFromArgs(String[] args, int firstWordIndex){
        StringBuilder sb = new StringBuilder();
        for (int i = firstWordIndex; i < args.length; i++){
            sb.append(args[i]);
            if (i < args.length-1){
                sb.append(' ');
            }
        }
        return sb.toString();
    }

}
