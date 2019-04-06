package me.mariocmflys.nmc.ui;

import java.awt.BorderLayout;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class BrowserDialog extends JFrame {

	private JPanel contentPane;

	/**
	 * Open a dialog box with a TextFrame
	 * @param mime
	 * @param contents
	 */
	public BrowserDialog(String mime, String title, String contents) {
		setTitle(title);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 640, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		JTextPane textPane = new JTextPane();
		textPane.setContentType(mime);
		scrollPane.setViewportView(textPane);
		textPane.setText(contents);
		textPane.select(0, 0);
		textPane.setEditable(false);
		
		scrollPane.getViewport().setViewPosition(new Point(0,0));
	}

}
