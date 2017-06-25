package handiebot.command.types;

/**
 * @author Andrew Lalis
 * Basic type of command.
 */
public abstract class Command {

    private String name;
    private String usage;
    private String description;

    public Command(String name, String usage, String description){
        this.name = name;
        this.usage = usage;
        this.description = description;
    }

    public String getName(){
        return this.name;
    };

    public String getUsage() {
        return this.name+" "+this.usage;
    };

    public String getDescription() {
        return this.description;
    }

}
