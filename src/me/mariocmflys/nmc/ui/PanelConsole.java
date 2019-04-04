package me.mariocmflys.nmc.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import me.mariocmflys.nmc.launcher.OutputConsole;

@SuppressWarnings("serial")
public class PanelConsole extends JPanel implements OutputConsole {
	JTextPane txtConsole;
	public static SimpleAttributeSet STYLE_ERROR = new SimpleAttributeSet();
	public static SimpleAttributeSet STYLE_INIT = new SimpleAttributeSet();
	
	public PanelConsole() {
		StyleConstants.setForeground(STYLE_ERROR, Color.RED);
		StyleConstants.setForeground(STYLE_INIT, Color.BLUE);
		
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		txtConsole = new JTextPane();
		txtConsole.setEditable(false);
		
		scrollPane.setViewportView(txtConsole);
		
	}
	public void write(String text, OutputConsole.Type type) {
		type.getPrintStream().println(text);
		
		SimpleAttributeSet attrib = null;
		
		if(type == OutputConsole.Type.ERROR) attrib = STYLE_ERROR;
		if(type == OutputConsole.Type.INIT) attrib = STYLE_INIT;
		
		StyledDocument sdoc = txtConsole.getStyledDocument();
		try {
			sdoc.insertString(sdoc.getLength(), text + "\n", attrib);
			txtConsole.selectAll();
			int end = txtConsole.getSelectionEnd();
			txtConsole.select(end, end);			
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
	}
}
