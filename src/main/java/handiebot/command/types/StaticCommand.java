package handiebot.command.types;

/**
 * @author Andrew Lalis
 * Class for commands which require no context, so execute on a global scale.
 */
public abstract class StaticCommand extends Command {

    public StaticCommand(String name, String usage, String description, int permissionsRequired) {
        super(name, usage, description, permissionsRequired);
    }

    public abstract void execute();

}
