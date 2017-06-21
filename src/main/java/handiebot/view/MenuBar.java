package handiebot.view;

import handiebot.view.actions.ActionItem;
import handiebot.view.actions.QuitAction;

import javax.swing.*;

/**
 * @author Andrew Lalis
 * Custom menu bar to be added to the console control panel.
 */
public class MenuBar extends JMenuBar {

    public MenuBar(){
        JMenu fileMenu = new JMenu("File");
            fileMenu.add(new ActionItem("Quit", new QuitAction()));
            this.add(fileMenu);
    }

}
