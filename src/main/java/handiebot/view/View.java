package handiebot.view;

import javax.swing.*;

/**
 * @author Andrew Lalis
 */
public class View {
    public JPanel mainPanel;
    private JTextPane outputArea;
    private JTextField commandField;

    public View(){
        this.commandField.addKeyListener(new CommandLineListener());
    }

   public JTextPane getOutputArea(){
       return this.outputArea;
   }

}
