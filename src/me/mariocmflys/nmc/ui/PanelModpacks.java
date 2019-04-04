package me.mariocmflys.nmc.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.json.JSONArray;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import me.mariocmflys.nmc.Instance;
import me.mariocmflys.nmc.launcher.MinecraftLauncher;
import me.mariocmflys.nmc.launcher.Profile;
import me.mariocmflys.nmc.launcher.TunedProfile;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

@SuppressWarnings("serial")
public class PanelModpacks extends JPanel {
	HashMap<Integer, TunedProfile> profiles = new HashMap<Integer, TunedProfile>();
	
	public PanelModpacks(MainWindow mainWindow) {
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);
		
		JScrollPane scrollProfiles = new JScrollPane();
		splitPane.setLeftComponent(scrollProfiles);
		
		JList<String> listProfiles = new JList<String>();
		scrollProfiles.setViewportView(listProfiles);
		
		JPanel panelDetails = new JPanel();
		splitPane.setRightComponent(panelDetails);
		panelDetails.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblName = new JLabel("Name");
		panelDetails.add(lblName, "2, 2");
		
		JLabel lblAuthor = new JLabel("Author");
		panelDetails.add(lblAuthor, "2, 4");
		
		JButton btnLaunch = new JButton("Play");
		btnLaunch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLaunch.setEnabled(false);
				btnLaunch.setText("Already playing!");

				Profile profile = profiles.get(listProfiles.getSelectedIndex()).getProfile();
				
				try {
					File libDir = new File(Instance.getDataDir() + File.separator + "lib");
					File clientDir = new File(Instance.getDataDir() + File.separator + "clients");
					File workDir = new File(Instance.getDataDir() + File.separator + "profile" + File.separator + profile.getID());
					File assetDir = new File(Instance.getDataDir() + File.separator + "assets");
					libDir.mkdirs();
					clientDir.mkdirs();
					workDir.mkdirs();
					assetDir.mkdirs();
					File indexDir = new File(assetDir.getAbsolutePath() + File.separator + "indexes");
					indexDir.mkdirs();
					
					new Thread() {
						public void run() {
							try {
								mainWindow.running = true;
								PanelConsole pc = new PanelConsole();
								mainWindow.tabbedPane.addTab("Game Log", null, pc, null);
								mainWindow.tabbedPane.setSelectedComponent(pc);
								/**pc.write("Regular", null);
								pc.write("Error", PanelConsole.STYLE_ERROR);
								pc.write("Regular once more", null);**/
								
								MinecraftLauncher.launch(profile, Instance.player, libDir, clientDir, workDir, assetDir, indexDir, pc);
							} catch (IOException e) {
								e.printStackTrace();
							}
							btnLaunch.setEnabled(true);
							btnLaunch.setText("Play");
							mainWindow.setVisible(true);
							mainWindow.running = false;
						}
					}.start();
					
					
					
				} catch (Exception e1) {
					System.err.println("Fatal error occured while launching the game");
					e1.printStackTrace();
				}
			}
		});
		add(btnLaunch, BorderLayout.SOUTH);
		
		JSONArray pr = Instance.config.getArray("installed_profiles");
		for(int i = 0; i<pr.toList().size(); i++) {
			try {
				TunedProfile p = new TunedProfile(pr.getJSONObject(i));
				p.attachProfile();
				profiles.put(profiles.size(), p);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		
		listProfiles.setModel(new AbstractListModel<String>() {
			public int getSize() {
				return profiles.size();
			}
			public String getElementAt(int index) {
				return profiles.get(index).getProfile().getDisplayName();
			}
		});
		
		listProfiles.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				TunedProfile tp = profiles.get(listProfiles.getSelectedIndex());
				lblName.setText(tp.getProfile().getDisplayName() + " (" + tp.getProfile().getVersion() + ")");
				lblAuthor.setText("By " + tp.getProfile().getAuthor());
			}
		});
		
		if(profiles.size() >= 0) listProfiles.setSelectedIndex(0);
	}

}
