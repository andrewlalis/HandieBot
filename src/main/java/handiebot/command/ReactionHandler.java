package handiebot.command;

import handiebot.command.types.ReactionListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Lalis
 * Class which handles user reactions to songs and performs necessary actions.
 */
public class ReactionHandler {

    private static List<ReactionListener> listeners = new ArrayList<>();
    //Flag to tell if the handler is iterating over the listeners.
    private static boolean iterating = false;
    //Queue of listeners to remove after an iteration.
    private static List<ReactionListener> listenersToRemove = new ArrayList<>();
    //Flag that individual listeners can set to request the message be deleted after processing.
    private static boolean deleteRequested = false;
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
     * Requests that the currently processing message should be deleted after the iteration.
     */
    public static void requestMessageDeletion(){
        deleteRequested = true;
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
        if (deleteRequested) {
            RequestBuffer.request(event.getMessage()::delete);
        }
        deleteRequested = false;
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
