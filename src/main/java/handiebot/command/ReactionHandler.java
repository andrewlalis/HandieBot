package handiebot.command;

import handiebot.command.types.ReactionListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Lalis
 * Class which handles user reactions to songs and performs necessary actions.
 */
public class ReactionHandler {

    private static List<ReactionListener> listeners = new ArrayList<>();
    private static boolean iterating = false;
    private static List<ReactionListener> listenersToRemove = new ArrayList<>();
    /**
     * Adds a listener, so that it is notified when reaction events are received.
     * @param listener The listener to add.
     */
    public static void addListener(ReactionListener listener){
        listeners.add(listener);
    }

    /**
     * Removes a listener from the list of reaction listeners.
     * @param listener The listener to remove.
     */
    public static void removeListener(ReactionListener listener){
        if (iterating){
            listenersToRemove.add(listener);
        } else {
            listeners.remove(listener);
        }
    }

    /**
     * Notifies all listeners that a ReactionEvent has occurred, and calls each one's function.
     * @param event The event that occurred.
     */
    private static void notifyListeners(ReactionEvent event){
        iterating = true;
        for (ReactionListener listener : listeners){
            listener.onReactionEvent(event);
        }
        iterating = false;
        listeners.removeAll(listenersToRemove);
        listenersToRemove.clear();
    }

    /**
     * Processes a reaction.
     * @param event The reaction event to process.
     */
    public static void handleReaction(ReactionEvent event){
        notifyListeners(event);
    }

}
