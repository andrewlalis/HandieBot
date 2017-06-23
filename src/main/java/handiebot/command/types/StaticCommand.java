package handiebot.command.types;

/**
 * @author Andrew Lalis
 * Class for commands which require no context, so execute on a global scale.
 */
public abstract class StaticCommand extends Command {

    public StaticCommand(String s) {
        super(s);
    }

    public abstract void execute();

}
