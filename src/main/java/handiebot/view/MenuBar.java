package handiebot.view;

import handiebot.command.Commands;
import handiebot.view.actions.ActionItem;
import handiebot.view.actions.CommandAction;

import javax.swing.*;

import static handiebot.HandieBot.resourceBundle;

/**
 * @author Andrew Lalis
 * Custom menu bar to be added to the console control panel.
 */
public class MenuBar extends JMenuBar {

    public MenuBar(){
        JMenu fileMenu = new JMenu(resourceBundle.getString("menu.filemenu.title"));
            fileMenu.add(new ActionItem(resourceBundle.getString("menu.filemenu.quit"), new CommandAction(Commands.get("quit"))));
            this.add(fileMenu);
    }

}
