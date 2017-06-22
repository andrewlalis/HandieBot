package handiebot.command.types;

/**
 * @author Andrew Lalis
 * Basic type of command.
 */
public abstract class Command {

    private String name;

    public Command(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    };

}
