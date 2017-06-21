package handiebot.view;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static handiebot.view.BotLog.TYPE.*;

/**
 * @author Andrew Lalis
 */
public class BotLog {

    public enum TYPE {
        INFO,
        MUSIC,
        ERROR
    }

    private Map<TYPE, Style> logStyles;
    private Style defaultStyle;

    private JTextPane outputArea;

    public BotLog(JTextPane outputArea){
        this.outputArea = outputArea;
        initStyles();
    }

    /**
     * Initialize the styles for the various log data.
     */
    private void initStyles(){
        this.logStyles = new HashMap<>();
        //Define default style.
        this.defaultStyle = this.outputArea.addStyle("LogStyle", null);
        this.defaultStyle.addAttribute(StyleConstants.FontFamily, "Lucida Console");
        this.defaultStyle.addAttribute(StyleConstants.FontSize, 12);
        //Define each type's color.
        for (TYPE type : TYPE.values()) {
            this.logStyles.put(type, outputArea.addStyle(type.name(), this.defaultStyle));
        }
        this.logStyles.get(INFO).addAttribute(StyleConstants.Foreground, Color.blue);
        this.logStyles.get(MUSIC).addAttribute(StyleConstants.Foreground, new Color(51, 175, 66));
        this.logStyles.get(ERROR).addAttribute(StyleConstants.Foreground, Color.red);
    }

    /**
     * Writes a string to the output window with the given tag and text.
     * @param type The type of message to write.
     * @param message The content of the message.
     */
    public void log(TYPE type, String message){
        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        String dateFormatted = formatter.format(date);
        try {
            this.outputArea.getStyledDocument().insertString(this.outputArea.getStyledDocument().getLength(), dateFormatted, this.defaultStyle);
            this.outputArea.getStyledDocument().insertString(this.outputArea.getStyledDocument().getLength(), '['+type.name()+"] ", this.logStyles.get(type));
            this.outputArea.getStyledDocument().insertString(this.outputArea.getStyledDocument().getLength(), message+'\n', this.defaultStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

}
