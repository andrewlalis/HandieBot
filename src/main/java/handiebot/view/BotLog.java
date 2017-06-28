package handiebot.view;

import sx.blah.discord.handle.obj.IGuild;

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
        ERROR,
        COMMAND
    }

    //Styles for output to the console.
    private Map<TYPE, Style> logStyles;
    private static Map<TYPE, Color> logStyleColors = new HashMap<TYPE, Color>(){{
       put(INFO, new Color(22, 63, 160));
       put(MUSIC, new Color(51, 175, 66));
       put(ERROR, new Color(255, 0, 0));
       put(COMMAND, new Color(255, 123, 0));
    }};

    private Style defaultStyle;

    private JTextPane outputArea;

    public BotLog(JTextPane outputArea){
        this.outputArea = outputArea;
        if (outputArea != null) {
            initStyles();
        }
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
            this.logStyles.get(type).addAttribute(StyleConstants.Foreground, logStyleColors.get(type));
        }
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
        System.out.println(dateFormatted+'['+type.name()+"] "+message);
        if (this.outputArea != null) {
            try {
                this.outputArea.getStyledDocument().insertString(this.outputArea.getStyledDocument().getLength(), dateFormatted, this.defaultStyle);
                this.outputArea.getStyledDocument().insertString(this.outputArea.getStyledDocument().getLength(), '[' + type.name() + "] ", this.logStyles.get(type));
                this.outputArea.getStyledDocument().insertString(this.outputArea.getStyledDocument().getLength(), message + '\n', this.defaultStyle);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes a string to the output window with the given tag, guild name, and text.
     * @param type The type of message to write.
     * @param guild The guild to get the name of.
     * @param message The content of the message.
     */
    public void log(TYPE type, IGuild guild, String message){
        if (guild == null){
            log(type, message);
            return;
        }
        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        String dateFormatted = formatter.format(date);
        System.out.println(dateFormatted+'['+type.name()+"]["+guild.getName()+"] "+message);
        if (this.outputArea != null) {
            try {
                this.outputArea.getStyledDocument().insertString(this.outputArea.getStyledDocument().getLength(), dateFormatted, this.defaultStyle);
                this.outputArea.getStyledDocument().insertString(this.outputArea.getStyledDocument().getLength(), '[' + type.name() + ']', this.logStyles.get(type));
                this.outputArea.getStyledDocument().insertString(this.outputArea.getStyledDocument().getLength(), '[' + guild.getName() + "] ", this.defaultStyle);
                this.outputArea.getStyledDocument().insertString(this.outputArea.getStyledDocument().getLength(), message + '\n', this.defaultStyle);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

}
