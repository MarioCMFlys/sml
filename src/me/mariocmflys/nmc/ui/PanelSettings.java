package me.mariocmflys.nmc.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import me.mariocmflys.jsoncompat.JSONArray;
import me.mariocmflys.jsoncompat.JSONObject;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import me.mariocmflys.nmc.C;
import me.mariocmflys.nmc.Instance;
import me.mariocmflys.nmc.launcher.Profile;
import me.mariocmflys.nmc.launcher.TunedProfile;

@SuppressWarnings("serial")
public class PanelSettings extends JPanel {
	private JTextField txtAddProfile;
	public PanelSettings(MainWindow mainWindow) {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblImportProfile = new JLabel("Import Profile");
		add(lblImportProfile, "2, 2, right, default");
		
		txtAddProfile = new JTextField();
		add(txtAddProfile, "4, 2, fill, default");
		txtAddProfile.setColumns(10);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.setBackground(Appearance.color_button);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String url = txtAddProfile.getText();
				
				for(int i = 0; i < mainWindow.profiles.size(); i++) {
					if(mainWindow.profiles.get(i).getSource().equals(url)) {
						txtAddProfile.setText("");
						JOptionPane.showMessageDialog(mainWindow.frame, "This profile has already been imported.", "Import Profile", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				
				try {
					File tempFile = new File(Instance.getDataDir() + File.separator + "temp_manifest.json");
					FileUtils.copyURLToFile(new URL(url), tempFile);
					Scanner s = new Scanner(tempFile);
					String text = s.useDelimiter("\\Z").next();
					s.close();
					
					Profile p = new Profile(text);
					String id = p.getID();
					
					File newFile = new File(Instance.getDataDir() + File.separator + "profile" + File.separator + id + File.separator + "manifest.json");
					newFile.getParentFile().mkdirs();
					tempFile.renameTo(newFile);
					
					JSONObject tpJson = new JSONObject();
					tpJson.put("id", id);
					tpJson.put("memory", 1000);
					tpJson.put("dist_url", url);
					tpJson.put("version", p.getVersion());
					TunedProfile tp = new TunedProfile(tpJson);
					tp.attachProfile();
					
					JSONArray profiles = Instance.config.getArray("installed_profiles");
					profiles.put(tpJson);
					Instance.config.remove("installed_profiles");
					Instance.config.set("installed_profiles", profiles);
					Instance.config.save();
					
					mainWindow.profiles.put(mainWindow.profiles.size(), tp);
					mainWindow.listProfiles.updateUI();
					txtAddProfile.setText("");
					JOptionPane.showMessageDialog(mainWindow.frame, "Successfully imported profile", "Import Profile", JOptionPane.INFORMATION_MESSAGE);
					
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(mainWindow.frame, "Failed to import profile, see log for details", "Import Profile", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		add(btnAdd, "4, 4");
		
		if(System.getProperty("nmc.mode").equalsIgnoreCase("offline")) {
			txtAddProfile.setEnabled(false);
			btnAdd.setEnabled(false);
		}
		
		ButtonGroup grpCloseAction = new ButtonGroup();
		
		JLabel lblLauncherCloseAction = new JLabel("Close Button:");
		add(lblLauncherCloseAction, "2, 6");
		
		JRadioButton rdbtnDoNothing = new JRadioButton("Does nothing");
		rdbtnDoNothing.setEnabled(false);
		grpCloseAction.add(rdbtnDoNothing);
		add(rdbtnDoNothing, "4, 6");
		
		JRadioButton rdbtnMinimizesLauncher = new JRadioButton("Minimizes Launcher");
		rdbtnMinimizesLauncher.setEnabled(false);
		grpCloseAction.add(rdbtnMinimizesLauncher);
		add(rdbtnMinimizesLauncher, "4, 8");
		
		JRadioButton rdbtnHidesLauncher = new JRadioButton("Hides launcher");
		rdbtnHidesLauncher.setEnabled(false);
		rdbtnHidesLauncher.setSelected(true);
		grpCloseAction.add(rdbtnHidesLauncher);
		add(rdbtnHidesLauncher, "4, 10");
		
		JRadioButton rdbtnExitsLauncher = new JRadioButton("Closes launcher");
		rdbtnExitsLauncher.setEnabled(false);
		grpCloseAction.add(rdbtnExitsLauncher);
		add(rdbtnExitsLauncher, "4, 12");
		
		
		JLabel lblAbout = new JLabel("About");
		add(lblAbout, "2, 14");
		
		JLabel lblAboutVersion = new JLabel("SML version " + C.VERSION.toUpperCase());
		add(lblAboutVersion, "4, 14");
		
		JLabel lblAboutBootstrap = new JLabel(
				(Instance.getBootstrapVersion() != null ? "Bootstrap version " + Instance.getBootstrapVersion().toUpperCase() : "Not using bootstrap"));
		add(lblAboutBootstrap, "4, 16");
		
		JLabel lblAboutCopyright = new JLabel("Copyright 2017-2019 MarioCMFlys");
		add(lblAboutCopyright, "4, 18");
		
		JButton lnkLicense = new JButton();
		lnkLicense.setBackground(Appearance.color_button);
	    lnkLicense.setText("License Information");
	    lnkLicense.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputStream lStream = PanelSettings.class.getResourceAsStream("/LICENSE.txt");
				StringWriter writer = new StringWriter();
				try {
					IOUtils.copy(lStream, writer, "utf-8");
					String license = writer.toString();
					new BrowserDialog("text/plain", "License", license).setVisible(true);
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(mainWindow.frame, "License information available in LICENSE.txt of this Jarfile", "License", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
	    add(lnkLicense, "4, 20");
	}
}
