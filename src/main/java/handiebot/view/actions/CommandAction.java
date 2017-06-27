package handiebot.view.actions;

import handiebot.command.types.Command;
import handiebot.command.types.StaticCommand;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Andrew Lalis
 */
public class CommandAction implements ActionListener {

    private Command command;

    public CommandAction(Command command){
        this.command = command;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.command instanceof StaticCommand){
            ((StaticCommand) this.command).execute();
        }
    }
}
