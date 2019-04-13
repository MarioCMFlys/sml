package me.mariocmflys.nmc.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import me.mariocmflys.nmc.Instance;
import me.mariocmflys.nmc.launcher.MinecraftLauncher;
import me.mariocmflys.nmc.launcher.OutputConsole.Type;
import me.mariocmflys.nmc.launcher.Profile;
import me.mariocmflys.nmc.launcher.TunedProfile;
import java.awt.Component;
import javax.swing.Box;

@SuppressWarnings("serial")
public class PanelModpacks extends JPanel {
	public PanelModpacks(MainWindow mainWindow) {
		setLayout(new BorderLayout(0, 0));
		setBackground(Appearance.color_bg);
		
		JPanel panelMain = new JPanel();
		add(panelMain, BorderLayout.CENTER);
		panelMain.setLayout(new BorderLayout(0, 0));
		
		JPanel panelDetails = new JPanel();
		panelMain.add(panelDetails, BorderLayout.CENTER);
		panelDetails.setBackground(Appearance.color_bg_light);
		panelDetails.setLayout(null);
		
		JLabel lblName = new JLabel("Name");
		lblName.setFont(Appearance.font_regular.deriveFont(24f));
		lblName.setBounds(7, 7, 412, 31);
		panelDetails.add(lblName);
		
		JLabel lblAuthor = new JLabel("Author");
		lblAuthor.setBounds(7, 43, 200, 15);
		panelDetails.add(lblAuthor);
		
		JLabel lblNote = new JLabel("");
		lblNote.setBounds(7, 70, 200, 15);
		lblNote.setForeground(Color.RED);
		panelDetails.add(lblNote);
		
		JPanel panelSettings = new JPanel();
		panelSettings.setBackground(Appearance.color_bg_light);
		panelSettings.setBorder(new TitledBorder(null, "User Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelSettings.setBounds(7, 97, 315, 153);
		panelDetails.add(panelSettings);
		panelSettings.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblMemory = new JLabel("Memory: ");
		panelSettings.add(lblMemory, "4, 4");
		
		JSlider sliderMemory = new JSlider();
		panelSettings.add(sliderMemory, "4, 2");
		sliderMemory.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				lblMemory.setText("Memory: " + sliderMemory.getValue() + "M");
			}
		});
		sliderMemory.setBackground(Appearance.color_bg_light);
		sliderMemory.setForeground(Appearance.color_highlight);
		sliderMemory.setMinorTickSpacing(250);
		sliderMemory.setMajorTickSpacing(1000);
		sliderMemory.setPaintTicks(true);
		sliderMemory.setMaximum(6000);
		sliderMemory.setSnapToTicks(true);
		sliderMemory.setValue(1000);
		
		JButton btnSave = new JButton("Save");
		btnSave.setBackground(Appearance.color_button);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int mem = sliderMemory.getValue();
				TunedProfile tprofile = mainWindow.profiles.get(mainWindow.listProfiles.getSelectedIndex());
				tprofile.setAllocatedMemory(mem);
				
				
				JSONArray prof = Instance.config.getArray("installed_profiles");
				for(int i = 0; i < prof.toList().size(); i++) {
					JSONObject j = prof.getJSONObject(i);
					if(j.getString("id").equals(tprofile.getID())) {
						j.put("memory", mem);
						break;
					}
				}
				Instance.config.remove("installed_profiles");
				Instance.config.set("installed_profiles", prof);
				Instance.config.save();
				JOptionPane.showMessageDialog(mainWindow.frame, "Profile settings saved.", "Profile Management", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		panelSettings.add(btnSave, "4, 6");
		
		mainWindow.listProfiles = new JList<String>();
		
		JButton btnLaunch = new JButton("Play");
		btnLaunch.setBackground(Appearance.color_button);
		btnLaunch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLaunch.setEnabled(false);
				btnLaunch.setText("Already playing!");
				
				TunedProfile tprofile = mainWindow.profiles.get(mainWindow.listProfiles.getSelectedIndex());
				Profile profile = tprofile.getProfile();
				
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
							PanelConsole pc = new PanelConsole();
							mainWindow.running = true;
							mainWindow.tabbedPane.addTab("Game Log", null, pc, null);
							mainWindow.tabbedPane.setSelectedComponent(pc);
							try {
								MinecraftLauncher.launch(tprofile, Instance.player, libDir, clientDir, workDir, assetDir, indexDir, pc);
							} catch (Exception e) {
								pc.write("Failed to launch", Type.ERROR);
								pc.write(e, Type.ERROR);
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
					JOptionPane.showMessageDialog(mainWindow.frame, "Failed to initialize launcher, see log for details", "Launch", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JPanel panelWest = new JPanel();
		add(panelWest, BorderLayout.WEST);
		panelWest.setLayout(new BorderLayout(0, 0));
		
		Component horizontalStrut = Box.createHorizontalStrut(150);
		panelWest.add(horizontalStrut, BorderLayout.NORTH);
		
		JScrollPane scrollProfiles = new JScrollPane();
		panelWest.add(scrollProfiles, BorderLayout.CENTER);
		//scrollProfiles.setViewportView(mainWindow.listProfiles);
		scrollProfiles.setViewportView(mainWindow.listProfiles);
		add(btnLaunch, BorderLayout.SOUTH);
		
		JSONArray pr = Instance.config.getArray("installed_profiles");
		for(int i = 0; i<pr.toList().size(); i++) {
			try {
				TunedProfile p = new TunedProfile(pr.getJSONObject(i));
				p.attachProfile();
				mainWindow.profiles.put(mainWindow.profiles.size(), p);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		
		mainWindow.listProfiles.setBackground(Appearance.color_bg_light);
		mainWindow.listProfiles.setModel(new AbstractListModel<String>() {
			public int getSize() {
				return mainWindow.profiles.size();
			}
			public String getElementAt(int index) {
				return mainWindow.profiles.get(index).getProfile().getDisplayName();
			}
		});
		
		mainWindow.listProfiles.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				TunedProfile tp = mainWindow.profiles.get(mainWindow.listProfiles.getSelectedIndex());
				lblName.setText(tp.getProfile().getDisplayName() + " (" + tp.getProfile().getVersion() + ")");
				lblAuthor.setText("By " + tp.getProfile().getAuthor());
				if(!tp.getInstalledVersion().equals(tp.getProfile().getVersion())) lblNote.setText("Upgrades next launch");
				else if(tp.getSource().equals("")) lblNote.setText("Installed locally");
				else lblNote.setText("");
				
				sliderMemory.setValue(tp.getAllocatedMemory());
				
				panelSettings.setVisible(true);
			}
		});
		
		if(mainWindow.profiles.size() > 0) mainWindow.listProfiles.setSelectedIndex(0);
		else {
			lblName.setText("You have no profiles.");
			lblAuthor.setText("Import a new profile in the Settings tab.");
			
			panelSettings.setVisible(false);
		}
	}
}
