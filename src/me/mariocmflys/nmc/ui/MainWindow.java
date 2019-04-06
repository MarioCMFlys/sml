package me.mariocmflys.nmc.ui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import me.mariocmflys.nmc.launcher.TunedProfile;

public class MainWindow {

	JFrame frame;
	JTabbedPane tabbedPane;
	boolean running = false;
	
	HashMap<Integer, TunedProfile> profiles = new HashMap<Integer, TunedProfile>();
	JList<String> listProfiles;
	
	public MainWindow() {
		initialize();
	}
	
	/**
	 * Set visibility of main window
	 * @param v Visible or not
	 */
	public void setVisible(boolean v) {
		frame.setVisible(v);
	}
	
	/**
	 * Destroy the MainWindow frame
	 */
	public void dispose() {
		frame.dispose();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Simplified Minecraft Launcher");
		frame.setBounds(100, 100, 800, 480);
		try {
			frame.setIconImage(ImageIO.read(MainWindow.class.getResource("/icon.ico")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if(running) {
		        	System.out.println("Hiding launcher");
		        	frame.setVisible(false);
		        	//frame.setState(JFrame.ICONIFIED);
		        }
		        else {
		        	frame.setVisible(false);
		        	frame.dispose();
		        	System.exit(0);
		        }
		    }
		});
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		PanelModpacks panelProfiles = new PanelModpacks(this);
		tabbedPane.addTab("Profiles", null, panelProfiles, null);
		
		PanelAccount panelAccount = new PanelAccount(this);
		tabbedPane.addTab("Account", null, panelAccount, null);
		
		JScrollPane scrollPrefs = new JScrollPane();
		tabbedPane.addTab("Settings", null, scrollPrefs, null);
		
		JPanel panelPrefs = new PanelSettings(this);
		scrollPrefs.setViewportView(panelPrefs);
	}
}
