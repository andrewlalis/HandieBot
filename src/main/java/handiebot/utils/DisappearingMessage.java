package handiebot.utils;

import handiebot.HandieBot;
import handiebot.view.BotLog;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;

import static handiebot.HandieBot.log;
import static handiebot.HandieBot.resourceBundle;

/**
 * @author Andrew Lalis
 * Creates a message on a channel that will disappear after some time.
 */
public class DisappearingMessage extends Thread implements Runnable {
    
    /**
     * Creates a new disappearing message that times out after some time.
     * @param channel The channel to write the message in.
     * @param message The message content.
     * @param timeout How long until the message is deleted.
     */
    public DisappearingMessage(IChannel channel, String message, long timeout){
        IMessage sentMessage = channel.sendMessage(message);
        try {
            sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (canDelete(sentMessage))
            sentMessage.delete();
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
                message.delete();
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

}
