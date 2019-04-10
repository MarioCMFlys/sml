package me.mariocmflys.nmc.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ProgressDialog extends JFrame {

	private JPanel contentPane;
	private JProgressBar progressBar;
	private JLabel lblText;

	/**
	 * @wbp.parser.constructor
	 */
	public ProgressDialog(String title, String text, int min, int max, int value) {
		setup();
		setTitle(title);
		
		progressBar.setMinimum(min);
		progressBar.setMaximum(max);
		progressBar.setValue(value);
		
		lblText.setText(text);
		
	}
	
	public ProgressDialog(String title, String text) {
		setup();
		setTitle(title);
		
		progressBar.setIndeterminate(true);
		
		lblText.setText(text);
	}
	
	private void setup() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 323, 140);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(12, 39, 299, 27);
		contentPane.add(progressBar);
		
		lblText = new JLabel("");
		lblText.setBounds(12, 12, 299, 15);
		contentPane.add(lblText);
		
	}
	
	public void updateValue(int value) {
		progressBar.setValue(value);
	}
	
	public void updateLabel(String text) {
		lblText.setText(text);
	}
	
	public void close() {
		setVisible(false);
		dispose();
	}
}
