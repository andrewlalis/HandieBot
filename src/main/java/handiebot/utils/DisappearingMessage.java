package handiebot.utils;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

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
                message.delete();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }).start();
    }

}
