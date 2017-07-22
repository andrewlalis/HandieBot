package handiebot.command.types;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;

/**
 * @author Andrew Lalis
 * Interface for objects that require reaction events.
 */
public interface ReactionListener {

    void onReactionEvent(ReactionEvent event);

}
