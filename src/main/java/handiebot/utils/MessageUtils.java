package handiebot.utils;

import handiebot.HandieBot;
import handiebot.view.BotLog;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import static handiebot.HandieBot.log;
import static handiebot.HandieBot.resourceBundle;

/**
 * @author Andrew Lalis
 * Creates a message on a channel that will disappear after some time.
 */
public class MessageUtils extends Thread implements Runnable {

    private MessageUtils(){}

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

}
