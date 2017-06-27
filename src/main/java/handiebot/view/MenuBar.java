package handiebot.view;

import handiebot.command.Commands;
import handiebot.view.actions.ActionItem;
import handiebot.view.actions.CommandAction;

import javax.swing.*;

/**
 * @author Andrew Lalis
 * Custom menu bar to be added to the console control panel.
 */
public class MenuBar extends JMenuBar {

    public MenuBar(){
        JMenu fileMenu = new JMenu("File");
            fileMenu.add(new ActionItem("Quit", new CommandAction(Commands.get("quit"))));
            this.add(fileMenu);
    }

}
