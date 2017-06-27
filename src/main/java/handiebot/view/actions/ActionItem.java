package handiebot.view.actions;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * @author Andrew Lalis
 * Class which creates a JMenuItem and automatically adds a listener.
 */
public class ActionItem extends JMenuItem {

    public ActionItem(String name, ActionListener listener){
        super(name);
        this.addActionListener(listener);
    }

}
