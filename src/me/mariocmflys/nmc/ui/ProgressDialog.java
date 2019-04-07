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

	public ProgressDialog(String title, String text, int min, int max, int value) {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 323, 140);
		setTitle(title);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(12, 39, 299, 27);
		progressBar.setMinimum(min);
		progressBar.setMaximum(max);
		progressBar.setValue(value);
		contentPane.add(progressBar);
		
		lblText = new JLabel(text);
		lblText.setBounds(12, 12, 70, 15);
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
